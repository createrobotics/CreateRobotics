package com.workert.robotics.base.roboscript;
public class RoboScriptNativeFunction implements RoboScriptCallable {
	final byte argumentCount;
	Caller function;

	RoboScriptNativeFunction(byte argumentCount) {
		this.argumentCount = argumentCount;
	}

	@Override
	public void call(VirtualMachine vm, byte argumentCount) {
		if (this.argumentCount != argumentCount)
			throw new RuntimeError("Expected " + this.argumentCount + " argument(s) but got " + argumentCount + ".");
		Object returnValue = this.function.call();
		vm.stack[vm.stackSize - 1] = returnValue;
	}


	@FunctionalInterface
	interface Caller {
		Object call();
	}
}