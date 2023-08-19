package com.workert.robotics.base.roboscript;
public class RoboScriptFunction implements RoboScriptCallable {
	int address;
	int argumentCount;

	RoboScriptFunction(int address, int argumentCount) {
		this.address = address;
		this.argumentCount = argumentCount;
	}

	@Override
	public void call(VirtualMachine vm, byte argumentCount) {
		if (this.argumentCount != argumentCount)
			throw new RuntimeError("Expected " + this.argumentCount + " argument(s) but got " + argumentCount + ".");
		vm.pushStack(vm.ip);
		vm.pushStack(vm.bp);
		vm.ip = this.address;
		vm.bp = vm.stackSize - argumentCount - 2;
	}
}
