package com.workert.robotics.base.roboscriptbytecode;
import java.util.HashMap;
import java.util.Map;

public class RoboScriptClass {
	RoboScriptClass superclass = null;
	final Map<String, RoboScriptFunction> functions = new HashMap<>();
}
