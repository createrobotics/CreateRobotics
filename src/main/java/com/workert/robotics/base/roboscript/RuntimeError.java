package com.workert.robotics.base.roboscript;
public final class RuntimeError extends RuntimeException {
	String message;

	public RuntimeError(String message) {
		super(message);
		this.message = message;
	}
}
