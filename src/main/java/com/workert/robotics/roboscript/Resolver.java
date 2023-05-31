package com.workert.robotics.roboscript;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
	private final Interpreter interpreter;
	private final Stack<Map<String, Boolean>> scopes = new Stack<>();
	private FunctionType currentFunction = FunctionType.NONE;
	private ClassType currentClass = ClassType.NONE;

	Resolver(Interpreter interpreter) {
		this.interpreter = interpreter;
	}

	private enum FunctionType {
		NONE,
		FUNCTION,
		INITIALIZER,
		METHOD
	}

	private enum ClassType {
		NONE,
		CLASS,
		SUBCLASS
	}

	void resolve(List<Stmt> statements) {
		for (Stmt statement : statements) {
			this.resolve(statement);
		}
	}

	private void resolve(Stmt stmt) {
		stmt.accept(this);
	}

	private void resolve(Expr expr) {
		expr.accept(this);
	}

	private void resolveFunction(Stmt.Function function, FunctionType type) {
		FunctionType enclosingFunction = this.currentFunction;
		this.currentFunction = type;

		this.beginScope();
		for (Token param : function.params) {
			this.declare(param);
			this.define(param);
		}
		this.resolve(function.body);
		this.endScope();

		this.currentFunction = enclosingFunction;
	}

	private void beginScope() {
		this.scopes.push(new HashMap<>());
	}

	private void endScope() {
		this.scopes.pop();
	}

	private void declare(Token name) {
		if (this.scopes.isEmpty()) return;

		Map<String, Boolean> scope = this.scopes.peek();

		if (scope.containsKey(name.lexeme)) {
			this.interpreter.roboScriptInstance.error(name, "Variable with this name in this scope already exists.");
		}

		scope.put(name.lexeme, false);
	}

	private void define(Token name) {
		if (this.scopes.isEmpty()) return;
		this.scopes.peek().put(name.lexeme, true);
	}

	private void resolveLocal(Expr expr, Token name) {
		for (int i = this.scopes.size() - 1; i >= 0; i--) {
			if (this.scopes.get(i).containsKey(name.lexeme)) {
				this.interpreter.resolve(expr, this.scopes.size() - 1 - i);
				return;
			}
		}
	}

	@Override
	public Void visitBlockStmt(Stmt.Block stmt) {
		this.beginScope();
		this.resolve(stmt.statements);
		this.endScope();
		return null;
	}

	@Override
	public Void visitClassStmt(Stmt.Class stmt) {
		ClassType enclosingClass = this.currentClass;
		this.currentClass = ClassType.CLASS;

		this.declare(stmt.name);
		this.define(stmt.name);

		if (stmt.superclass != null) {
			if (stmt.name.lexeme.equals(stmt.superclass.name.lexeme))
				this.interpreter.roboScriptInstance.error(stmt.superclass.name, "A class can't extend itself.");
			this.currentClass = ClassType.SUBCLASS;
			this.resolve(stmt.superclass);
		}

		if (stmt.superclass != null) {
			this.beginScope();
			this.scopes.peek().put("super", true);
		}

		this.beginScope();
		this.scopes.peek().put("this", true);

		for (Stmt.Function method : stmt.methods) {
			FunctionType declaration = FunctionType.METHOD;
			if (method.name.lexeme.equals(stmt.name.lexeme)) declaration = FunctionType.INITIALIZER;

			this.resolveFunction(method, declaration);
		}

		this.endScope();

		if (stmt.superclass != null) this.endScope();

		this.currentClass = enclosingClass;
		return null;
	}

	@Override
	public Void visitExpressionStmt(Stmt.Expression stmt) {
		this.resolve(stmt.expression);
		return null;
	}


	@Override
	public Void visitFunctionStmt(Stmt.Function stmt) {
		this.declare(stmt.name);
		this.define(stmt.name);

		this.resolveFunction(stmt, FunctionType.FUNCTION);
		return null;
	}

	@Override
	public Void visitIfStmt(Stmt.If stmt) {
		this.resolve(stmt.condition);
		this.resolve(stmt.thenBranch);
		if (stmt.elseBranch != null) this.resolve(stmt.elseBranch);
		return null;
	}

	@Override
	public Void visitReturnStmt(Stmt.Return stmt) {
		if (this.currentFunction == FunctionType.NONE) {
			this.interpreter.roboScriptInstance.error(stmt.keyword, "Can't return from top-level scope.");
		}

		if (stmt.value != null) {
			if (this.currentFunction == FunctionType.INITIALIZER) {
				this.interpreter.roboScriptInstance.error(stmt.keyword, "An initializer can't return a value.");
			}
			this.resolve(stmt.value);
		}

		return null;
	}

	@Override
	public Void visitBreakStmt(Stmt.Break stmt) {
		if (this.currentFunction == FunctionType.NONE || this.currentFunction == FunctionType.INITIALIZER) {
			this.interpreter.roboScriptInstance.error(stmt.keyword, "Can't break from top-level scope.");
		}

		return null;
	}

	@Override
	public Void visitVarStmt(Stmt.Var stmt) {
		this.declare(stmt.name);
		if (stmt.initializer != null) {
			this.resolve(stmt.initializer);
		}
		this.define(stmt.name);
		return null;
	}

	@Override
	public Void visitWhileStmt(Stmt.While stmt) {
		this.resolve(stmt.condition);
		this.resolve(stmt.body);
		return null;
	}

	@Override
	public Void visitVariableExpr(Expr.Variable expr) {
		if (!this.scopes.isEmpty() && this.scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
			this.interpreter.roboScriptInstance.error(expr.name, "Can't read local variable in its own initializer.");
		}
		this.resolveLocal(expr, expr.name);
		return null;
	}

	@Override
	public Void visitAssignExpr(Expr.Assign expr) {
		this.resolve(expr.value);
		this.resolveLocal(expr, expr.name);
		return null;
	}

	@Override
	public Void visitBinaryExpr(Expr.Binary expr) {
		this.resolve(expr.left);
		this.resolve(expr.right);
		return null;
	}

	@Override
	public Void visitCallExpr(Expr.Call expr) {
		this.resolve(expr.callee);

		for (Expr argument : expr.arguments) {
			this.resolve(argument);
		}

		return null;
	}

	@Override
	public Void visitGetExpr(Expr.Get expr) {
		this.resolve(expr.object);
		return null;
	}

	@Override
	public Void visitSetExpr(Expr.Set expr) {
		this.resolve(expr.value);
		this.resolve(expr.object);
		return null;
	}

	@Override
	public Void visitSuperExpr(Expr.Super expr) {
		if (this.currentClass == ClassType.NONE) {
			this.interpreter.roboScriptInstance.error(expr.keyword, "Can't use 'super' outside of a class.");
		} else if (this.currentClass != ClassType.SUBCLASS) {
			this.interpreter.roboScriptInstance.error(expr.keyword, "Can't use 'super' in a class with no superclass.");
		}
		this.resolveLocal(expr, expr.keyword);
		return null;
	}

	@Override
	public Void visitThisExpr(Expr.This expr) {
		if (this.currentClass == ClassType.NONE) {
			this.interpreter.roboScriptInstance.error(expr.keyword, "Can't use 'this' outside of a class.");
			return null;
		}

		this.resolveLocal(expr, expr.keyword);
		return null;
	}

	@Override
	public Void visitGroupingExpr(Expr.Grouping expr) {
		this.resolve(expr.expression);
		return null;
	}

	@Override
	public Void visitLiteralExpr(Expr.Literal expr) {
		return null;
	}

	@Override
	public Void visitLogicalExpr(Expr.Logical expr) {
		this.resolve(expr.left);
		this.resolve(expr.right);
		return null;
	}

	@Override
	public Void visitUnaryExpr(Expr.Unary expr) {
		this.resolve(expr.right);
		return null;
	}
}
