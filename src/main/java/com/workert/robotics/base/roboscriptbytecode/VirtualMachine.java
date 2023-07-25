package com.workert.robotics.base.roboscriptbytecode;
import java.util.List;
import java.util.Map;

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
	final Object[] stack = new Object[256];

	/**
	 * The current stack size
	 */
	int stackSize = 0;

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
	 * Global functions that are defined natively and use Java code.
	 */
	RoboScript.NativeFunction[] nativeFunctions = new RoboScript.NativeFunction[32];

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
		this.basePointer = 0;
		// this.pushStack((Object) (int) -1);   , top-level stack frame.
		// this.pushStack((Object) (int) -1);

		long currentTime = System.currentTimeMillis();
		System.out.println("Started interpreting.");
		this.run();
		System.out.println("Completed in " + (System.currentTimeMillis() - currentTime) + "ms.");
	}

	/**
	 * The main part of the VM
	 */
	private void run() {
		while (true) {
			switch (this.readByte()) {
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
					this.pushStack(this.stack[this.basePointer + slot]);
				}
				case OP_SET_LOCAL -> {
					byte slot = this.readByte();
					this.stack[this.basePointer + slot] = this.popStack();
				}
				case OP_GET_NATIVE -> {
					byte slot = this.readByte();
					this.pushStack(this.nativeFunctions[slot]);
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
				case OP_INCREMENT_GLOBAL -> {
					try {
						byte b = this.readByte();
						double previous = (double) this.globalVariables[b];
						this.globalVariables[b] = previous + 1;
						this.pushStack(previous);
					} catch (IndexOutOfBoundsException | ClassCastException e) {
						if (e instanceof IndexOutOfBoundsException) {
							throw new RuntimeError("Undefined variable.");
						} else {
							throw new RuntimeError("Incrementing variable must be a number.");
						}
					}
				}
				case OP_INCREMENT_LOCAL -> {
					try {
						byte b = this.readByte();
						double previous = (double) this.stack[this.basePointer + b];
						this.globalVariables[b] = previous + 1;
						this.pushStack(previous);
					} catch (IndexOutOfBoundsException | ClassCastException e) {
						if (e instanceof IndexOutOfBoundsException) {
							throw new RuntimeError("Undefined variable.");
						} else {
							throw new RuntimeError("Incrementing variable must be a number.");
						}
					}
				}
				case OP_INCREMENT_LIST_MAP -> {
					Object key = this.popStack();
					Object gettable = this.popStack();

					if (gettable instanceof Map map) {
						try {
							double previous = ((double) map.get(key));
							map.put(key, previous + 1);
							this.pushStack(previous);
						} catch (ClassCastException e) {
							throw new RuntimeError("Incrementing variable must be a number.");
						}
					} else if (gettable instanceof List list) {
						try {
							double d = (double) key;
							if (isWhole(d) && !isNegative(d)) {
								if (d >= list.size())
									throw new RuntimeError("List index out of range of '" + (list.size() - 1) + "'.");

								try {
									double previous = ((double) list.get((int) Math.round(d)));
									list.set((int) Math.round(d), previous + 1);
									this.pushStack(previous);
								} catch (ClassCastException e) {
									throw new RuntimeError("Incrementing variable must be a number.");
								}
							} else {
								throw new RuntimeError(
										"Index value for list must be a whole number greater or equal to 0.");
							}
						} catch (ClassCastException e) {
							throw new RuntimeError(
									"Index value for list must be a whole number greater or equal to 0.");
						}
					} else {
						throw new RuntimeError("Can only get objects from maps.");
					}
				}
				case OP_DECREMENT_GLOBAL -> {
					try {
						byte b = this.readByte();
						double previous = (double) this.globalVariables[b];
						this.globalVariables[b] = previous - 1;
						this.pushStack(previous);
					} catch (IndexOutOfBoundsException | ClassCastException e) {
						if (e instanceof IndexOutOfBoundsException) {
							throw new RuntimeError("Undefined variable.");
						} else {
							throw new RuntimeError("Decrementing variable must be a number.");
						}
					}
				}
				case OP_DECREMENT_LOCAL -> {
					try {
						byte b = this.readByte();
						double previous = (double) this.stack[this.basePointer + b];
						this.globalVariables[b] = previous - 1;
						this.pushStack(previous);
					} catch (IndexOutOfBoundsException | ClassCastException e) {
						if (e instanceof IndexOutOfBoundsException) {
							throw new RuntimeError("Undefined variable.");
						} else {
							throw new RuntimeError("Decrementing variable must be a number.");
						}
					}
				}
				case OP_DECREMENT_LIST_MAP -> {
					Object key = this.popStack();
					Object gettable = this.popStack();

					if (gettable instanceof Map map) {
						try {
							double previous = ((double) map.get(key));
							map.put(key, previous - 1);
							this.pushStack(previous);
						} catch (ClassCastException e) {
							throw new RuntimeError("Incrementing variable must be a number.");
						}
					} else if (gettable instanceof List list) {
						try {
							double d = (double) key;
							if (isWhole(d) && !isNegative(d)) {
								if (d >= list.size())
									throw new RuntimeError("List index out of range of '" + (list.size() - 1) + "'.");

								try {
									double previous = ((double) list.get((int) Math.round(d)));
									list.set((int) Math.round(d), previous - 1);
									this.pushStack(previous);
								} catch (ClassCastException e) {
									throw new RuntimeError("Decrementing variable must be a number.");
								}
							} else {
								throw new RuntimeError(
										"Index value for list must be a whole number greater or equal to 0.");
							}
						} catch (ClassCastException e) {
							throw new RuntimeError(
									"Index value for list must be a whole number greater or equal to 0.");
						}
					} else {
						throw new RuntimeError("Can only get objects from maps.");
					}
				}
				case OP_SUBTRACT -> this.binaryOperation('-');
				case OP_MULTIPLY -> this.binaryOperation('*');
				case OP_DIVIDE -> this.binaryOperation('/');
				case OP_MODULO -> this.binaryOperation('%');
				case OP_POWER -> this.binaryOperation('^');
				case OP_NOT -> this.stack[this.stackSize - 1] = !isTruthy(this.peekStack());
				case OP_NEGATE -> {
					try {
						this.stack[this.stackSize - 1] = -(double) this.peekStack();
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
					byte argumentCount = this.readByte();

					Object callable = this.peekStack(argumentCount);


					if (callable instanceof RoboScript.NativeFunction function) {
						if (function.argumentCount != argumentCount) {
							throw new RuntimeError(
									"Expected '" + function.argumentCount + "' arguments but got '" + argumentCount + "'.");
						}
						Object returnValue = function.call(this);
						while (!(this.peekStack() instanceof RoboScript.NativeFunction)) {
							this.popStack();
						}
						this.popStack();
						this.pushStack(returnValue);
						break;
					}

					if (!(callable instanceof RoboScriptFunction function))
						throw new RuntimeError(
								"Can only call functions, instead got '" + callable.getClass() + "'.");
					if (argumentCount != function.argumentCount)
						throw new RuntimeError(
								"Expected '" + function.argumentCount + "' arguments but got '" + argumentCount + "'.");

					// push return address and base pointer
					this.pushStack(this.instructionPointer);
					this.pushStack(this.basePointer);

					this.instructionPointer = function.address;
					this.basePointer = this.stackSize - argumentCount - 2;
				}
				case OP_RETURN -> {
					Object returnValue = this.popStack();

					this.basePointer = (int) this.popStack();
					this.instructionPointer = (int) this.popStack();
					while (!(this.peekStack() instanceof RoboScriptFunction)) {
						this.popStack();
					}
					this.popStack();
					this.pushStack(returnValue);
				}
				case OP_PUT -> {
					Object puttable = this.peekStack(2);
					Object value = this.popStack();
					Object key = this.popStack();

					if (puttable instanceof Map map) {
						map.put(key, value);
					} else {
						throw new RuntimeError(
								"Can only use 'OP_MAP_PUT' with a list being the third to top in the stack.");
					}

				}
				case OP_LIST_ADD -> {
					try {
						Object value = this.popStack();
						List<Object> list = (List<Object>) this.peekStack();
						list.add(value);
					} catch (ClassCastException e) {
						throw new IllegalArgumentException(
								"Can only use 'OP_LIST_PUT' with a list being the second to top in the stack.");
					}
				}
				case OP_LIST_MAP_GET -> {
					byte keep = this.readByte();
					Object key = this.peekStack();
					Object gettable = this.peekStack(1);

					if (keep == 0) {
						key = this.popStack();
						gettable = this.popStack();
					}

					if (gettable instanceof Map map) {
						this.pushStack(map.get(key));
					} else if (gettable instanceof List list) {
						try {
							double d = (double) key;
							if (isWhole(d) && !isNegative(d)) {
								if (d >= list.size())
									throw new RuntimeError("List index out of range of '" + (list.size() - 1) + "'.");
								this.pushStack(list.get((int) Math.round(d)));
							} else {
								throw new RuntimeError(
										"Index value for list must be a whole number greater or equal to 0.");
							}
						} catch (ClassCastException e) {
							throw new RuntimeError(
									"Index value for list must be a whole number greater or equal to 0.");
						}
					} else {
						throw new RuntimeError("Can only get objects from maps.");
					}
				}
				case OP_LIST_MAP_SET -> {
					Object value = this.popStack();
					Object key = this.popStack();
					Object settable = this.popStack();

					this.pushStack(value);

					if (settable instanceof Map map) {
						// different here
						map.put(key, value);
					} else if (settable instanceof List list) {
						try {

							// this is almost copy paste
							double d = (double) key;
							if (isWhole(d) && !isNegative(d)) {
								if (d >= list.size())
									throw new RuntimeError("List index out of range of '" + (list.size() - 1) + "'.");
								// different here
								list.set((int) Math.round(d), value);
							} else {
								throw new RuntimeError(
										"Index value for list must be a whole number greater or equal to 0.");
							}
						} catch (ClassCastException e) {
							throw new RuntimeError(
									"Index value for list must be a whole number greater or equal to 0.");
						}
					}
				}
				case OP_END -> {
					return;
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
		return this.chunk.getConstant(this.readShort());
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
		this.stack[this.stackSize++] = object;
	}

	/**
	 * Returns the value at the top of the stack and removes it.
	 *
	 * @return The value at the top of the stack.
	 */
	Object popStack() {
		return this.stack[--this.stackSize];
	}

	/**
	 * Returns the value at the top of the stack without removing it.
	 *
	 * @return The value at the top of the stack.
	 */
	Object peekStack() {
		return this.stack[this.stackSize - 1];
	}


	/**
	 * Returns the value at the top of the stack minus the distance without removing it.
	 *
	 * @param distance The distance from the top of the stack.
	 * @return The value at the top of the stack minus the distance without removing it.
	 */
	Object peekStack(int distance) {
		return this.stack[this.stackSize - 1 - distance];
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
					throw new RuntimeError(
							"Subtraction must be between two numbers, instead got '" + a.getClass() + "' and '" + b.getClass() + "'.");
				}
			}
			case '*' -> {
				try {
					this.pushStack((double) a * (double) b);
				} catch (ClassCastException e) {
					throw new RuntimeError(
							"Multiplication must be between two numbers, instead got '" + a.getClass() + "' and '" + b.getClass() + "'.");
				}
			}
			case '/' -> {
				try {
					if ((double) b == 0) throw new RuntimeError("Cannot divide by 0.");
					this.pushStack((double) a / (double) b);
				} catch (ClassCastException e) {
					throw new RuntimeError(
							"Division must be between two numbers, instead got '" + a.getClass() + "' and '" + b.getClass() + "'.");
				}
			}
			case '%' -> {
				try {
					if ((double) b == 0) throw new RuntimeError("Cannot divide / modulo by 0.");
					this.pushStack((double) a % (double) b);
				} catch (ClassCastException e) {
					throw new RuntimeError(
							"Modulo must be between two numbers, instead got '" + a.getClass() + "' and '" + b.getClass() + "'.");
				}
			}
			case '^' -> {
				try {
					this.pushStack(Math.pow((double) a, (double) b));
				} catch (ClassCastException e) {
					throw new RuntimeError(
							"Exponents must be between two numbers, instead got '" + a.getClass() + "' and '" + b.getClass() + "'.");
				}
			}
			case '>' -> {
				try {
					this.pushStack((double) a > (double) b);
				} catch (ClassCastException e) {
					throw new RuntimeError(
							"Comparison using '>' must be between two numbers, instead got '" + a.getClass() + "' and '" + b.getClass() + "'.");
				}
			}
			case '<' -> {
				try {
					this.pushStack((double) a < (double) b);
				} catch (ClassCastException e) {
					throw new RuntimeError(
							"Comparison using '<' must be between two numbers, instead got '" + a.getClass() + "' and '" + b.getClass() + "'.");
				}
			}
			case 'g' -> { // >=
				try {
					this.pushStack((double) a >= (double) b);
				} catch (ClassCastException e) {
					throw new RuntimeError(
							"Comparison using '>=' must be between two numbers, instead got '" + a.getClass() + "' and '" + b.getClass() + "'.");
				}
			}
			case 'l' -> { // <=
				try {
					this.pushStack((double) a <= (double) b);
				} catch (ClassCastException e) {
					throw new RuntimeError(
							"Comparison using '<=' must be between two numbers, instead got '" + a.getClass() + "' and '" + b.getClass() + "'.");
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

	/**
	 * Checks if a double value is a whole number
	 *
	 * @param d Double being checked for a whole number
	 * @return The boolean value of the double being a whole number.
	 */
	private static boolean isWhole(double d) {
		return d == Math.floor(d);
	}

	/**
	 * Checks if a double value is negative
	 *
	 * @param d Double being checked for a negative value.
	 * @return The boolean value of the double being negative
	 */
	private static boolean isNegative(double d) {
		return d < 0;
	}

	/**
	 * Gets a string value for an object passed in.
	 *
	 * @param object The object being stringified.
	 * @return The string value of the object.
	 */
	public static String stringify(Object object) {
		if (object == null) return "null";

		if (object instanceof Double) {
			String text = object.toString();
			if (text.endsWith(".0")) {
				text = text.substring(0, text.length() - 2);
			}
			return text;
		}

		return object.toString();
	}
}
