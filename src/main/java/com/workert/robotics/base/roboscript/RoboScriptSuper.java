package com.workert.robotics.base.roboscript;
import java.util.List;

public class RoboScriptSuper extends RoboScriptGettable implements RoboScriptCallable {
	private final RoboScriptClassInstance superclass;

	protected RoboScriptSuper(RoboScriptClassInstance superclass) {
		this.superclass = superclass;
	}


	@Override
	public int expectedArgumentSize() {
		return this.superclass.getBaseClass().expectedArgumentSize();
	}

	@Override
	public Object call(Interpreter interpreter, List<Object> arguments) {
		return null;
	}

	@Override
	public Object get(Token name) {
		return null;
	}
}
