package com.workert.robotics.base.roboscript;
public abstract class RoboScriptNativeMethod extends RoboScriptNativeFunction {
	/**
	 * Use this for the object you are applying the function to. DO NOT USE FUNCTION ARGUMENTS IN PLACE OF THIS FIELD.
	 */
	final Object instance;

	RoboScriptNativeMethod(Object instance, byte argumentCount) {
		super(argumentCount);
		this.instance = instance;
	}
}
