package com.workert.robotics.base.roboscriptbytecode;
public class RoboScriptRunner {
	public static void main(String[] args) {
		Chunk chunk = new Chunk();
		int constant = chunk.addConstant(1.14);
		chunk.writeCode(OpCode.OP_CONSTANT, 1);
		chunk.writeCode((byte) constant, 1);
		chunk.writeCode(OpCode.OP_RETURN, 1);
		Printer printer = new Printer();
		printer.disassembleChunk(chunk, "test chunk");
		VirtualMachine vm = new VirtualMachine();
		vm.interpret(chunk);
	}
}
