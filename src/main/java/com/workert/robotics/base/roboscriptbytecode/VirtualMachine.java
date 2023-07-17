package com.workert.robotics.base.roboscriptbytecode;
import java.util.Stack;

import static com.workert.robotics.base.roboscriptbytecode.OpCode.*;

final class VirtualMachine {

	/**
	 * The instance of RoboScript running the virtual machine.
	 */
	private final RoboScript roboScriptInstance;

	/**
	 * The current chunk being interpreted.
	 */
	private Chunk chunk;

	/**
	 * The main stack of the program.
	 */
	private final Stack<Object> stack = new Stack<>();

	/**
	 * The index of the current instruction.
	 */
	private int instructionPointer = 0;

	/**
	 * The size of the stack when a new function is entered.
	 */
	private int basePointer = 0;

	/**
	 * Variables defined in the global scope; Can be accessed from anywhere.
	 */
	private final Object[] globalVariables = new Object[256];


	/**
	 * Creates a static virtual machine.
	 *
	 * @param instance The instance of RoboScript running the virtual machine.
	 */
	VirtualMachine(RoboScript instance) {
		this.roboScriptInstance = instance;
	}

	/**
	 * Starts the virtual machine and interprets the chunk.
	 *
	 * @param chunk The chunk being interpreted.
	 */
	void interpret(Chunk chunk) {
		this.chunk = chunk;
		this.instructionPointer = 0;
		this.basePointer = 1;
		this.pushStack((Object) (int) -1); // top-level stack frame.
		this.pushStack((Object) (int) -1);

		long currentTime = System.currentTimeMillis();
		System.out.println("Started interpreting.");
		this.run();
		System.out.println("Completed in " + (System.currentTimeMillis() - currentTime) + "ms.");
	}

	/**
	 * The main part of the VM,
	 */
	private void run() {
		this.pushStack(this.basePointer);
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
					this.pushStack(this.stack.get(this.basePointer + slot));
				}
				case OP_SET_LOCAL -> {
					byte slot = this.readByte();
					this.stack.set(this.basePointer + slot, this.popStack());
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
						this.globalVariables[this.readByte()] = this.peekStack();
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

				case OP_CALL -> {
					int functionAddress = (int) this.popStack();
					this.pushStack(this.instructionPointer);
					this.pushStack(this.basePointer);
					this.basePointer = this.stack.size() - 1;
					this.instructionPointer = functionAddress;
				}
				case OP_RETURN -> {
					Object returnValue = this.popStack();

					// clear everything except the return address
					this.stack.setSize(this.basePointer + 1);

					this.basePointer = (int) this.stack.pop();
					int returnAddress = (int) this.popStack();
					this.instructionPointer = returnAddress;

					// test code to check for invalid return address, already placed here.
					if (this.instructionPointer < 0) {
						System.out.println(returnValue);
						return;
					}
					this.pushStack(returnValue);
					return;
				}
				case OP_END -> {
					return;
				}

				case OP_LOG -> {
					System.out.println(this.popStack());
				}
			}
		}
	}

	/**
	 * Reads the byte at the current index of instructionPointer.
	 *
	 * @return The byte at the current index of instructionPointer.
	 */
	private byte readByte() {
		return this.chunk.readCode(this.instructionPointer++);
	}

	/**
	 * Reads the current and next bytes from instructionPointer and combines them into a short
	 *
	 * @return The short value of the current and next byte.
	 */
	private short readShort() {
		this.instructionPointer += 2;
		return (short) ((this.chunk.readCode(this.instructionPointer - 2) << 8) | this.chunk.readCode(
				this.instructionPointer - 1));
	}

	/**
	 * Reads the current byte and finds the constant in the current chunk at the index of that byte.
	 *
	 * @return The constant in the current chunk at the index of the current byte.
	 */
	private Object readConstant() {
		return this.chunk.readConstant(this.readByte());
	}

	/**
	 * Reads the current byte and finds the global variable at the index of that byte.
	 *
	 * @return The global variable at the index of the current byte.
	 */
	private Object readGlobalVariable() {
		return this.globalVariables[this.readByte()];
	}

	/**
	 * Pushes a value to the stack.
	 *
	 * @param object The value pushed to the stack.
	 */
	void pushStack(Object object) {
		this.stack.push(object);
	}

	/**
	 * Returns the value at the top of the stack and removes it.
	 *
	 * @return The value at the top of the stack.
	 */
	Object popStack() {
		return this.stack.pop();
	}

	/**
	 * Returns the value at the top of the stack without removing it.
	 *
	 * @return The value at the top of the stack.
	 */
	Object peekStack() {
		return this.stack.peek();
	}


	/**
	 * Returns the value at the top of the stack minus the distance without removing it.
	 *
	 * @param distance The distance from the top of the stack.
	 * @return The value at the top of the stack minus the distance without removing it.
	 */
	Object peekStack(int distance) {
		return this.stack.get(this.stack.size() - 1 - distance);
	}


	/**
	 * Adds two numbers.
	 *
	 * @param a Addend 'a'.
	 * @param b Addend 'b'.
	 */
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

	/**
	 * Handles all operations with their given operand.
	 *
	 * @param operand The operand used to perform an operation.
	 */
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

	/**
	 * Returns the truthy value of the passed in object.
	 *
	 * @param o The object being evaluated.
	 * @return The truthy value of the passed in object.
	 */
	private static boolean isTruthy(Object o) {
		if (o == null) return false;
		if (o instanceof Boolean b) return b;
		return true;
	}
}
