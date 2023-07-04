package com.workert.robotics.base.roboscriptbytecode;
import java.io.ByteArrayOutputStream;

public class Chunk {
	protected interface OpCode {
		byte OP_RETURN = 0; // 0000 0000
	}

	private ByteArrayOutputStream code = new ByteArrayOutputStream();


	protected void write(byte b) {
		this.code.write(b);
	}

	protected byte read(int i) {
		return this.code.toByteArray()[i];
	}

	protected int getCodeCount() {
		return this.code.size();
	}
}
