package com.workert.robotics.base.roboscriptbytecode;
final class RuntimeError extends RuntimeException {
	String message;

	RuntimeError(String message) {
		super(message);
		this.message = message;
	}
}
