package com.workert.robotics.base.roboscript;
public class RoboScriptNativeMethod extends RoboScriptNativeFunction {

	/**
	 * Use this to get the object you are applying the function to. DO NOT USE FUNCTION ARGUMENTS IN PLACE OF THIS FIELD.
	 */
	public final Object parentClassInstance;

	public RoboScriptNativeMethod(Object parentClassInstance, byte argumentCount) {
		super(argumentCount);
		this.parentClassInstance = parentClassInstance;
	}

	public RoboScriptNativeMethod(byte argumentCount) {
		super(argumentCount);
		this.parentClassInstance = null;
	}

	public RoboScriptNativeMethod(RoboScriptNativeFunction function, Object parentClassInstance) {
		super(function.argumentCount);
		this.function = function.function;
		this.parentClassInstance = parentClassInstance;
	}

	public Object getParentClassInstance() {
		return this.parentClassInstance;
	}
}

