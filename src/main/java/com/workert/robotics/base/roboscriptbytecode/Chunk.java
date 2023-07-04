package com.workert.robotics.base.roboscriptbytecode;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Chunk {
	protected interface OpCode {
		/* 0000 0000 */ byte OP_CONSTANT = 0;
		/* 0000 0001 */ byte OP_RETURN = 1;
	}

	private ByteArrayOutputStream code = new ByteArrayOutputStream();
	private List<Object> constants = new ArrayList<>();


	protected void write(byte b) {
		this.code.write(b);
	}

	protected byte read(int i) {
		return this.code.toByteArray()[i];
	}

	protected int addConstant(Object constant) {
		this.constants.add(constant);
		return this.getCodeCount() - 1;
	}

	protected Object readConstant(int i) {
		return this.constants.get(i);
	}

	protected int getCodeCount() {
		return this.code.size();
	}
}
