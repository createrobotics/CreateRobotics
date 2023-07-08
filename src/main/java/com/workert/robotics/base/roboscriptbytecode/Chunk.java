package com.workert.robotics.base.roboscriptbytecode;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

final class Chunk {
	// Dynamic array of bytes, Crafting Interpreters used a custom system as C does not have something like this.
	private ByteArrayOutputStream code = new ByteArrayOutputStream();
	private List<Object> constants = new ArrayList<>();
	private List<Integer> lines = new ArrayList<>();


	// Add code to the chunk
	void writeCode(byte code, int line) {
		this.code.write(code);
		this.lines.add(line);
	}

	byte readCode(int i) {
		return this.code.toByteArray()[i];
	}

	int getCodeSize() {
		return this.code.size();
	}


	// Add a value to the chunk
	int addConstant(Object value) {
		this.constants.add(value);
		return this.constants.size() - 1;
	}

	Object readConstant(int i) {
		return this.constants.get(i);
	}


	int readLine(int i) {
		return this.lines.get(i);
	}
}
