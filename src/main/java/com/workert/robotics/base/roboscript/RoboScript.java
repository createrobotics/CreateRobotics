package com.workert.robotics.base.roboscript;

import com.workert.robotics.base.roboscript.util.RoboScriptArgumentPredicates;
import com.workert.robotics.base.roboscript.util.RoboScriptObjectConversions;

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
			this.virtualMachine.interpret(this.compiler.chunk, 0);
		}).start();

	}

	public final void queueStopForProgram() {
		this.virtualMachine.queueStop();
	}


	protected void defineNativeFunctions() {
		this.defineNativeFunction("print", 1, args -> {
			RoboScript.this.handlePrintMessage(RoboScriptObjectConversions.stringify(args[0]));
			return null;
		});

		this.defineNativeFunction("toString", 1, args -> RoboScriptObjectConversions.stringify(args[0]));
		this.defineNativeFunction("sleep", 1, args -> {
			try {
				Thread.sleep(
						(long) (RoboScriptArgumentPredicates.asNumber(args[0]) * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		});

		this.defineNativeFunction("sin", 1, args -> Math.sin(RoboScriptArgumentPredicates.asNumber(args[0])));
		this.defineNativeFunction("cos", 1, args -> Math.cos(RoboScriptArgumentPredicates.asNumber(args[0])));
		this.defineNativeFunction("tan", 1, args -> Math.tan(RoboScriptArgumentPredicates.asNumber(args[0])));
		this.defineNativeFunction("asin", 1, args -> Math.asin(RoboScriptArgumentPredicates.asNumber(args[0])));
		this.defineNativeFunction("acos", 1, args -> Math.acos(RoboScriptArgumentPredicates.asNumber(args[0])));
		this.defineNativeFunction("atan", 1, args -> Math.atan(RoboScriptArgumentPredicates.asNumber(args[0])));
		this.defineNativeFunction("abs", 1, args -> Math.abs(RoboScriptArgumentPredicates.asNumber(args[0])));
		this.defineNativeFunction("floor", 1, args -> Math.floor(RoboScriptArgumentPredicates.asNumber(args[0])));
		this.defineNativeFunction("ceil", 1, args -> Math.ceil(RoboScriptArgumentPredicates.asNumber(args[0])));
	}

	public final void defineNativeFunction(String name, int argumentCount, NativeFunctionFunctionalInterface function) {
		RoboScriptNativeFunction nativeFunctionWrapper = new RoboScriptNativeFunction((byte) argumentCount);
		nativeFunctionWrapper.function = () -> {
			Object functionOutput;

			if (nativeFunctionWrapper.argumentCount > 0) {
				Object[] functionArgs = new Object[nativeFunctionWrapper.argumentCount];
				for (int i = nativeFunctionWrapper.argumentCount - 1; i >= 0; i--) {
					functionArgs[i] = RoboScript.this.virtualMachine.popStack();
				}

				functionOutput = function.call(functionArgs);
			} else functionOutput = function.call(new Object[] {});
			return RoboScriptObjectConversions.prepareForRoboScriptUse(functionOutput);
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
