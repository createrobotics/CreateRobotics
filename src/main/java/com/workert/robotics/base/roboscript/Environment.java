package com.workert.robotics.base.roboscript;
import java.util.HashMap;
import java.util.Map;

public final class Environment {
	final Environment enclosing;
	final Map<String, RoboScriptVariable> variableMap = new HashMap<>();

	public Environment() {
		this.enclosing = null;
	}

	public Environment(Environment enclosing) {
		this.enclosing = enclosing;
	}

	void define(String name, Object value, boolean staticc) {
		this.variableMap.put(name, new RoboScriptVariable(staticc, value));
	}

	void define(Token name, Object value, boolean staticc) {
		if (this.checkAccessibleVariable(name)) throw new RuntimeError(name,
				"Variable with the name '" + name.lexeme + "' already shares this environment");
		this.variableMap.put(name.lexeme, new RoboScriptVariable(staticc, value));
	}

	Environment ancestor(int distance) {
		Environment environment = this;
		for (int i = 0; i < distance; i++) {
			environment = environment.enclosing;
		}

		return environment;
	}

	Object getAt(int distance, Token name) {
		return this.ancestor(distance).variableMap.get(name.lexeme).value;
	}

	Object get(Token name) {
		if (this.variableMap.containsKey(name.lexeme)) {
			return this.variableMap.get(name.lexeme).value;
		}

		if (this.enclosing != null) return this.enclosing.get(name);

		throw new RuntimeError(name, "Undefined variable or function '" + name.lexeme + "'.");
	}

	boolean checkAccessibleVariable(Token name) {
		if (this.variableMap.containsKey(name.lexeme)) {
			return true;
		}

		if (this.enclosing != null) return this.enclosing.checkAccessibleVariable(name);

		return false;
	}

	void assignAt(int distance, Token name, Object value) {
		this.ancestor(distance).variableMap.put(name.lexeme,
				new RoboScriptVariable(false, value));
	}

	public void assign(Token name, Object value) {
		if (this.variableMap.containsKey(name.lexeme)) {
			this.variableMap.get(name.lexeme).value = value;
			return;
		}

		if (this.enclosing != null) {
			this.enclosing.assign(name, value);
			return;
		}

		throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
	}
}
