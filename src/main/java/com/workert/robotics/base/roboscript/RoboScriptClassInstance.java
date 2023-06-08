package com.workert.robotics.base.roboscript;

import java.util.HashMap;
import java.util.Map;

public class RoboScriptClassInstance {
	private final RoboScript roboScriptInstance;
	private final RoboScriptClass clazz;
	private final Map<String, Object> fields = new HashMap<>();

	RoboScriptClassInstance(RoboScript roboScriptInstance, RoboScriptClass clazz) {
		this.roboScriptInstance = roboScriptInstance;
		this.clazz = clazz;
	}

	Object get(Token name) {
		if (name.lexeme.equals(this.clazz.name))
			this.roboScriptInstance.runtimeError(new RuntimeError(name, "Can't re-initialize a class instance."));

		if (this.fields.containsKey(name.lexeme)) {
			return this.fields.get(name.lexeme);
		}

		RoboScriptFunction method = this.clazz.findMethod(name.lexeme);
		if (method != null) return method.bind(this);

		throw new RuntimeError(name, "Undefined property '" + name.lexeme + "'.");
	}

	void set(Token name, Object value) {
		this.fields.put(name.lexeme, value);
	}

	@Override
	public String toString() {
		return "<instance of class " + this.clazz.name + ">";
	}
}