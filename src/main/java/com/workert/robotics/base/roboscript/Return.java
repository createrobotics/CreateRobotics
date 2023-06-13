package com.workert.robotics.base.roboscript;
public final class Return extends RuntimeException {
	final Object value;

	Return(Object value) {
		super(null, null, false, false);
		this.value = value;
	}
}
