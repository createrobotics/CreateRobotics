package com.workert.robotics.base.roboscript;
import java.util.List;
import java.util.Map;

public class RoboScriptClass implements RoboScriptCallable {
	final String name;
	final RoboScriptClass superclass;
	private final Map<String, RoboScriptFunction> methods;
	protected final Map<String, Object> fields;
	private final RoboScriptFunction initializer;

	RoboScriptClass(String name, RoboScriptClass superclass, Map<String, RoboScriptFunction> methods, Map<String, Object> fields, RoboScriptFunction initializer) {
		this.name = name;
		this.superclass = superclass;
		this.methods = methods;
		this.fields = fields;
		this.initializer = initializer;
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		RoboScriptClassInstance instance = new RoboScriptClassInstance(this);
		instance.register();
		if (this.initializer != null) {
			this.initializer.bind(instance).call(interpreter, arguments);
		}
		return instance;
	}

	RoboScriptFunction findMethod(String name) {
		if (this.methods.containsKey(name)) {
			return this.methods.get(name);
		}

		if (this.superclass != null) {
			return this.superclass.findMethod(name);
		}

		return null;
	}

	@Override
	public int expectedArgumentSize() {
		RoboScriptFunction initializer = this.findMethod(this.name);
		if (initializer == null) return 0;
		return initializer.expectedArgumentSize();
	}

	@Override
	public String toString() {
		return "<class " + this.name + ">";
	}
}