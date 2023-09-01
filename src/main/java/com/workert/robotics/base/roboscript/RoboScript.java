package com.workert.robotics.base.roboscript;

import javax.annotation.Nonnull;

public abstract class RoboScript {
	Compiler compiler = new Compiler(this);
	VirtualMachine virtualMachine = new VirtualMachine(this);

	private boolean hadError = false;

	public final void runString(String source) {
		new Thread(() -> {
			this.hadError = false;
			this.compiler = new Compiler(this);
			this.virtualMachine = new VirtualMachine(this);
			this.defineNativeFunctions();
			this.compiler.compile(source);
			if (this.hadError) {
				return;
			}
			this.virtualMachine.interpret(this.compiler.chunk);
		}).start();

	}

	public final void queueStopForProgram() {
		this.virtualMachine.queueStop();
	}

	public final void queueSignal(RoboScriptCallable c, Object[] args) {
		if (c == null) return;
		this.virtualMachine.addSignalToQueue(new VirtualMachine.ExecutingSignal(c, args));
	}


	protected void defineNativeFunctions() {
		this.defineNativeFunction("print", 1, args -> {
			RoboScript.this.handlePrintMessage(RoboScriptHelper.stringify(args[0]));
			return null;
		});

		this.defineNativeFunction("toString", 1, args -> RoboScriptHelper.stringify(args[0]));
		this.defineNativeFunction("toNumber", 1,
				args -> Double.parseDouble(RoboScriptHelper.asNonEmptyString(args[0])));
		this.defineNativeFunction("sleep", 1, args -> {
			try {
				Thread.sleep(
						(long) (RoboScriptHelper.asDouble(args[0]) * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		});

		this.defineNativeFunction("sin", 1, args -> Math.sin(RoboScriptHelper.asDouble(args[0])));
		this.defineNativeFunction("cos", 1, args -> Math.cos(RoboScriptHelper.asDouble(args[0])));
		this.defineNativeFunction("tan", 1, args -> Math.tan(RoboScriptHelper.asDouble(args[0])));
		this.defineNativeFunction("asin", 1, args -> Math.asin(RoboScriptHelper.asDouble(args[0])));
		this.defineNativeFunction("acos", 1, args -> Math.acos(RoboScriptHelper.asDouble(args[0])));
		this.defineNativeFunction("atan", 1, args -> Math.atan(RoboScriptHelper.asDouble(args[0])));
		this.defineNativeFunction("abs", 1, args -> Math.abs(RoboScriptHelper.asDouble(args[0])));
		this.defineNativeFunction("floor", 1, args -> Math.floor(RoboScriptHelper.asDouble(args[0])));
		this.defineNativeFunction("ceil", 1, args -> Math.ceil(RoboScriptHelper.asDouble(args[0])));
	}

	public final void defineNativeFunction(String name, int argumentCount, NativeFunctionFunctionalInterface function) {
		RoboScriptNativeFunction nativeFunctionWrapper = new RoboScriptNativeFunction((byte) argumentCount);
		nativeFunctionWrapper.function = (vm, fun) -> {
			Object functionOutput;

			if (nativeFunctionWrapper.argumentCount > 0) {
				Object[] functionArgs = new Object[nativeFunctionWrapper.argumentCount];
				for (int i = nativeFunctionWrapper.argumentCount - 1; i >= 0; i--) {
					functionArgs[i] = RoboScript.this.virtualMachine.popStack();
				}

				functionOutput = function.call(functionArgs);
			} else functionOutput = function.call(new Object[] {});
			return RoboScriptHelper.prepareForRoboScriptUse(functionOutput);
		};
		this.virtualMachine.nativeFunctions[this.virtualMachine.nativeFunctionSize] = nativeFunctionWrapper;
		this.compiler.nativeFunctionLookup.put(name, (byte) this.virtualMachine.nativeFunctionSize++);
	}

	@FunctionalInterface
	public interface NativeFunctionFunctionalInterface {
		Object call(@Nonnull Object[] parameters);
	}

	protected final void reportCompileError(int line, String message) {
		this.hadError = true;
		this.handleErrorMessage("[line " + line + "] Error: " + message);
	}


	protected abstract void handlePrintMessage(String message);

	protected abstract void handleErrorMessage(String error);
}
