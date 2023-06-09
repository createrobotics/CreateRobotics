package com.workert.robotics.base.roboscript;
import java.util.HashMap;
import java.util.Map;

public class Environment {


	public final Environment enclosing;
	private final Map<String, RoboScriptVar> values = new HashMap<>();

	public Environment() {
		this.enclosing = null;
	}

	public Environment(Environment enclosing) {
		this.enclosing = enclosing;
	}

	protected void define(String name, Object value, boolean staticc) {
		this.values.put(name, new RoboScriptVar(staticc, value));
	}

	Environment ancestor(int distance) {
		Environment environment = this;
		for (int i = 0; i < distance; i++) {
			environment = environment.enclosing;
		}

		return environment;
	}

	Object getAt(int distance, Token name) {
		return this.ancestor(distance).values.get(name).value;
	}

	Object get(Token name) {
		if (this.values.containsKey(name.lexeme)) {
			return this.values.get(name.lexeme).value;
		}

		if (this.enclosing != null) return this.enclosing.get(name);

		throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
	}

	void assignAt(int distance, Token name, Object value) {
		this.ancestor(distance).values.put(name.lexeme, new RoboScriptVar(false, value));
	}

	public void assign(Token name, Object value) {
		if (this.values.containsKey(name.lexeme)) {
			this.values.get(name.lexeme).value = value;
			return;
		}

		if (this.enclosing != null) {
			this.enclosing.assign(name, value);
			return;
		}

		throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
	}
}
