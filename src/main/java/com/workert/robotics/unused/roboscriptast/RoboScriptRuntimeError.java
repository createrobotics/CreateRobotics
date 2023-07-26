package com.workert.robotics.unused.roboscriptast;
public final class RoboScriptRuntimeError extends RuntimeException {
	public final Token token;

	public RoboScriptRuntimeError(Token token, String message) {
		super(message);
		this.token = token;
	}
}
