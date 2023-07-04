package com.workert.robotics.base.roboscriptbytecode;
public class Printer {
	protected void disassembleChunk(Chunk chunk, String name) {
		System.out.println("== " + name + " ==");
		for (int offset = 0; offset < chunk.getCodeCount(); ) {
			offset = this.disassembleInstruction(chunk, offset);
		}
	}

	protected int disassembleInstruction(Chunk chunk, int offset) {
		System.out.printf("%04d ", offset);
		byte instruction = chunk.read(offset);
		switch (instruction) {
			case Chunk.OpCode.OP_CONSTANT -> {
				return this.constantInstruction("OP_CONSTANT", chunk, offset);
			}
			case Chunk.OpCode.OP_RETURN -> {
				return this.simpleInstruction("OP_RETURN", offset);
			}
			default -> {
				System.out.println("Unknown opcode " + instruction);
				return offset + 1;
			}
		}
	}

	private int simpleInstruction(String name, int offset) {
		System.out.println(name);
		return offset + 1;
	}

	private int constantInstruction(String name, Chunk chunk, int offset) {
		byte constant = chunk.read(offset + 1);
		System.out.printf("%-16s %4d '", name, constant);
		System.out.println(chunk.readConstant(constant) + "'");
		return offset + 2;
	}


}
