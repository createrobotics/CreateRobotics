package com.workert.robotics.base.roboscriptbytecode;
public abstract class RoboScript {
	VirtualMachine vm = new VirtualMachine(this);
	int nativeFunctions = 0;
	private boolean hadError = false;

	public final void runString(String source) {
		this.hadError = false;
		Scanner s = new Scanner(source);
		System.out.println(s.scanTokens());
		Compiler c = new Compiler(this);
		this.defineNativeFunctions(c);
		c.compile(source);
		if (this.hadError) {
			return;
		}
		this.vm.interpret(c.chunk);
	}


	void defineNativeFunctions(Compiler c) {
		// print(arg)
		this.defineNativeFunction("print", 1, new NativeFunction() {
			@Override
			public Object call(VirtualMachine vm) {
				RoboScript.this.handlePrintMessage(VirtualMachine.stringify(vm.popStack()));
				return null;
			}
		}, c);

		// str(arg)
		this.defineNativeFunction("str", 1, new NativeFunction() {
			@Override
			Object call(VirtualMachine vm) {
				return VirtualMachine.stringify(vm.popStack());
			}
		}, c);

		// sin(num)
		this.defineNativeFunction("sin", 1, new NativeFunction() {
			@Override
			Object call(VirtualMachine vm) {
				try {
					double num = (double) vm.popStack();
					return Math.sin(num);
				} catch (ClassCastException e) {
					throw new RuntimeError("Function 'sin' requires a number as its only argument.");
				}
			}
		}, c);

		// cos(num)
		this.defineNativeFunction("cos", 1, new NativeFunction() {
			@Override
			Object call(VirtualMachine vm) {
				try {
					double num = (double) vm.popStack();
					return Math.cos(num);
				} catch (ClassCastException e) {
					throw new RuntimeError("Function 'cos' requires a number as its only argument.");
				}
			}
		}, c);

		// tan(num)
		this.defineNativeFunction("tan", 1, new NativeFunction() {
			@Override
			Object call(VirtualMachine vm) {
				try {
					double num = (double) vm.popStack();
					return Math.tan(num);
				} catch (ClassCastException e) {
					throw new RuntimeError("Function 'tan' requires a number as its only argument.");
				}
			}
		}, c);

		// asin(num)
		this.defineNativeFunction("asin", 1, new NativeFunction() {
			@Override
			Object call(VirtualMachine vm) {
				try {
					double num = (double) vm.popStack();
					return Math.asin(num);
				} catch (ClassCastException e) {
					throw new RuntimeError("Function 'asin' requires a number as its only argument.");
				}
			}
		}, c);

		// acos(num)
		this.defineNativeFunction("acos", 1, new NativeFunction() {
			@Override
			Object call(VirtualMachine vm) {
				try {
					double num = (double) vm.popStack();
					return Math.acos(num);
				} catch (ClassCastException e) {
					throw new RuntimeError("Function 'acos' requires a number as its only argument.");
				}
			}
		}, c);

		// atan(num)
		this.defineNativeFunction("atan", 1, new NativeFunction() {
			@Override
			Object call(VirtualMachine vm) {
				try {
					double num = (double) vm.popStack();
					return Math.atan(num);
				} catch (ClassCastException e) {
					throw new RuntimeError("Function 'atan' requires a number as its only argument.");
				}
			}
		}, c);

		// abs(num)
		this.defineNativeFunction("abs", 1, new NativeFunction() {
			@Override
			Object call(VirtualMachine vm) {
				try {
					double num = (double) vm.popStack();
					return Math.abs(num);
				} catch (ClassCastException e) {
					throw new RuntimeError("Function 'abs' requires a number as its only argument.");
				}
			}
		}, c);

		// floor(num)
		this.defineNativeFunction("floor", 1, new NativeFunction() {
			@Override
			Object call(VirtualMachine vm) {
				try {
					double num = (double) vm.popStack();
					return Math.floor(num);
				} catch (ClassCastException e) {
					throw new RuntimeError("Function 'floor' requires a number as its only argument.");
				}
			}
		}, c);

		// ceil(num)
		this.defineNativeFunction("ceil", 1, new NativeFunction() {
			@Override
			Object call(VirtualMachine vm) {
				try {
					double num = (double) vm.popStack();
					return Math.ceil(num);
				} catch (ClassCastException e) {
					throw new RuntimeError("Function 'ceil' requires a number as its only argument.");
				}
			}
		}, c);
	}

	public final void defineNativeFunction(String name, int argumentCount, NativeFunction function, Compiler c) {
		function.argumentCount = argumentCount;
		this.vm.nativeFunctions[this.nativeFunctions++] = function;
		c.nativeFunctionLookup.put(name, (byte) (this.nativeFunctions - 1));
	}

	protected final void reportCompileError(int line, String message) {
		this.hadError = true;
		this.handleErrorMessage("[line " + line + "] Error: " + message);
	}


	protected abstract void handlePrintMessage(String message);

	protected abstract void handleErrorMessage(String message);


	abstract static class NativeFunction {
		int argumentCount = 0;

		/**
		 * Calls a native function and automatically pushes the return value.
		 *
		 * @param vm The current virtual machine of the executing function.
		 * @return The return value of the function (automatically gets pushed, do not worry about that)
		 */
		abstract Object call(VirtualMachine vm);
	}
}
