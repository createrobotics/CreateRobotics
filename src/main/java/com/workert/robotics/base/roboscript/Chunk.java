package com.workert.robotics.base.roboscript;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>Warning!</b>
 * <p>
 * This Class contains code that is difficult to read and does <i>not</i> follow the Java Code Conventions.
 * <p>
 * It prioritizes high-speed execution, often using boilerplate alternatives instead of more concise code.
 * <p>
 * Do not reuse this code.<br>
 * Instead, look at RoboScript AST for better written but slower running Examples.
 * <p>
 * If you want to make your own Scripting Language the Create Robotics Team recommends reading <a href="https://craftinginterpreters.com/">Crafting Interpreters</a>, a Book which has helped us a lot with implementing RoboScript.
 */
final class Chunk {
	private List<Byte> code = new ArrayList<>();

	byte[] finalCode;

	private final List<Object> constants = new ArrayList<>();

	Object[] finalConstants;

	private List<Integer> lines = new ArrayList<>();

	int[] finalLines;

	int getCodeSize() {
		return this.code.size();
	}


	// Add a value to the chunk
	int addConstant(Object value) {
		this.constants.add(value);
		return this.constants.size() - 1;
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

	void finishChunk() {
		this.finalCode = new byte[this.code.size()];
		for (int i = 0; i < this.code.size(); i++) {
			this.finalCode[i] = this.code.get(i);
		}
		this.finalConstants = this.constants.toArray();
		this.finalLines = this.lines.stream().mapToInt(Integer::intValue).toArray();
	}
}
