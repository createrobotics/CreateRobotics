package com.workert.robotics.base.roboscriptbytecode;
import java.util.Stack;

import static com.workert.robotics.base.roboscriptbytecode.OpCode.*;

final class VirtualMachine {
	private final RoboScript roboScriptInstance;
	private Chunk chunk;
	private final Stack<Object> stack = new Stack<>();
	private int instructionPointer = 0;

	private Object[] globalVariables = new Object[256];

	VirtualMachine(RoboScript instance) {
		this.roboScriptInstance = instance;
	}


	void interpret(Chunk chunk) {
		this.chunk = chunk;
		this.instructionPointer = 0;
		long currentTime = System.currentTimeMillis();
		System.out.println("Started interpreting.");
		this.run();
		System.out.println("Completed in " + (System.currentTimeMillis() - currentTime) + "ms.");
	}

	// heart of the vm, most of the time spent running the program will live here
	private void run() {
		while (true) {
			byte instruction;
			switch (instruction = this.readByte()) {
				case OP_CONSTANT -> {
					Object constant = this.readConstant();
					this.pushStack(constant);
				}
				case OP_NULL -> this.pushStack(null);
				case OP_TRUE -> this.pushStack(true);
				case OP_FALSE -> this.pushStack(false);
				case OP_POP -> this.popStack();
				case OP_GET_LOCAL -> {
					byte slot = this.readByte();
					this.pushStack(this.stack.get(slot));
				}
				case OP_SET_LOCAL -> {
					byte slot = this.readByte();
					this.stack.set(slot, this.peekStack());
				}
				case OP_GET_GLOBAL -> {
					try {
						this.pushStack(this.readGlobalVariable());
					} catch (IndexOutOfBoundsException i) {
						throw new RuntimeError("Undefined variable.");
					}
				}
				case OP_DEFINE_GLOBAL -> this.globalVariables[this.readByte()] = this.popStack();
				case OP_SET_GLOBAL -> {
					try {
						this.setGlobalVariable();
					} catch (IndexOutOfBoundsException i) {
						throw new RuntimeError("Undefined variable.");
					}
				}
				case OP_EQUAL -> this.binaryOperation('=');
				case OP_NOT_EQUAL -> this.binaryOperation('n');
				case OP_LESS -> this.binaryOperation('<');
				case OP_LESS_EQUAL -> this.binaryOperation('l');
				case OP_GREATER -> this.binaryOperation('>');
				case OP_GREATER_EQUAL -> this.binaryOperation('g');
				case OP_ADD -> this.binaryOperation('+');
				case OP_SUBTRACT -> this.binaryOperation('-');
				case OP_MULTIPLY -> this.binaryOperation('*');
				case OP_DIVIDE -> this.binaryOperation('/');
				case OP_NOT -> this.stack.set(this.stack.size() - 1, !isTruthy(this.stack.peek()));
				case OP_NEGATE -> {
					try {
						this.stack.set(this.stack.size() - 1, -(double) this.stack.peek());
					} catch (ClassCastException e) {
						throw new RuntimeError("Can only negate numbers.");
					}
				}
				case OP_JUMP -> {
					short offset = this.readShort();
					this.instructionPointer += offset;
				}
				case OP_JUMP_IF_FALSE -> {
					short offset = this.readShort();
					if (!isTruthy(this.peekStack())) this.instructionPointer += offset;
				}
				case OP_LOOP -> {
					short offset = this.readShort();
					this.instructionPointer -= offset;
				}
				case OP_RETURN -> {
					if (!this.stack.isEmpty()) System.out.println(this.popStack());
					return;
				}
			}
		}
	}


	private byte readByte() {
		return this.chunk.readCode(this.instructionPointer++);
	}

	private short readShort() {
		this.instructionPointer += 2;
		return (short) ((this.chunk.readCode(this.instructionPointer - 2) << 8) | this.chunk.readCode(
				this.instructionPointer - 1));
	}

	private Object readConstant() {
		return this.chunk.readConstant(this.readByte());
	}


	private Object readGlobalVariable() {
		return this.globalVariables[this.readByte()];
	}

	private Object setGlobalVariable() {
		return this.globalVariables[this.readByte()] = this.peekStack();
	}

	void pushStack(Object object) {
		this.stack.push(object);
	}


	Object popStack() {
		return this.stack.pop();
	}

	Object peekStack() {
		return this.stack.peek();
	}

	Object peekStack(int distance) {
		return this.stack.get(this.stack.size() - 1 - distance);
	}


	private void binaryAdd(Object a, Object b) {
		if (a instanceof String || b instanceof String) {
			this.pushStack(a.toString() + b.toString());
			return;
		}
		try {
			this.pushStack((double) a + (double) b);
		} catch (ClassCastException e) {
			throw new RuntimeError("Addition must be between two numbers or a string.");
		}
	}

	private void binaryOperation(char operand) {
		Object b = this.popStack();
		Object a = this.popStack();
		switch (operand) {
			case '+' -> this.binaryAdd(a, b);
			case '-' -> {
				try {
					this.pushStack((double) a - (double) b);
				} catch (ClassCastException e) {
					throw new RuntimeError("Subtraction must be between two numbers.");
				}
			}
			case '*' -> {
				try {
					this.pushStack((double) a * (double) b);
				} catch (ClassCastException e) {
					throw new RuntimeError("Multiplication must be between two numbers.");
				}
			}
			case '/' -> {
				try {
					this.pushStack((double) a / (double) b);
				} catch (ClassCastException e) {
					throw new RuntimeError("Division must be between two numbers.");
				}
			}
			case '>' -> {
				try {
					this.pushStack((double) a > (double) b);
				} catch (ClassCastException e) {
					throw new RuntimeError("Comparison using '>' must be between two numbers.");
				}
			}
			case '<' -> {
				try {
					this.pushStack((double) a < (double) b);
				} catch (ClassCastException e) {
					throw new RuntimeError("Comparison using '<' must be between two numbers.");
				}
			}
			case 'g' -> { // >=
				try {
					this.pushStack((double) a >= (double) b);
				} catch (ClassCastException e) {
					throw new RuntimeError("Comparison using '>=' must be between two numbers.");
				}
			}
			case 'l' -> { // <=
				try {
					this.pushStack((double) a <= (double) b);
				} catch (ClassCastException e) {
					throw new RuntimeError("Comparison using '<=' must be between two numbers.");
				}
			}
			case '=' -> this.pushStack(a.equals(b)); // ==
			case 'n' -> this.pushStack(!a.equals(b)); // !=
		}
	}

	private static boolean isTruthy(Object o) {
		if (o == null) return false;
		if (o instanceof Boolean b) return b;
		return true;
	}
}
