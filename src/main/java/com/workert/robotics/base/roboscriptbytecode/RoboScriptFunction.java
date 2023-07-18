package com.workert.robotics.base.roboscriptbytecode;
public class RoboScriptFunction {
	int arity;
	Chunk chunk;

	RoboScriptFunction(int arity, Chunk chunk) {
		this.arity = arity;
		this.chunk = chunk;
	}
}
