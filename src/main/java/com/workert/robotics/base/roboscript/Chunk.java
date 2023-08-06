package com.workert.robotics.base.roboscript;

import java.util.ArrayList;
import java.util.List;

final class Chunk {
	private List<Byte> code = new ArrayList<>();


	private final List<Object> constants = new ArrayList<>();

	private List<Integer> lines = new ArrayList<>();

	byte readCode(int i) {
		return this.code.get(i);
	}

	int getCodeSize() {
		return this.code.size();
	}


	// Add a value to the chunk
	int addConstant(Object value) {
		this.constants.add(value);
		return this.constants.size() - 1;
	}

	void removeConstant(int location) {
		this.constants.remove(location);
	}

	void setConstant(int index, Object value) {
		this.constants.set(index, value);
	}


	void setCode(List<Byte> b) {
		this.code = b;
	}

	void addCode(List<Byte> b) {
		this.code.addAll(b);
	}

	void setLines(List<Integer> i) {
		this.lines = i;
	}

	void addLines(List<Integer> i) {
		this.lines.addAll(i);
	}

	Object getConstant(int i) {
		return this.constants.get(Math.abs(i));
	}


	int getLine(int i) {
		return this.lines.get(i);
	}
}
