package com.workert.robotics.base.roboscriptast;

public class RoboScriptVariable {
	public final boolean staticc;
	public Object value;

	public RoboScriptVariable(boolean staticc, Object value) {
		this.staticc = staticc;
		this.value = value;
	}
}
