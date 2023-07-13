package com.workert.robotics.base.roboscriptbytecode;
import jdk.internal.util.ArraysSupport;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

final class Chunk {
	private RoboByteArrayOutputStream code = new RoboByteArrayOutputStream();
	private List<Object> constants = new ArrayList<>();
	private List<Integer> lines = new ArrayList<>();


	// Add code to the chunk
	void writeCode(byte code, int line) {
		this.code.write(code);
		this.lines.add(line);
	}

	void setCode(int index, byte code) {
		this.code.buf[index] = code;
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
		return this.constants.get(Math.abs(i));
	}


	int readLine(int i) {
		return this.lines.get(i);
	}


	public static class RoboByteArrayOutputStream extends OutputStream {


		private byte buf[];


		private int count;

		public RoboByteArrayOutputStream() {
			this.buf = new byte[32];
		}


		private void ensureCapacity(int minCapacity) {
			// overflow-conscious code
			int oldCapacity = this.buf.length;
			int minGrowth = minCapacity - oldCapacity;
			if (minGrowth > 0) {
				this.buf = Arrays.copyOf(this.buf, ArraysSupport.newLength(oldCapacity,
						minGrowth, oldCapacity /* preferred growth */));
			}
		}


		@Override
		public synchronized void write(int b) {
			this.ensureCapacity(this.count + 1);
			this.buf[this.count] = (byte) b;
			this.count += 1;
		}

		@Override
		public synchronized void write(byte b[], int off, int len) {
			Objects.checkFromIndexSize(off, len, b.length);
			this.ensureCapacity(this.count + len);
			System.arraycopy(b, off, this.buf, this.count, len);
			this.count += len;
		}

		public void writeToByte(int index, byte b) {
			this.buf[index] = b;
		}

		public synchronized byte[] toByteArray() {
			return Arrays.copyOf(this.buf, this.count);
		}

		public synchronized int size() {
			return this.count;
		}

		@Override
		public String toString() {
			return this.buf.toString();
		}

		@Override
		public void close() throws IOException {
		}

	}

}
