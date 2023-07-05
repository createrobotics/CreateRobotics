package com.workert.robotics.base.roboscriptbytecode;
import java.util.Stack;

import static com.workert.robotics.base.roboscriptbytecode.OpCode.*;

public class VirtualMachine {
	private Chunk chunk;
	private Stack<Object> stack = new Stack<>();
	private int stackPointer = 0;
	private int instructionPointer = 0;

	// this is the function that will be called when the computer or drone or whatever actually needs to run
	protected void interpret(Chunk chunk) {
		this.chunk = chunk;
		this.instructionPointer = 0;
		this.stackPointer = 0;
		this.run();
	}

	// heart of the vm, most of the time spent running the program will live here
	private void run() {
		while (true) {
			byte instruction;
			switch (instruction = this.readByte()) {
				case OP_CONSTANT -> {
					Object constant = this.readConstant();
					this.pushStack(constant);
					System.out.println(constant);
				}
				case OP_NEGATE -> {
					if (this.peekStack() instanceof Double) this.pushStack(-(double) this.popStack());
					break;
				}
				case OP_RETURN -> {
					System.out.println(this.popStack());
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
		System.out.print("          ");
		for (Object slot : this.stack) {
			System.out.print("[ " + slot + " ]");
		}
		System.out.println();
		Printer.disassembleInstruction(this.chunk, this.instructionPointer);
	}

	protected void pushStack(Object object) {
		this.stack.push(object);
	}

	protected Object readStack(int index) {
		return this.stack.get(index);
	}

	protected Object popStack() {
		return this.stack.pop();
	}

	protected Object peekStack() {
		return this.stack.peek();
	}
}
