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

	protected void reportCompileError(int line, String message) {
		this.hadError = true;
		this.handleErrorMessage("[line " + line + "] Error: " + message);
	}


	protected abstract void handlePrintMessage(String message);

	protected abstract void handleErrorMessage(String message);
}
