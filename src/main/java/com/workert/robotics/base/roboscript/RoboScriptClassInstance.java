package com.workert.robotics.base.roboscript;

import java.util.Map;

public class RoboScriptClassInstance extends RoboScriptGettable implements RoboScriptSettable {
	private final RoboScriptClass clazz;
	private final Map<String, Object> fields;

	RoboScriptClassInstance(RoboScriptClass clazz, Map<String, Object> fields) {
		this.clazz = clazz;
		this.fields = fields;
	}

	@Override
	public Object get(Token name) {
		if (name.lexeme.equals(this.clazz.name))
			throw new RuntimeError(name, "Cannot re-initialize a class instance.");

		if (this.fields.containsKey(name.lexeme)) {
			return this.fields.get(name.lexeme);
		}

		RoboScriptFunction method = this.clazz.findMethod(name.lexeme);
		if (method != null) return method.bind(this);


		throw new RuntimeError(name, "Class '" + this.clazz.name + "' does not contain field '" + name.lexeme + "'.");
	}

	@Override
	public void set(Token name, Object value) {
		if (this.fields.containsKey(name.lexeme)) {
			this.fields.put(name.lexeme, value);
		} else {
			throw new RuntimeError(name,
					"Class '" + this.clazz.name + "' does not contain field '" + name.lexeme + "'.");
		}

	}

	public RoboScriptClass getBaseClass() {
		return this.clazz;
	}

	@Override
	public String toString() {
		return "<instance of class " + this.clazz.name + ">";
	}
}