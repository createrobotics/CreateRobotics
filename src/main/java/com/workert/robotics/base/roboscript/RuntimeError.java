package com.workert.robotics.base.roboscript;
public final class RuntimeError extends RuntimeException {
	public final Token token;

	RuntimeError(Token token, String message) {
		super(message);
		this.token = token;
	}
}
