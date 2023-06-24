package com.workert.robotics.base.roboscriptbytecode;
public class Chunk {
	int count;
	int capacity;
	byte code;

	protected enum OperationCode {
		OP_RETURN,
	}

	protected Chunk(int count, int capacity, byte code) {
		this.count = count;
		this.capacity = capacity;
		this.code = code;
	}

	void initChunk(Chunk chunk) {
		chunk.count = 0;
		chunk.capacity = 0;
		chunk.code = '\0';
	}


}
