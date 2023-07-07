package com.workert.robotics.base.roboscriptbytecode;
public abstract class RoboScript {
	VirtualMachine vm = new VirtualMachine(this);
	private boolean hadError = false;

	public final void runString(String source) {
		this.hadError = false;
		Scanner s = new Scanner(source);
		System.out.println(s.scanTokens());
		Compiler c = new Compiler(this);
		c.compile(source);
		if (this.hadError) {
			return;
		}
		Printer p = new Printer();
		p.disassembleChunk(c.chunk, "From source");
		this.vm.interpret(c.chunk);
	}

	public final void runASMString(String source) {
		this.hadError = false;
		Assembler assembler = new Assembler();
		Chunk c;
		try {
			c = assembler.assemble(source);
		} catch (AssembleError e) {
			this.handleErrorMessage("[line " + e.line + "] " + e.message);
			return;
		}
		if (this.hadError) return;
		new Printer().disassembleChunk(c, "From ASM");
		System.out.println();
		System.out.println("== From VM ==");
		try {
			this.vm.interpret(c);
		} catch (RuntimeError e) {
			this.handleErrorMessage(e.message);
		}
	}

	protected void reportCompileError(int line, String message) {
		this.hadError = true;
		this.handleErrorMessage("[line " + line + "] Error: " + message);
	}


	protected abstract void handlePrintMessage(String message);

	protected abstract void handleErrorMessage(String message);
}
