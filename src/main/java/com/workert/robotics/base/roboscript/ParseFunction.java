package com.workert.robotics.base.roboscript;
@FunctionalInterface
public interface ParseFunction {
	void apply(Compiler c, boolean canAssign);
}
