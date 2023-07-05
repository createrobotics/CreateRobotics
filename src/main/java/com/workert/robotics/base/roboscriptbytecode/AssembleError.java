package com.workert.robotics.base.roboscriptbytecode;
public class AssembleError extends RuntimeException {
	protected String message;
	protected int line;

	protected AssembleError(String message, int line) {
		this.message = message;
		this.line = line;
	}
}
