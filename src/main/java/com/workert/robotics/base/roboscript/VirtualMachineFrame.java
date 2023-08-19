package com.workert.robotics.base.roboscript;
import java.util.HashMap;
import java.util.Map;

public class VirtualMachineFrame {
	/**
	 * The current chunk being interpreted.
	 */
	Chunk chunk;

	/**
	 * The main stack of the program.
	 */
	final Object[] stack = new Object[256];

	/**
	 * The current stack size
	 */
	int stackSize = 0;

	/**
	 * Variables defined in the global scope; Can be accessed from anywhere.
	 */
	final Object[] globalVariables = new Object[256];

	/**
	 * Signals that can be called externally using a string that
	 */
	final Map<String, Object> signals = new HashMap<>();

	/**
	 * Global functions that are defined natively and use Java code.
	 */
	RoboScript.NativeFunction[] nativeFunctions = new RoboScript.NativeFunction[Short.MAX_VALUE];

	/**
	 * Halts the program when true.
	 */
	boolean stopQueued = false;

	/**
	 * The amount of all Native Functions in the `nativeFunctions` array.
	 */
	int nativeFunctionSize = 0;
}
