package com.workert.robotics.base.roboscriptbytecode;
import java.util.Map;

public class RoboScriptObject {
	Map<String, Object> fields;

	RoboScriptObject(Map<String, Object> fields) {
		this.fields = fields;
	}
}
