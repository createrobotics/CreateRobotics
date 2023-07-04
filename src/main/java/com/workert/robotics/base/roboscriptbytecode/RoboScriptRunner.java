package com.workert.robotics.base.roboscriptbytecode;
public class RoboScriptRunner {
	public static void main(String[] args) {
		Chunk chunk = new Chunk();
		byte constant = (byte) chunk.addConstant(1.2);
		chunk.write(Chunk.OpCode.OP_CONSTANT);
		chunk.write(constant);
		chunk.write(Chunk.OpCode.OP_RETURN);
		Printer p = new Printer();
		p.disassembleChunk(chunk, "test chunk");
	}
}
