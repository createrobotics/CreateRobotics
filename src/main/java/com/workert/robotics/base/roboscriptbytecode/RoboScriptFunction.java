package com.workert.robotics.base.roboscriptbytecode;
public class RoboScriptFunction {
	int address;
	int arity;

	RoboScriptFunction(int address, int arity) {
		this.address = address;
		this.arity = arity;
	}

	@Override
	public String toString() {
		return "RoboScriptFunction (adr: " + this.address + ", arity: " + this.arity + ")";
	}
}
