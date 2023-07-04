package com.workert.robotics.base.roboscriptbytecode;
import java.util.ArrayList;
import java.util.List;

public class VirtualMachine {
	private Chunk chunk;
	private List<Object> stack = new ArrayList<>(256);
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
				case OpCode.OP_CONSTANT -> {
					Object constant = this.readConstant();
					this.addStack(constant);
					System.out.println(constant);
				}
				case OpCode.OP_RETURN -> {
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

	protected void addStack(Object object) {
		this.stack.add(object);
	}

	protected void addStack(int index, Object object) {
		this.stack.set(index, object);
	}

	protected Object readStack(int index) {
		return this.stack.get(index);
	}

	protected Object popStack() {
		Object lastElement = this.stack.get(this.stack.size() - 1);
		this.stack.remove(this.stack.size() - 1);
		return lastElement;
	}
}
