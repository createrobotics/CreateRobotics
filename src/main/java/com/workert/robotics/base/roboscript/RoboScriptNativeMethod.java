package com.workert.robotics.base.roboscript;
public abstract class RoboScriptNativeMethod<T> {
	T object;
	byte argumentCount;

	RoboScriptNativeMethod(T object, byte argumentCount) {
		this.object = object;
		this.argumentCount = argumentCount;
	}

	abstract Object run();
}