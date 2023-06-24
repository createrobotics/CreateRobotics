package com.workert.robotics.base.roboscript;
public final class RoboScriptRuntimeError extends RuntimeException {
	public final Token token;

	RoboScriptRuntimeError(Token token, String message) {
		super(message);
		this.token = token;
	}
}
