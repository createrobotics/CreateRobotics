package com.workert.robotics.base.roboscript;
import java.util.HashMap;
import java.util.Map;

public class RoboScriptClass implements RoboScriptCallable {
	RoboScriptClass superclass = null;
	final Map<String, RoboScriptFunction> functions = new HashMap<>();

	@Override
	public void call(VirtualMachine vm, byte argumentCount) {
		RoboScriptObject object = new RoboScriptObject(this);
		RoboScriptMethod initializer;
		if ((initializer = vm.getFunctionInClass(this, object, "init")) != null) {
			vm.stack[vm.stackSize - 1 - argumentCount] = object;
			initializer.call(vm, argumentCount);
		} else {
			if (argumentCount > 0) throw new RuntimeError("Cannot have arguments if 'init' method is not present.");
			vm.stack[vm.stackSize - 1] = object;
		}
	}
}
