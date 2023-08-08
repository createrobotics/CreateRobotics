package com.workert.robotics.base.roboscript;
final class RuntimeError extends RuntimeException {
	String message;

	RuntimeError(String message) {
		super(message);
		this.message = message;
	}
}
