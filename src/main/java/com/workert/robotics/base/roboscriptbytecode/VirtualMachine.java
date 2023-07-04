package com.workert.robotics.base.roboscriptbytecode;
public class VirtualMachine {
	private Chunk chunk;
	private int instructionPointer;

	// this is the function that will be called when the computer or drone or whatever actually needs to run
	protected void interpret(Chunk chunk) {
		this.chunk = chunk;
		this.instructionPointer = 0;
		this.run();
	}

	// heart of the vm, most of the time spent running the program will live here
	private void run() {
		while (true) {
			byte instruction;
			switch (instruction = this.readByte()) {
				case Chunk.OpCode.CONSTANT -> {
					Object constant = this.readConstant();
					System.out.println(constant);
				}
				case Chunk.OpCode.RETURN -> {
					return;
				}
			}
		}
	}


	private byte readByte() {
		return this.chunk.readCode(this.instructionPointer++);
	}

	private Object readConstant() {
		return this.chunk.readConstant(this.readByte());
	}

	private void debugTraceExecution() {
		Printer.disassembleInstruction(this.chunk, this.instructionPointer);
	}
}
