package com.workert.robotics.base.roboscript;
import java.util.HashMap;
import java.util.Map;

final class Environment {
	final Environment enclosing;
	final Map<String, RoboScriptVariable> variableMap = new HashMap<>();

	Environment() {
		this.enclosing = null;
	}

	Environment(Environment enclosing) {
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

	private Environment getAncestor(int distance) {
		Environment environment = this;
		for (int i = 0; i < distance; i++) {
			environment = environment.enclosing;
		}

		return environment;
	}

	Object getVariableAt(int distance, Token name) {
		return this.getAncestor(distance).getVariable(name);
	}

	/**
	 * <b>Do not use this method except when not otherwise possible!</b>
	 * <p>
	 * If you're in the {@link Interpreter} class use {@link Interpreter#lookUpVariable} instead!
	 */
	Object getVariable(Token name) {
		if (this.variableMap.containsKey(name.lexeme)) {
			return this.variableMap.get(name.lexeme).value;
		}

		if (this.enclosing != null) return this.enclosing.getVariable(name);

		throw new RuntimeError(name, "Undefined variable or function '" + name.lexeme + "'.");
	}

	private boolean checkAccessibleVariable(Token name) {
		if (this.variableMap.containsKey(name.lexeme)) {
			return true;
		}

		if (this.enclosing != null) return this.enclosing.checkAccessibleVariable(name);

		return false;
	}

	void assignAt(int distance, Token name, Object value) {
		this.getAncestor(distance).variableMap.put(name.lexeme,
				new RoboScriptVariable(false, value));
	}

	void assign(Token name, Object value) {
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
