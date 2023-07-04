package com.workert.robotics.base.roboscriptbytecode;

public class Printer {
	// The printer is the bytecode version of the AstPrinter

	void disassembleChunk(Chunk chunk, String name) {
		System.out.printf("== %s ==\n", name);
		// Instructions may have different sizes, so you cannot increment by a finite value.
		// Increments are decided by the disassembleInstruction method.
		for (int offset = 0; offset < chunk.getCodeSize(); ) {
			offset = disassembleInstruction(chunk, offset);
		}
	}


	protected static int disassembleInstruction(Chunk chunk, int offset) {
		System.out.printf("%04d", offset);
		if (offset > 0 && chunk.readLine(offset) == chunk.readLine(offset - 1)) System.out.printf("   | ");
		else
			System.out.printf("%4d ", chunk.readLine(offset));
		byte instruction = chunk.readCode(offset);
		switch (instruction) {
			case Chunk.OpCode.CONSTANT -> {
				return constantInstruction("OP_CONSTANT", chunk, offset);
			}
			case Chunk.OpCode.RETURN -> {
				return simpleInstruction("OP_RETURN", offset);
			}


			default -> {
				System.err.println("Unknown opcode '" + offset + "'.");
				return offset + 1;
			}
		}
	}

	private static int simpleInstruction(String name, int offset) {
		System.out.printf("%s\n", name);
		return offset + 1;
	}

	private static int constantInstruction(String name, Chunk chunk, int offset) {
		byte constant = chunk.readCode(offset + 1);
		System.out.printf("%-16s %4d '", name, constant);
		System.out.printf("%s", chunk.readConstant(offset).toString());
		System.out.printf("'\n");
		return offset + 2;
	}
}
