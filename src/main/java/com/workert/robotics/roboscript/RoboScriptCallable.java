package com.workert.robotics.roboscript;
import java.util.List;

interface RoboScriptCallable {
	int expectedArgumentSize();

	Object call(Interpreter interpreter, List<Object> arguments);
}
