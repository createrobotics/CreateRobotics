package com.workert.robotics.base.roboscriptast;
import java.util.Map;

public abstract class RoboScriptGettable {

	public abstract Object get(Token name);

	final void defineFunction(String name, int expectedArgumentSize, RoboScriptCallableFunction function, Map<String, Object> fields) {
		fields.put(name, RoboScript.defineCallable(name, expectedArgumentSize, function));
	}
}
