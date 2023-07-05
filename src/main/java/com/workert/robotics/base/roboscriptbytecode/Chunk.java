package com.workert.robotics.base.roboscriptbytecode;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Chunk {
	// Dynamic array of bytes, Crafting Interpreters used a custom system as C does not have something like this.
	protected ByteArrayOutputStream code = new ByteArrayOutputStream();
	private List<Object> constants = new ArrayList<>();
	private List<Integer> lines = new ArrayList<>();


	// Add code to the chunk
	protected void writeCode(byte code, int line) {
		this.code.write(code);
		this.lines.add(line);
	}

	protected byte readCode(int i) {
		return this.code.toByteArray()[i];
	}

	protected int getCodeSize() {
		return this.code.size();
	}


	// Add a value to the chunk
	protected int addConstant(Object value) {
		this.constants.add(value);
		return this.constants.size() - 1;
	}

	protected Object readConstant(int i) {
		return this.constants.get(i);
	}


	protected int readLine(int i) {
		return this.lines.get(i);
	}
}
