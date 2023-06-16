package com.workert.robotics.base.roboscript;

import java.util.HashMap;
import java.util.Map;

public class RoboScriptClassInstance extends RoboScriptGettable implements RoboScriptSettable {
	private final RoboScriptClass clazz;
	private final Map<String, Object> fields;

	RoboScriptClassInstance(RoboScriptClass clazz) {
		this.clazz = clazz;
		this.fields = new HashMap<>(clazz.fields);
	}

	@Override
	public Object get(Token name) {
		if (name.lexeme.equals(this.clazz.name))
			throw new RuntimeError(name, "Can't re-initialize a class instance.");
		if (this.fields.containsKey(name.lexeme)) {
			return this.fields.get(name.lexeme);
		}

		RoboScriptFunction method = this.clazz.findMethod(name.lexeme);
		if (method != null) return method.bind(this);

		throw new RuntimeError(name,
				"Class '" + this.clazz.name + "' does not contain the field or method '" + name.lexeme + "'.");
	}

	@Override
	public void set(Token name, Object value) {
		if (this.fields.containsKey(name.lexeme))
			this.fields.put(name.lexeme, value);
		else throw new RuntimeError(name,
				"Class '" + this.clazz.name + "' does not contain the field '" + name.lexeme + "'.");
	}

	public RoboScriptClass getBaseClass() {
		return this.clazz;
	}

	@Override
	public String toString() {
		return "<instance of class " + this.clazz.name + ">";
	}
}