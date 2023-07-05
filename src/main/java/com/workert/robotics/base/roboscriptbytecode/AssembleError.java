package com.workert.robotics.base.roboscriptbytecode;
final class AssembleError extends RuntimeException {
	protected String message;
	protected int line;

	protected AssembleError(String message, int line) {
		super(message);
		this.message = message;
		this.line = line;

	}
}
