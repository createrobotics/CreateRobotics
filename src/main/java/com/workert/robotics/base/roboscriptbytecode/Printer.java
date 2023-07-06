package com.workert.robotics.base.roboscriptbytecode;

import static com.workert.robotics.base.roboscriptbytecode.OpCode.*;

final class Printer {
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
		String segmentedByte = String.format("%8s", Integer.toBinaryString(instruction & 0xFF)).replace(' ', '0');
		System.out.print('[' + segmentedByte.substring(0, 4) + ' ' + segmentedByte.substring(4, 8) + "] ");
		switch (instruction) {
			case OP_CONSTANT -> {
				return constantInstruction("OP_CONSTANT", chunk, offset);
			}
			case OP_ADD -> {
				return simpleInstruction("OP_ADD", offset);
			}
			case OP_SUBTRACT -> {
				return simpleInstruction("OP_SUBTRACT", offset);
			}
			case OP_MULTIPLY -> {
				return simpleInstruction("OP_MULTIPLY", offset);
			}
			case OP_DIVIDE -> {
				return simpleInstruction("OP_DIVIDE", offset);
			}
			case OP_NOT -> {
				return simpleInstruction("OP_NOT", offset);
			}
			case OP_NEGATE -> {
				return simpleInstruction("OP_NEGATE", offset);
			}
			case OP_RETURN -> {
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
		System.out.println(chunk.readConstant(constant) + "'");
		return offset + 2;
	}
}
