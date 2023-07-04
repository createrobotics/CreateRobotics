package com.workert.robotics.base.roboscriptbytecode;
import static com.workert.robotics.base.roboscriptbytecode.OpCode.*;

public class RoboScriptRunner {
	public static void main(String[] args) {
		Chunk chunk = new Chunk();
		int constant = chunk.addConstant(1.14);
		chunk.writeCode(OP_CONSTANT, 1);
		chunk.writeCode((byte) constant, 1);
		chunk.writeCode(OP_NEGATE, 1);
		chunk.writeCode(OP_RETURN, 1);
		Printer printer = new Printer();
		printer.disassembleChunk(chunk, "test chunk");
		VirtualMachine vm = new VirtualMachine();
		vm.interpret(chunk);
	}
}
