package com.workert.robotics.base.roboscript;

import java.util.Map;

public class RoboScriptVariable {
	private final RoboScript roboScriptInstance;
	public final boolean staticc;
	public final String name;
	private Object value;

	RoboScriptVariable(RoboScript roboScriptInstance, String name, boolean staticc, Object value) {
		this.roboScriptInstance = roboScriptInstance;
		this.name = name;
		this.staticc = staticc;
		this.value = value;
	}

	public Object getValue() {
		if (this.staticc)
			this.roboScriptInstance.getExternallySavedVariables().get(this.name);
		else
			return this.value;
		return null;
	}

	public void setValue(Object value) {
		if (this.staticc)
			this.roboScriptInstance.saveVariableExternally(Map.entry(this.name, value));
		else
			this.value = value;
	}
}
