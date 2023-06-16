package com.workert.robotics.base.roboscript;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoboScriptClass implements RoboScriptCallable {
	final String name;
	final RoboScriptClass superclass;
	protected final Map<String, Object> variableMap;
	private final RoboScriptFunction initializer;

	RoboScriptClass(String name, RoboScriptClass superclass, Map<String, Object> variableMap, RoboScriptFunction initializer) {
		this.name = name;
		this.superclass = superclass;
		this.variableMap = variableMap;
		this.initializer = initializer;
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		RoboScriptClassInstance instance = new RoboScriptClassInstance(this,
				this.getFieldsWithoutMethods());
		if (this.initializer != null) {
			this.initializer.bind(instance).call(interpreter, arguments);
		}
		return instance;
	}

	private Map<String, Object> getFieldsWithoutMethods() {
		Map<String, Object> fields = new HashMap<>();
		if (this.superclass != null)
			for (Map.Entry<String, Object> variable : this.superclass.getFieldsWithoutMethods().entrySet()) {
				fields.put(variable.getKey(), variable.getValue());
			}
		for (Map.Entry<String, Object> variable : this.variableMap.entrySet()) {
			if (!(variable instanceof RoboScriptFunction)) {
				fields.put(variable.getKey(), variable.getValue());
			}
		}

		return fields;
	}


	protected Object findVariable(String name) {
		if (this.variableMap.containsKey(name)) {
			return this.variableMap.get(name);
		}
		if (this.superclass != null) {
			return this.superclass.findVariable(name);
		}
		return null;
	}

	protected RoboScriptFunction findMethod(String name) {
		Object variable = this.findVariable(name);
		if (variable == null || !(variable instanceof RoboScriptFunction function)) return null;
		return function;
	}

	@Override
	public int expectedArgumentSize() {
		if (this.initializer != null)
			return this.initializer.expectedArgumentSize();
		return 0;
	}

	@Override
	public String toString() {
		return "<class " + this.name + ">";
	}
}