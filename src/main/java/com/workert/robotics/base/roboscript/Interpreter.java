package com.workert.robotics.base.roboscript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Interpreter implements Expression.Visitor<Object>, Statement.Visitor<Void> {
	final RoboScript roboScriptInstance;
	Environment environment = new Environment();
	private final Map<Expression, Integer> locals = new HashMap<>();

	private boolean stopRequested = false;

	Interpreter(RoboScript roboScriptInstance) {
		this.roboScriptInstance = roboScriptInstance;
	}

	void requestStop() {
		this.stopRequested = true;
	}

	void interpret(List<Statement> statements) {
		try {
			for (Statement statement : statements) {
				if (this.stopRequested) {
					this.stopRequested = false;
					break;
				}
				this.execute(statement);
			}
		} catch (RuntimeError error) {
			this.roboScriptInstance.runtimeError(error);
		}
	}

	void reset() {
		this.stopRequested = false;
		this.locals.clear();
		this.environment = new Environment();
		this.roboScriptInstance.defineDefaultFunctions();
	}

	private void execute(Statement statement) {
		statement.accept(this);
	}

	private void execute(Statement statement, Environment environment) {
		this.executeBlock(List.of(statement), environment);
	}

	void resolve(Expression expression, int depth) {
		this.locals.put(expression, depth);
	}


	public Map<String, RoboScriptVariable> getValues() {
		return this.environment.variableMap;
	}

	@Override
	public Void visitBlockStmt(Statement.Block stmt) {
		this.executeBlock(stmt.statements, new Environment(this.environment));
		return null;
	}

	@Override
	public Void visitClassStmt(Statement.Class stmt) {
		Object superclass = null;
		if (stmt.superclass != null) {
			superclass = this.evaluate(stmt.superclass);
			if (!(superclass instanceof RoboScriptClass)) {
				throw new RuntimeError(stmt.superclass.name, "Superclass must be a class.");
			}
		}

		this.environment.define(stmt.name, null, false);

		if (stmt.superclass != null) {
			this.environment = new Environment(this.environment);
			this.environment.define("super", superclass, false);
		}


		Map<String, RoboScriptFunction> methods = new HashMap<>();
		for (Statement.Function method : stmt.methods) {
			RoboScriptFunction function = new RoboScriptFunction(method, this.environment,
					method.name.lexeme.equals(stmt.name.lexeme));
			methods.put(method.name.lexeme, function);
		}


		Map<String, Object> fields = (superclass != null) ? new HashMap<>(
				((RoboScriptClass) superclass).fields) : new HashMap<>();
		for (Statement.Var var : stmt.fields) {
			if (fields.containsKey(var.name.lexeme)) throw new RuntimeError(var.name,
					"Class already contains the field '" + var.name.lexeme + "' in either a superclass or itself");
			Object value = null;
			if (var.initializer != null) {
				value = this.evaluate(var.initializer);
			}
			fields.put(var.name.lexeme, value);
		}


		RoboScriptFunction initializer = new RoboScriptFunction(
				new Statement.Function(stmt.name, new ArrayList<>(), new ArrayList<>()), this.environment, true);
		if (stmt.initializer != null) initializer = new RoboScriptFunction(stmt.initializer, this.environment, true);


		RoboScriptClass clazz = new RoboScriptClass(stmt.name.lexeme,
				(RoboScriptClass) superclass, methods, fields, initializer);

		if (superclass != null) {
			this.environment = this.environment.enclosing;
		}

		this.environment.assign(stmt.name, clazz);
		return null;
	}

	@Override
	public Object visitGetExpr(Expression.Get expr) {
		Object object = this.evaluate(expr.object);
		if (object instanceof RoboScriptGettable gettable) {
			return gettable.get(expr.name);
		}
		if (object instanceof String s) {
			return StringDefaultFunctionHelper.get(s, expr.name);
		}


		throw new RuntimeError(expr.name, "Only classes, arrays, and strings have properties.");
	}

	@Override
	public Object visitSetExpr(Expression.Set expr) {
		Object object = this.evaluate(expr.object);

		if (!(object instanceof RoboScriptClassInstance)) {
			throw new RuntimeError(expr.name, "Only class instances have fields.");
		}
		Object value = this.evaluate(expr.value);
		((RoboScriptClassInstance) object).set(expr.name, value);
		return value;
	}

	@Override
	public Object visitSuperExpr(Expression.Super expr) {
		int distance = this.locals.get(expr);
		RoboScriptClass superclass = (RoboScriptClass) this.environment.getAt(distance,
				new Token(Token.TokenType.SUPER, "super", "super", expr.keyword.line));

		RoboScriptClassInstance instance = (RoboScriptClassInstance) this.environment.getAt(distance - 1,
				new Token(Token.TokenType.THIS, "this", "this", expr.keyword.line));

		if (expr.method != null) {
			RoboScriptFunction method = superclass.findMethod(expr.method.lexeme);
			return method.bind(instance);
		} else { //you should not be able to have a super expr with no arguments or no method, should be tested though.
			List<Object> arguments = new ArrayList<>();
			for (Expression argument : expr.arguments) {
				arguments.add(this.evaluate(argument));
			}
			return instance.getBaseClass().callSuperInitializer(this, arguments, instance);
		}

	}

	@Override
	public Object visitThisExpr(Expression.This expr) {
		return this.lookUpVariable(expr.keyword, expr);
	}

	void executeBlock(List<Statement> statements, Environment environment) {
		Environment previous = this.environment;
		try {
			this.environment = environment;

			for (Statement statement : statements) {
				this.execute(statement);
			}
		} finally {
			this.environment = previous;
		}
	}

	@Override
	public Object visitLiteralExpr(Expression.Literal expr) {
		return expr.value;
	}

	@Override
	public Object visitLogicalExpr(Expression.Logical expr) {
		Object left = this.evaluate(expr.left);

		if (expr.operator.type == Token.TokenType.OR) {
			if (this.isTruthy(left)) return left;
		} else {
			if (!this.isTruthy(left)) return left;
		}

		return this.evaluate(expr.right);
	}

	@Override
	public Object visitGroupingExpr(Expression.Grouping expr) {
		return this.evaluate(expr.expression);
	}

	private Object evaluate(Expression expression) {
		return expression.accept(this);
	}

	@Override
	public Void visitExpressionStmt(Statement.Expression stmt) {
		this.evaluate(stmt.expression);
		return null;
	}

	@Override
	public Void visitFunctionStmt(Statement.Function stmt) {
		RoboScriptFunction function = new RoboScriptFunction(stmt, this.environment, false);
		this.environment.define(stmt.name, function, false);
		return null;
	}

	@Override
	public Void visitIfStmt(Statement.If stmt) {
		if (this.isTruthy(this.evaluate(stmt.condition))) {
			this.execute(stmt.thenBranch);
		} else if (stmt.elseBranch != null) {
			this.execute(stmt.elseBranch);
		}
		return null;
	}

	@Override
	public Void visitReturnStmt(Statement.Return stmt) {
		Object value = null;
		if (stmt.value != null) value = this.evaluate(stmt.value);

		throw new Return(value);
	}

	@Override
	public Void visitBreakStmt(Statement.Break stmt) {
		throw new Break();
	}

	@Override
	public Void visitVarStmt(Statement.Var stmt) {
		Object value = null;
		if (stmt.initializer != null) {
			value = this.evaluate(stmt.initializer);
		}

		this.environment.define(stmt.name, value, stmt.staticc);
		return null;
	}

	@Override
	public Void visitWhileStmt(Statement.While stmt) {
		try {
			while (this.isTruthy(this.evaluate(stmt.condition))) {
				if (this.stopRequested) {
					this.stopRequested = false;
					break;
				}
				this.execute(stmt.body);
			}
		} catch (Break breakValue) {
		}
		return null;
	}

	@Override
	public Void visitForeachStmt(Statement.Foreach stmt) {
		Object iterator = this.evaluate(stmt.right);
		Environment environment = new Environment(this.environment);
		if (iterator instanceof Double d) {
			environment.define(stmt.variable, 0.0d, false);
			for (double i = 0; i < d; i++) {
				if (this.stopRequested) {
					this.stopRequested = false;
					break;
				}
				environment.assign(stmt.variable, i);
				this.execute(stmt.body, environment);
			}
		} else if (iterator instanceof RoboScriptArray array) {
			environment.define(stmt.variable, null, false);
			for (Object i : array.elements) {
				if (this.stopRequested) {
					this.stopRequested = false;
					break;
				}
				environment.assign(stmt.variable, i);
				this.execute(stmt.body, environment);
			}
		}

		return null;
	}

	@Override
	public Object visitAssignExpr(Expression.Assign expr) {
		Object value = this.evaluate(expr.value);

		Integer distance = this.locals.get(expr);
		if (distance != null) {
			this.environment.assignAt(distance, expr.name, value);
		} else {
			this.environment.assign(expr.name, value);
		}

		return value;
	}

	@Override
	public Object visitInstanceExpr(Expression.Instance expr) {
		Object value = this.evaluate(expr.left);
		return switch (expr.right.type) {
			case STRING -> value instanceof String;
			case DOUBLE -> value instanceof Double;
			case BOOLEAN -> value instanceof Boolean;
			case ARRAY -> value instanceof RoboScriptArray;
			case OBJECT -> value instanceof RoboScriptVariable || value instanceof RoboScriptFunction || value instanceof RoboScriptClassInstance;
			case IDENTIFIER -> {
				if (this.environment.get(expr.right) instanceof RoboScriptClass clazz) {
					if (value instanceof RoboScriptClassInstance classInstance)
						yield isInstanceOfClass(clazz, classInstance);
					else
						yield false;
				} else
					throw new RuntimeError(expr.right, "No valid class name.");
			}

			default -> throw new IllegalArgumentException();
		};
	}

	private static boolean isInstanceOfClass(RoboScriptClass clazz, RoboScriptClassInstance instance) {
		return isInstanceOfClass(clazz, instance.getBaseClass());
	}

	private static boolean isInstanceOfClass(RoboScriptClass clazz, RoboScriptClass sameOrExtendingClass) {
		if (clazz.equals(sameOrExtendingClass)) {
			return true;
		} else if (sameOrExtendingClass.superclass != null) {
			return isInstanceOfClass(clazz, sameOrExtendingClass.superclass);
		} else {
			return false;
		}
	}

	@Override
	public Object visitBinaryExpr(Expression.Binary expr) {
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
			case PLUS_PLUS -> {
				this.checkNumberOperand(expr.operator, left);
				return (double) left + 1.0d;
			}

			case MINUS -> {
				this.checkNumberOperands(expr.operator, left, right);
				return (double) left - (double) right;
			}
			case MINUS_MINUS -> {
				this.checkNumberOperand(expr.operator, left);
				return (double) left - 1.0d;
			}
			case SLASH -> {
				this.checkNumberOperands(expr.operator, left, right);
				if (right.equals(0d)) throw new RuntimeError(expr.operator, "Can't divide by zero.");
				return (double) left / (double) right;
			}
			case STAR -> {
				if (left instanceof RoboScriptArray array) {
					this.checkNumberOperand(expr.operator, right);
					List<Object> newList = new ArrayList<>();
					for (int i = 0; i < (double) right; i++) {
						newList.addAll(array.elements);
					}
					array.elements.clear();
					array.elements.addAll(newList);
					return array;
				}
				this.checkNumberOperands(expr.operator, left, right);
				return (double) left * (double) right;
			}

			case PERCENT -> {
				this.checkNumberOperands(expr.operator, left, right);
				return (double) left % (double) right;
			}

			case CARET -> {
				this.checkNumberOperands(expr.operator, left, right);
				return Math.pow((double) left, (double) right);
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

			default -> throw new IllegalArgumentException();
		}
	}

	@Override
	public Object visitCallExpr(Expression.Call expr) {
		Object callee = this.evaluate(expr.callee);

		List<Object> arguments = new ArrayList<>();
		for (Expression argument : expr.arguments) {
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
	public Object visitUnaryExpr(Expression.Unary expr) {
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
	public Object visitVariableExpr(Expression.Variable expr) {
		return this.lookUpVariable(expr.name, expr);
	}

	@Override
	public Object visitArrayExpr(Expression.Array expr) {
		return new RoboScriptArray(expr.elements.stream().map(this::evaluate).collect(Collectors.toList()));
	}

	@Override
	public Object visitIndexGetExpr(Expression.IndexGet expr) {
		Object array = this.evaluate(expr.array);
		Object index = this.evaluate(expr.index);

		if (!(index instanceof Double d))
			throw new RuntimeError(expr.bracket, "Index must be a positive whole number.");
		if (!(array instanceof RoboScriptArray arrayObject))
			throw new RuntimeError(expr.bracket, "Object is not an array");

		return arrayObject.get(d, expr.bracket);
	}

	@Override
	public Object visitIndexSetExpr(Expression.IndexSet expr) {
		Object array = this.evaluate(expr.array);

		Object index = this.evaluate(expr.index);
		Object value = this.evaluate(expr.value);

		if (!(index instanceof Double d))
			throw new RuntimeError(expr.bracket, "Index must be a positive whole number.");
		if (!(array instanceof RoboScriptArray arrayObject))
			throw new RuntimeError(expr.bracket, "Object is not an array.");

		arrayObject.set(d, value, expr.bracket);
		return null;
	}

	private Object lookUpVariable(Token name, Expression expression) {
		Integer distance = this.locals.get(expression);
		if (distance != null) {
			return this.environment.getAt(distance, name);
		} else {
			return this.environment.get(name);
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

	public static String stringify(Object object) {
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
