package com.workert.robotics.base.roboscript;
public class RoboScriptNativeFunction implements RoboScriptCallable {
	final byte argumentCount;
	public Caller function;

	public RoboScriptNativeFunction(byte argumentCount) {
		this.argumentCount = argumentCount;
	}

	@Override
	public void call(VirtualMachine vm, byte argumentCount, boolean asSignal) {
		if (this.argumentCount != argumentCount)
			throw new RuntimeError("Expected " + this.argumentCount + " argument(s) but got " + argumentCount + ".");
		Object returnValue = this.function.call();
		if (!asSignal)
			vm.stack[vm.stackSize - 1] = returnValue;
		else
			vm.queueStop();
	}


	@FunctionalInterface
	public interface Caller {
		Object call();
	}
}