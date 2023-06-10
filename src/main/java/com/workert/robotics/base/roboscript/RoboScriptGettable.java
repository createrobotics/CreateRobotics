package com.workert.robotics.base.roboscript;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class RoboScriptGettable {

	public abstract Object get(Token name);

	public final void defineFunction(String name, int expectedArgumentSize, BiFunction<Interpreter, List<Object>, Object> function, Map<String, Object> fields) {
		fields.put(name, new RoboScriptCallable() {
			@Override
			public int expectedArgumentSize() {
				return expectedArgumentSize;
			}

			@Override
			public Object call(Interpreter interpreter, List<Object> arguments) {
				return function.apply(interpreter, arguments);
			}

			@Override
			public String toString() {
				return "<native function " + name + ">";
			}
		});
	}
}
