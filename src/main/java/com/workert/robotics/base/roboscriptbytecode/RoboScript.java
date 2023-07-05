package com.workert.robotics.base.roboscriptbytecode;
public abstract class RoboScript {


	public final void runString(String source) {

	}

	public final void runASMString(String source) {
		Assembler assembler = new Assembler();
		Chunk c;
		try {
			c = assembler.assemble(source);
		} catch (AssembleError e) {
			this.handleErrorMessage("[line " + e.line + "] " + e.message);
			return;
		}

		new Printer().disassembleChunk(c, "From ASM");
		System.out.println();
		System.out.println("== From VM ==");
		try {
			new VirtualMachine().interpret(c);
		} catch (RuntimeError e) {
			this.handleErrorMessage(e.message);
		}
	}

	protected abstract void handlePrintMessage(String message);

	protected abstract void handleErrorMessage(String message);
}
