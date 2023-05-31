package com.workert.robotics.roboscript;
public class RuntimeError extends RuntimeException {
	public final Token token;

	RuntimeError(Token token, String message) {
		super(message);
		this.token = token;
	}
}
