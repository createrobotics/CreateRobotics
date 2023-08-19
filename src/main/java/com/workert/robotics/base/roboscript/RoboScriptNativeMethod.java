package com.workert.robotics.base.roboscript;
public abstract class RoboScriptNativeMethod {
	byte argumentCount;

	RoboScriptNativeMethod(byte argumentCount) {
		this.argumentCount = argumentCount;
	}

	abstract Object run();
}