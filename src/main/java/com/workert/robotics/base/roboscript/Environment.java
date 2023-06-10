package com.workert.robotics.base.roboscript;
import java.util.HashMap;
import java.util.Map;

public class Environment {
	public RoboScript roboScriptInstance;
	public final Environment enclosing;
	private final Map<String, RoboScriptVariable> values = new HashMap<>();

	public Environment(RoboScript roboScriptInstance) {
		this.roboScriptInstance = roboScriptInstance;
		this.enclosing = null;
	}

	public Environment(RoboScript roboScriptInstance, Environment enclosing) {
		this.roboScriptInstance = roboScriptInstance;
		this.enclosing = enclosing;
	}

	protected void define(String name, Object value, boolean staticc) {
		this.values.put(name, new RoboScriptVariable(this.roboScriptInstance, name, staticc, value));
	}

	protected void define(Token name, Object value, boolean staticc) {
		if (this.checkAccessibleVariable(name)) throw new RuntimeError(name,
				"Variable with the name '" + name.lexeme + "' already shares this environment");
		this.values.put(name.lexeme, new RoboScriptVar(staticc, value));
	}

	Environment ancestor(int distance) {
		Environment environment = this;
		for (int i = 0; i < distance; i++) {
			environment = environment.enclosing;
		}

		return environment;
	}

	Object getAt(int distance, Token name) {
		return this.ancestor(distance).values.get(name).getValue();
	}

	Object get(Token name) {
		if (this.values.containsKey(name.lexeme)) {
			return this.values.get(name.lexeme).getValue();
		}

		if (this.enclosing != null) return this.enclosing.get(name);

		throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
	}

	boolean checkAccessibleVariable(Token name) {
		if (this.values.containsKey(name.lexeme)) {
			return true;
		}

		if (this.enclosing != null) return this.enclosing.checkAccessibleVariable(name);

		return false;
	}

	void assignAt(int distance, Token name, Object value) {
		this.ancestor(distance).values.put(name.lexeme,
				new RoboScriptVariable(this.roboScriptInstance, name.lexeme, false, value));
	}

	public void assign(Token name, Object value) {
		if (this.values.containsKey(name.lexeme)) {
			this.values.get(name.lexeme).setValue(value);
			return;
		}

		if (this.enclosing != null) {
			this.enclosing.assign(name, value);
			return;
		}

		throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
	}
}
