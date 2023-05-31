package com.workert.robotics.roboscript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
	public final RoboScript roboScriptInstance;
	final Environment globals = new Environment();
	private Environment environment = this.globals;
	private final Map<Expr, Integer> locals = new HashMap<>();

	public Interpreter(RoboScript roboScriptInstance) {
		this.roboScriptInstance = roboScriptInstance;
	}

	public void interpret(List<Stmt> statements) {
		try {
			for (Stmt statement : statements) {
				this.execute(statement);
			}
		} catch (RuntimeError error) {
			this.roboScriptInstance.runtimeError(error);
		}
	}

	private void execute(Stmt stmt) {
		stmt.accept(this);
	}

	void resolve(Expr expr, int depth) {
		this.locals.put(expr, depth);
	}

	@Override
	public Void visitBlockStmt(Stmt.Block stmt) {
		this.executeBlock(stmt.statements, new Environment(this.environment));
		return null;
	}

	@Override
	public Void visitClassStmt(Stmt.Class stmt) {
		Object superclass = null;
		if (stmt.superclass != null) {
			superclass = this.evaluate(stmt.superclass);
			if (!(superclass instanceof RoboScriptClass)) {
				throw new RuntimeError(stmt.superclass.name, "Superclass must be a class.");
			}
		}

		this.environment.define(stmt.name.lexeme, null);

		if (stmt.superclass != null) {
			this.environment = new Environment(this.environment);
			this.environment.define("super", superclass);
		}

		Map<String, RoboScriptFunction> methods = new HashMap<>();
		for (Stmt.Function method : stmt.methods) {
			RoboScriptFunction function = new RoboScriptFunction(method, this.environment,
					method.name.lexeme.equals(stmt.name.lexeme));
			methods.put(method.name.lexeme, function);
		}

		RoboScriptClass clazz = new RoboScriptClass(this.roboScriptInstance, stmt.name.lexeme,
				(RoboScriptClass) superclass, methods);

		if (superclass != null) {
			this.environment = this.environment.enclosing;
		}

		this.environment.assign(stmt.name, clazz);
		return null;
	}

	@Override
	public Object visitGetExpr(Expr.Get expr) {
		Object object = this.evaluate(expr.object);
		if (object instanceof RoboScriptClassInstance) {
			return ((RoboScriptClassInstance) object).get(expr.name);
		}

		throw new RuntimeError(expr.name, "Only class instances have properties.");
	}

	@Override
	public Object visitSetExpr(Expr.Set expr) {
		Object object = this.evaluate(expr.object);

		if (!(object instanceof RoboScriptClassInstance)) {
			throw new RuntimeError(expr.name, "Only class instances have fields.");
		}
		Object value = this.evaluate(expr.value);
		((RoboScriptClassInstance) object).set(expr.name, value);
		return value;
	}

	@Override
	public Object visitSuperExpr(Expr.Super expr) {
		int distance = this.locals.get(expr);
		RoboScriptClass superclass = (RoboScriptClass) this.environment.getAt(distance,
				new Token(Token.TokenType.SUPER, "super", "super", expr.keyword.line));

		RoboScriptClassInstance instance = (RoboScriptClassInstance) this.environment.getAt(distance - 1,
				new Token(Token.TokenType.THIS, "this", "this", expr.keyword.line));

		RoboScriptFunction method = superclass.findMethod(expr.method.lexeme);
		return method.bind(instance);
	}

	@Override
	public Object visitThisExpr(Expr.This expr) {
		return this.lookUpVariable(expr.keyword, expr);
	}

	void executeBlock(List<Stmt> statements, Environment environment) {
		Environment previous = this.environment;
		try {
			this.environment = environment;

			for (Stmt statement : statements) {
				this.execute(statement);
			}
		} finally {
			this.environment = previous;
		}
	}

	@Override
	public Object visitLiteralExpr(Expr.Literal expr) {
		return expr.value;
	}

	@Override
	public Object visitLogicalExpr(Expr.Logical expr) {
		Object left = this.evaluate(expr.left);

		if (expr.operator.type == Token.TokenType.OR) {
			if (this.isTruthy(left)) return left;
		} else {
			if (!this.isTruthy(left)) return left;
		}

		return this.evaluate(expr.right);
	}

	@Override
	public Object visitGroupingExpr(Expr.Grouping expr) {
		return this.evaluate(expr.expression);
	}

	private Object evaluate(Expr expr) {
		return expr.accept(this);
	}

	@Override
	public Void visitExpressionStmt(Stmt.Expression stmt) {
		this.evaluate(stmt.expression);
		return null;
	}

	@Override
	public Void visitFunctionStmt(Stmt.Function stmt) {
		RoboScriptFunction function = new RoboScriptFunction(stmt, this.environment, false);
		this.environment.define(stmt.name.lexeme, function);
		return null;
	}

	@Override
	public Void visitIfStmt(Stmt.If stmt) {
		if (this.isTruthy(this.evaluate(stmt.condition))) {
			this.execute(stmt.thenBranch);
		} else if (stmt.elseBranch != null) {
			this.execute(stmt.elseBranch);
		}
		return null;
	}

	@Override
	public Void visitReturnStmt(Stmt.Return stmt) {
		Object value = null;
		if (stmt.value != null) value = this.evaluate(stmt.value);

		throw new Return(value);
	}

	@Override
	public Void visitBreakStmt(Stmt.Break stmt) {
		throw new Break();
	}

	@Override
	public Void visitVarStmt(Stmt.Var stmt) {
		Object value = null;
		if (stmt.initializer != null) {
			value = this.evaluate(stmt.initializer);
		}

		this.environment.define(stmt.name.lexeme, value);
		return null;
	}

	@Override
	public Void visitWhileStmt(Stmt.While stmt) {
		try {
			while (this.isTruthy(this.evaluate(stmt.condition))) {
				this.execute(stmt.body);
			}
		} catch (Break breakValue) {
		}
		return null;
	}

	@Override
	public Object visitAssignExpr(Expr.Assign expr) {
		Object value = this.evaluate(expr.value);

		Integer distance = this.locals.get(expr);
		if (distance != null) {
			this.environment.assignAt(distance, expr.name, value);
		} else {
			this.globals.assign(expr.name, value);
		}

		return value;
	}

	@Override
	public Object visitBinaryExpr(Expr.Binary expr) {
		Object left = this.evaluate(expr.left);
		Object right = this.evaluate(expr.right);

		switch (expr.operator.type) {
			case PLUS -> {
				if (left instanceof Double && right instanceof Double) {
					return (double) left + (double) right;
				} else if (left instanceof String && right instanceof String) {
					return (String) left + (String) right;
				} else {
					throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
				}
			}

			case MINUS -> {
				this.checkNumberOperands(expr.operator, left, right);
				return (double) left - (double) right;
			}
			case SLASH -> {
				this.checkNumberOperands(expr.operator, left, right);
				if (((Double) right).equals(0d)) throw new RuntimeError(expr.operator, "Can't divide by zero.");
				return (double) left / (double) right;
			}
			case STAR -> {
				this.checkNumberOperands(expr.operator, left, right);
				return (double) left * (double) right;
			}

			case GREATER -> {
				this.checkNumberOperands(expr.operator, left, right);
				return (double) left > (double) right;
			}
			case GREATER_EQUAL -> {
				this.checkNumberOperands(expr.operator, left, right);
				return (double) left >= (double) right;
			}
			case LESS -> {
				this.checkNumberOperands(expr.operator, left, right);
				return (double) left < (double) right;
			}
			case LESS_EQUAL -> {
				this.checkNumberOperands(expr.operator, left, right);
				return (double) left <= (double) right;
			}

			case BANG_EQUAL -> {
				return !this.isEqual(left, right);
			}
			case EQUAL_EQUAL -> {
				return this.isEqual(left, right);
			}
		}

		// Unreachable.
		return null;
	}

	@Override
	public Object visitCallExpr(Expr.Call expr) {
		Object callee = this.evaluate(expr.callee);

		List<Object> arguments = new ArrayList<>();
		for (Expr argument : expr.arguments) {
			arguments.add(this.evaluate(argument));
		}

		if (!(callee instanceof RoboScriptCallable)) {
			throw new RuntimeError(expr.paren, "Can only call functions and classes.");
		}
		RoboScriptCallable function = (RoboScriptCallable) callee;

		if (arguments.size() != function.expectedArgumentSize()) {
			throw new RuntimeError(expr.paren,
					"Expected " + function.expectedArgumentSize() + " arguments but got " + arguments.size() + ".");
		}

		return function.call(this, arguments);
	}

	@Override
	public Object visitUnaryExpr(Expr.Unary expr) {
		Object right = this.evaluate(expr.right);

		return switch (expr.operator.type) {
			case BANG -> !this.isTruthy(right);
			case MINUS -> -(double) right;
			default ->
				// Unreachable.
					null;
		};
	}

	@Override
	public Object visitVariableExpr(Expr.Variable expr) {
		return this.lookUpVariable(expr.name, expr);
	}

	private Object lookUpVariable(Token name, Expr expr) {
		Integer distance = this.locals.get(expr);
		if (distance != null) {
			return this.environment.getAt(distance, name);
		} else {
			return this.globals.get(name);
		}
	}

	private void checkNumberOperand(Token operator, Object operand) {
		if (operand instanceof Double) return;
		throw new RuntimeError(operator, "Operand must be a number.");
	}

	private void checkNumberOperands(Token operator, Object left, Object right) {
		this.checkNumberOperand(operator, left);
		this.checkNumberOperand(operator, right);
	}

	private boolean isTruthy(Object object) {
		if (object == null) return false;
		if (object instanceof Boolean) return (boolean) object;
		return true;
	}

	private boolean isEqual(Object a, Object b) {
		if (a == null && b == null) return true;
		if (a == null) return false;
		return a.equals(b);
	}

	private String stringify(Object object) {
		if (object == null) return "null";

		if (object instanceof Double) {
			String text = object.toString();
			if (text.endsWith(".0")) {
				text = text.substring(0, text.length() - 2);
			}
			return text;
		}

		return object.toString();
	}
}
