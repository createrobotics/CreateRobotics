package com.workert.robotics.base.roboscriptbytecode;
public class RoboScriptSuper extends RoboScriptObject {
	final RoboScriptObject subclassObject;

	RoboScriptSuper(RoboScriptClass clazz, RoboScriptObject subclassObject) {
		super(clazz);
		this.settable = false;
		this.subclassObject = subclassObject;
	}
}
