package com.workert.robotics.base.roboscript;
import java.util.HashMap;
import java.util.Map;

public class RoboScriptClass implements RoboScriptCallable {
	RoboScriptClass superclass = null;
	public final Map<String, RoboScriptCallable> functions = new HashMap<>();

	@Override
	public void call(VirtualMachine vm, byte argumentCount, boolean asSignal) {
		RoboScriptObject object = new RoboScriptObject(this);
		RoboScriptCallable initializer;
		if ((initializer = vm.getFunctionInClass(this, object, "init")) != null) {
			vm.stack[vm.stackSize - 1 - argumentCount] = object;
			initializer.call(vm, argumentCount, false);
		} else {
			if (argumentCount > 0) throw new RuntimeError("Cannot have arguments if 'init' method is not present.");
			vm.stack[vm.stackSize - 1] = object;
		}
	}
}
