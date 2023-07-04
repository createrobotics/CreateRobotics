package com.workert.robotics.base.roboscriptbytecode;
public class RoboScriptRunner {
	public static void main(String[] args) {
		Chunk chunk = new Chunk();
		chunk.write(Chunk.OpCode.OP_RETURN);
		Printer p = new Printer();
		p.disassembleChunk(chunk, "test chunk");
	}
}
