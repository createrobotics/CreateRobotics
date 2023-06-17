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
		if (this.initializer != null) {
			this.initializer.bind(instance).call(interpreter, arguments);
		}
		return instance;
	}

	protected Object callSuperInitializer(Interpreter interpreter, List<Object> arguments, RoboScriptClassInstance instance) {
		RoboScriptClassInstance superInstance = new RoboScriptClassInstance(this.superclass);
		this.superclass.initializer.bind(instance).call(interpreter, arguments);
		return superInstance;
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
		if (this.initializer != null) return this.initializer.expectedArgumentSize();
		return 0;
	}

	@Override
	public String toString() {
		return "<class " + this.name + ">";
	}
}