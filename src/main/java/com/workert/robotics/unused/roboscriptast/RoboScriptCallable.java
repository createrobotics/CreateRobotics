package com.workert.robotics.unused.roboscriptast;
import java.util.List;

interface RoboScriptCallable {
	int expectedArgumentSize();

	Object call(Interpreter interpreter, List<Object> arguments, Token errorToken);
}
