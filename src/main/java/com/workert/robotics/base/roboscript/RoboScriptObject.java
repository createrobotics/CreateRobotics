package com.workert.robotics.base.roboscript;
import java.util.HashMap;
import java.util.Map;

public class RoboScriptObject {

	RoboScriptClass clazz;
	public Map<String, RoboScriptField> fields = new HashMap<>();

	boolean settable;


	public RoboScriptObject(RoboScriptClass clazz) {
		this.clazz = clazz;
		this.settable = true;
	}
}