package com.workert.robotics.base.roboscript;
public class RoboScriptFunction implements RoboScriptCallable {
	int address;
	byte argumentCount;
	boolean runningAsSignal = false;

	RoboScriptFunction(int address, byte argumentCount) {
		this.address = address;
		this.argumentCount = argumentCount;
	}

	@Override
	public void call(VirtualMachine vm, byte argumentCount, boolean asSignal) {
		if (this.argumentCount != argumentCount)
			throw new RuntimeError("Expected " + this.argumentCount + " argument(s) but got " + argumentCount + ".");

		this.runningAsSignal = asSignal;
		if (!vm.inSignal && asSignal) {
			vm.inSignal = true;
		}
		if (vm.running) {
			vm.pushStack(vm.ip);
			vm.pushStack(vm.bp);
		} else {
			vm.pushStack(-1);
			vm.pushStack(-1);
		}

		vm.ip = this.address;
		vm.bp = vm.stackSize - argumentCount - 2;
	}
}
