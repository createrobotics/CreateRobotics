package com.workert.robotics.roboscript;
import java.util.List;
import java.util.Map;

public class RoboScriptClass implements RoboScriptCallable {
	private final RoboScript roboScriptInstance;
	final String name;
	final RoboScriptClass superclass;
	private final Map<String, RoboScriptFunction> methods;

	RoboScriptClass(RoboScript roboScriptInstance, String name, RoboScriptClass superclass, Map<String, RoboScriptFunction> methods) {
		this.roboScriptInstance = roboScriptInstance;
		this.name = name;
		this.superclass = superclass;
		this.methods = methods;
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		RoboScriptClassInstance instance = new RoboScriptClassInstance(this.roboScriptInstance, this);

		RoboScriptFunction initializer = this.findMethod(this.name);
		if (initializer != null) {
			initializer.bind(instance).call(interpreter, arguments);
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
