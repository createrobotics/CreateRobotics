package com.workert.robotics.base.roboscript;
public class RoboScriptSuper extends RoboScriptObject {
	final RoboScriptObject subclassObject;

	RoboScriptSuper(RoboScriptClass clazz, RoboScriptObject subclassObject) {
		super(clazz);
		this.settable = false;
		this.subclassObject = subclassObject;
	}
}
