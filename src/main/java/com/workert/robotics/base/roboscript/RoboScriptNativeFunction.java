package com.workert.robotics.base.roboscript;
public abstract class RoboScriptNativeFunction implements RoboScriptCallable {
	byte argumentCount = 0;

	/**
	 * Calls a native function and automatically pushes the return value.
	 *
	 * @return The return value of the function (automatically gets pushed, do not worry about that)
	 */
	abstract Object call();

	@Override
	public final void call(VirtualMachine vm, byte argumentCount) {
		if (this.argumentCount != argumentCount) throw new RuntimeError(
				"Expected " + this.argumentCount + " argument(s) but got " + argumentCount + ".");
		vm.stack[vm.stackSize - 1] = this.call();
	}
}
