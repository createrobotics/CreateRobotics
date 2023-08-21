package com.workert.robotics.base.roboscript;
public class RoboScriptNativeMethod extends RoboScriptNativeFunction {

	/**
	 * Use this for the object you are applying the function to. DO NOT USE FUNCTION ARGUMENTS IN PLACE OF THIS FIELD.
	 */
	public final Object instance;

	public RoboScriptNativeMethod(Object instance, byte argumentCount) {
		super(argumentCount);
		this.instance = instance;
	}

	public RoboScriptNativeMethod(byte argumentCount) {
		super(argumentCount);
		this.instance = null;
	}

	public RoboScriptNativeMethod(RoboScriptNativeFunction function, Object instance) {
		super(function.argumentCount);
		this.function = function.function;
		this.instance = instance;
	}

	public Object getInstance() {
		return this.instance;
	}
}

