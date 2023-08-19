package com.workert.robotics.base.roboscript;
public abstract class RoboScriptNativeFunction implements RoboScriptCallable {
	final byte argumentCount;

	RoboScriptNativeFunction(byte argumentCount) {
		this.argumentCount = argumentCount;
	}

	@Override
	public final void call(VirtualMachine vm, byte argumentCount) {
		if (this.argumentCount != argumentCount)
			throw new RuntimeError("Expected " + this.argumentCount + " argument(s) but got " + argumentCount + ".");
		vm.stack[vm.stackSize - 1] = this.call();
	}

	abstract Object call();
}