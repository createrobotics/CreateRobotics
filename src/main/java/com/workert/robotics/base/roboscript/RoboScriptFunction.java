package com.workert.robotics.base.roboscript;
import java.util.List;

public class RoboScriptFunction implements RoboScriptCallable {
	private final Statement.Function declaration;
	private final Environment parent;

	private final boolean isInitializer;

	RoboScriptFunction(Statement.Function declaration, Environment parent, boolean isInitializer) {
		this.isInitializer = isInitializer;
		this.parent = parent;
		this.declaration = declaration;
	}

	RoboScriptFunction bind(RoboScriptClassInstance instance) {
		Environment environment = new Environment(this.parent);
		environment.define("this", instance, false);
		return new RoboScriptFunction(this.declaration, environment, this.isInitializer);
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		Environment environment = new Environment(this.parent);
		for (int i = 0; i < this.declaration.params.size(); i++) {
			environment.define(this.declaration.params.get(i), arguments.get(i), false);
		}

		try {
			interpreter.executeBlock(this.declaration.body, environment);
		} catch (Return returnValue) {
			if (this.isInitializer)
				throw new RuntimeError(this.declaration.name, "Cannot return from class initializer.");
			return returnValue.value;
		}

		if (this.isInitializer) {
			return this.parent.getAt(0, new Token(Token.TokenType.THIS, "this", "this", 0));
		}
		return null;
	}

	@Override
	public int expectedArgumentSize() {
		return this.declaration.params.size();
	}

	@Override
	public String toString() {
		return "<function " + this.declaration.name.lexeme + ">";
	}
}
