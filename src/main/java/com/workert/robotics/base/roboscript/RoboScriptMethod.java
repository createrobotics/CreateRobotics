package com.workert.robotics.base.roboscript;
public class RoboScriptMethod extends RoboScriptFunction {
	RoboScriptObject instance;

	RoboScriptMethod(RoboScriptFunction function, RoboScriptObject instance) {
		super(function.address, function.argumentCount);
		this.instance = instance;
	}
}
