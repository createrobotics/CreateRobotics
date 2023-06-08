package com.workert.robotics.roboscript;
import java.util.List;

public class RoboScriptFunction implements RoboScriptCallable {
	private final Statement.Function declaration;
	private final Environment closure; // parent

	private final boolean isInitializer;

	RoboScriptFunction(Statement.Function declaration, Environment parent, boolean isInitializer) {
		this.isInitializer = isInitializer;
		this.closure = parent;
		this.declaration = declaration;
	}

	RoboScriptFunction bind(RoboScriptClassInstance instance) {
		Environment environment = new Environment(this.closure);
		environment.define("this", instance);
		return new RoboScriptFunction(this.declaration, environment, this.isInitializer);
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		Environment environment = new Environment(this.closure);
		for (int i = 0; i < this.declaration.params.size(); i++) {
			environment.define(this.declaration.params.get(i).lexeme, arguments.get(i));
		}

		try {
			interpreter.executeBlock(this.declaration.body, environment);
		} catch (Return returnValue) {
			return returnValue.value;
		}

		if (this.isInitializer) return this.closure.getAt(0, new Token(Token.TokenType.THIS, "this", "this", 0));
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
