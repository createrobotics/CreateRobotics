package com.workert.robotics.base.roboscriptbytecode;
import java.util.HashMap;
import java.util.Map;

public class RoboScriptObject {

	RoboScriptClass clazz;
	Map<String, Object> fields = new HashMap<>();

	RoboScriptObject(RoboScriptClass clazz) {
		this.clazz = clazz;
	}
}