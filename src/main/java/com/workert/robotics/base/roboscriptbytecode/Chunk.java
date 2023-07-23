package com.workert.robotics.base.roboscriptbytecode;

import java.util.ArrayList;
import java.util.List;

final class Chunk {
	private List<Byte> code = new ArrayList<>();


	private Object[] constants = new Object[512];
	private int constantsSize = 0;

	private List<Integer> lines = new ArrayList<>();

	byte readCode(int i) {
		return this.code.get(i);
	}

	int getCodeSize() {
		return this.code.size();
	}


	// Add a value to the chunk
	int addConstant(Object value) {
		this.constants[this.constantsSize++] = value;
		return this.constantsSize - 1;
	}

	void setConstant(int index, Object value) {
		this.constants[index] = value;
	}


	void setCode(List<Byte> b) {
		this.code = b;
	}

	void combineCode(List<Byte> b) {
		this.code.addAll(b);
	}

	void setLines(List<Integer> i) {
		this.lines = i;
	}

	void combineLines(List<Integer> i) {
		this.lines.addAll(i);
	}


	Object readConstant(int i) {
		return this.constants[i & 0xFF];
	}


	int readLine(int i) {
		return this.lines.get(i);
	}


}
