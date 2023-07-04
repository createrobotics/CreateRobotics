package com.workert.robotics.base.roboscriptbytecode;
public class RoboScriptRunner {
	public static void main(String[] args) {
		Chunk chunk = new Chunk();
		int constant = chunk.addConstant(1.14);
		chunk.writeCode(Chunk.OpCode.CONSTANT, 1);
		chunk.writeCode((byte) constant, 1);
		chunk.writeCode(Chunk.OpCode.RETURN, 1);
		Printer printer = new Printer();
		printer.disassembleChunk(chunk, "test chunk");
		VirtualMachine vm = new VirtualMachine();
		vm.interpret(chunk);
	}
}
