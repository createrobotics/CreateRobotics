package com.workert.robotics.base.roboscript;
import java.util.List;

interface RoboScriptCallable {
	int expectedArgumentSize();

	Object call(Interpreter interpreter, List<Object> arguments);
}
