package com.workert.robotics.base.roboscriptbytecode;
import java.util.HashMap;
import java.util.Map;

public class RoboScriptObject {

	RoboScriptClass clazz;
	Map<String, Object> fields = new HashMap<>();

	boolean settable = false;


	RoboScriptObject(RoboScriptClass clazz) {
		this.clazz = clazz;
		this.settable = true;
	}
}