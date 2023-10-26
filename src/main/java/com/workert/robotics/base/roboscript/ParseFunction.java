package com.workert.robotics.base.roboscript;
@FunctionalInterface
public interface ParseFunction {
	void apply(LegacyCompiler c, boolean canAssign);
}
