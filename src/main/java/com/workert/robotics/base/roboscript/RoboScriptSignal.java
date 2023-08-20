package com.workert.robotics.base.roboscript;
public class RoboScriptSignal {
	public RoboScriptCallable callable = null;

	void connect(RoboScriptCallable callable) {
		this.callable = callable;
	}
}
