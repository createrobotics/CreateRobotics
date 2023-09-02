package com.workert.robotics.base.roboscript;
public class RoboScriptNativeMethod<C> extends RoboScriptNativeFunction {

	/**
	 * Use this to get the object you are applying the function to. DO NOT USE FUNCTION ARGUMENTS IN PLACE OF THIS FIELD.
	 */
	public final C instance;

	public RoboScriptNativeMethod(C instance, byte argumentCount) {
		super(argumentCount);
		this.parentClassInstance = parentClassInstance;
	}

	public RoboScriptNativeMethod(byte argumentCount) {
		super(argumentCount);
		this.parentClassInstance = null;
	}

	public RoboScriptNativeMethod(RoboScriptNativeFunction function, C instance) {
		super(function.argumentCount);
		this.function = function.function;
		this.parentClassInstance = parentClassInstance;
	}
}

