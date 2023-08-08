package com.workert.robotics.base.roboscriptbytecode;
import java.util.List;
import java.util.Map;

import static com.workert.robotics.base.roboscriptbytecode.OpCode.*;

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
	RoboScript.NativeFunction[] nativeFunctions = new RoboScript.NativeFunction[Short.MAX_VALUE];

	/**
	 * The amount of all Native Functions in the `nativeFunctions` array.
	 */
	int nativeFunctionSize = 0;

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
		try {
			this.run();
		} catch (RuntimeError e) {
			System.err.println("[line " + this.chunk.getLine(this.instructionPointer) + "] " + e.message);
		}

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

				case OP_GET_LOCAL -> this.pushStack(this.stack[this.basePointer + this.readByte()]);

				case OP_SET_LOCAL -> this.stack[this.basePointer + this.readByte()] = this.popStack();

				case OP_GET_NATIVE -> this.pushStack(this.nativeFunctions[this.readByte()]);

				case OP_GET_GLOBAL -> this.pushStack(this.readGlobalVariable());

				case OP_DEFINE_GLOBAL -> this.globalVariables[this.readByte()] = this.popStack();

				case OP_SET_GLOBAL -> this.globalVariables[this.readByte()] = this.peekStack();

				case OP_EQUAL -> this.binaryOperation('=');

				case OP_NOT_EQUAL -> this.binaryOperation('n');

				case OP_LESS -> this.binaryOperation('<');

				case OP_LESS_EQUAL -> this.binaryOperation('l');

				case OP_GREATER -> this.binaryOperation('>');

				case OP_GREATER_EQUAL -> this.binaryOperation('g');

				case OP_ADD -> this.binaryOperation('+');

				case OP_INCREMENT_GLOBAL -> {
					byte global = this.readByte();
					if (!(this.globalVariables[global] instanceof Double increment))
						throw new RuntimeError("Incrementing variable must be a number.");

					this.globalVariables[global] = increment + 1;
					this.pushStack(increment);
				}

				case OP_INCREMENT_LOCAL -> {
					byte local = this.readByte();
					if (!(this.stack[this.basePointer + local] instanceof Double increment))
						throw new RuntimeError("Incrementing variable must be a number.");
					this.stack[this.basePointer + local] = increment + 1;
					this.pushStack(increment);
				}

				case OP_INCREMENT_MAP -> {
					Object key = this.popStack();
					Object gettable = this.popStack();

					if (gettable instanceof Map map) {
						if (!(map.get(key) instanceof Double increment))
							throw new RuntimeError("Incrementing variable must be a number.");
						map.put(key, increment + 1);
						this.pushStack(increment);
					} else if (gettable instanceof List list) {
						if (!(key instanceof Double d)) throw new RuntimeError(
								"Index value for list must be a whole number greater or equal to 0.");

						if (!isWhole(d) || isNegative(d)) throw new RuntimeError(
								"Index value for list must be a whole number greater or equal to 0.");

						if (d >= list.size())
							throw new RuntimeError("List index out of range of '" + (list.size() - 1) + "'.");

						if (!(list.get((int) Math.round(d)) instanceof Double increment))
							throw new RuntimeError("Incrementing variable must be a number.");

						list.set((int) Math.round(d), increment + 1);
						this.pushStack(increment);
					} else {
						throw new RuntimeError("Can only get objects from maps and lists.");
					}
				}

				case OP_INCREMENT_CLASS -> {
					String key = (String) this.popStack();
					Object gettable = this.popStack();

					if (!(gettable instanceof RoboScriptObject object))
						throw new RuntimeError(
								"Can only use '.' to set fields from an object. Instead got '" + gettable.getClass() + "'.");
					if (!object.settable)
						throw new RuntimeError("Cannot edit fields of a 'super' keyword.");
					if (!object.fields.containsKey(key))
						throw new RuntimeError("Object does not contain field '" + key + "'.");
					if (!(object.fields.get(key) instanceof Double increment))
						throw new RuntimeError("Incrementing field must be a number.");
					this.pushStack(increment);
					object.fields.put(key, increment + 1);
				}

				case OP_DECREMENT_GLOBAL -> {
					byte global = this.readByte();
					if (!(this.globalVariables[global] instanceof Double increment))
						throw new RuntimeError("Decrementing variable must be a number.");

					this.globalVariables[global] = increment - 1;
					this.pushStack(increment);
				}

				case OP_DECREMENT_LOCAL -> {
					byte local = this.readByte();
					if (!(this.stack[this.basePointer + local] instanceof Double increment))
						throw new RuntimeError("Decrementing variable must be a number.");
					this.stack[this.basePointer + local] = increment - 1;
					this.pushStack(increment);
				}

				case OP_DECREMENT_MAP -> {
					Object key = this.popStack();
					Object gettable = this.popStack();

					if (gettable instanceof Map map) {
						if (!(map.get(key) instanceof Double increment))
							throw new RuntimeError("Decrementing variable must be a number.");
						map.put(key, increment - 1);
						this.pushStack(increment);
					} else if (gettable instanceof List list) {
						if (!(key instanceof Double d)) throw new RuntimeError(
								"Index value for list must be a whole number greater or equal to 0.");

						if (!isWhole(d) || isNegative(d)) throw new RuntimeError(
								"Index value for list must be a whole number greater or equal to 0.");

						if (d >= list.size())
							throw new RuntimeError("List index out of range of '" + (list.size() - 1) + "'.");

						if (!(list.get((int) Math.round(d)) instanceof Double increment))
							throw new RuntimeError("Decrementing variable must be a number.");

						list.set((int) Math.round(d), increment - 1);
						this.pushStack(increment);
					} else {
						throw new RuntimeError("Can only get objects from maps and lists.");
					}
				}

				case OP_DECREMENT_CLASS -> {
					String key = (String) this.popStack();
					Object gettable = this.popStack();

					if (!(gettable instanceof RoboScriptObject object))
						throw new RuntimeError(
								"Can only use '.' to set fields from an object. Instead got '" + gettable.getClass() + "'.");
					if (!object.settable)
						throw new RuntimeError("Cannot edit fields of a 'super' keyword.");
					if (!object.fields.containsKey(key))
						throw new RuntimeError("Object does not contain field '" + key + "'.");
					if (!(object.fields.get(key) instanceof Double increment))
						throw new RuntimeError("Decrementing field must be a number.");
					this.pushStack(increment);
					object.fields.put(key, increment - 1);
				}

				case OP_SUBTRACT -> this.binaryOperation('-');

				case OP_MULTIPLY -> this.binaryOperation('*');

				case OP_DIVIDE -> this.binaryOperation('/');

				case OP_MODULO -> this.binaryOperation('%');

				case OP_POWER -> this.binaryOperation('^');

				case OP_NOT -> this.stack[this.stackSize - 1] = !isTruthy(this.peekStack());

				case OP_NEGATE -> {
					if (!(this.peekStack() instanceof Double d)) throw new RuntimeError("Can only negate numbers.");
					this.stack[this.stackSize - 1] = -(double) d;
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
						this.popStack();
						this.pushStack(returnValue);
						break;
					}

					if (callable instanceof RoboScriptClass clazz) {
						RoboScriptObject object = new RoboScriptObject(clazz, true);
						if ((callable = this.getFunctionInClass(clazz, object, "init")) != null) {
							this.stack[this.stackSize - 1 - argumentCount] = object;
						} else {
							this.pushStack(object);
							break;
						}
					}


					if (!(callable instanceof RoboScriptFunction function)) {
						if (callable != null)
							throw new RuntimeError(
									"Can only call functions, instead got '" + callable.getClass() + "'.");
						else
							throw new RuntimeError("Can only call functions, instead got 'null'.");
					}
					if (argumentCount != function.argumentCount)
						throw new RuntimeError(
								"Expected '" + function.argumentCount + "' arguments but got '" + argumentCount + "'.");

					if (callable instanceof RoboScriptMethod method) {
						this.pushStack(method.instance);
						argumentCount++;
						if (method.instance.clazz.superclass != null) {
							this.pushStack(new RoboScriptObject(method.instance.clazz.superclass, false));
							argumentCount++;
						}
					}

					// push return address and base pointer
					this.pushStack(this.instructionPointer);
					this.pushStack(this.basePointer);

					this.instructionPointer = function.address;
					this.basePointer = this.stackSize - argumentCount - 2;
				}

				case OP_RETURN -> {
					byte argCount = this.readByte();
					Object returnValue = this.popStack();
					this.basePointer = (int) this.popStack();
					this.instructionPointer = (int) this.popStack();
					this.stackSize -= argCount; // pops args
					if (this.peekStack() instanceof RoboScriptFunction) {
						this.stackSize--; // pops function
						this.pushStack(returnValue);
					} else if (!(this.peekStack() instanceof RoboScriptObject)) {
						throw new IllegalArgumentException(
								"Expected a function or object in this place. Rework the compiler.");
					}
				}

				case OP_MAKE_MAP -> {
					byte mapSize = this.readByte();
					Object puttable = this.peekStack(mapSize * 2);
					for (int i = 0; i < mapSize; i++) {
						Object value = this.popStack();
						Object key = this.popStack();

						if (puttable instanceof Map map) map.put(key, value);
						else throw new IllegalArgumentException("Unable to find map.");
					}
				}

				case OP_MAKE_LIST -> {
					byte listSize = this.readByte();
					Object puttable = this.peekStack(listSize);
					if (!(puttable instanceof List list))
						throw new IllegalArgumentException("Unable to find list.");
					for (int i = listSize - 1; i >= 0; i--) {
						list.add(this.peekStack(i));
					}
					this.stackSize -= listSize;
				}

				case OP_GET_MAP -> {
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
						if (!(key instanceof Double d))
							throw new RuntimeError(
									"Index value for list must be a whole number greater or equal to 0.");
						if (!isWhole(d) || isNegative(d)) throw new RuntimeError(
								"Index value for list must be a whole number greater or equal to 0.");
						if (d >= list.size())
							throw new RuntimeError("List index out of range of '" + (list.size() - 1) + "'.");
						this.pushStack(list.get((int) Math.round(d)));
					} else {
						throw new RuntimeError(
								"Can only get objects from maps, instead getting from '" + gettable.getClass() + "'.");
					}
				}

				case OP_SET_MAP -> {
					Object value = this.popStack();
					Object key = this.popStack();
					Object settable = this.popStack();

					this.pushStack(value);

					if (settable instanceof Map map) {
						map.put(key, value);
					} else if (settable instanceof List list) {

						if (!(key instanceof Double d))
							throw new RuntimeError(
									"Index value for list must be a whole number greater or equal to 0.");
						if (!isWhole(d) || isNegative(d)) throw new RuntimeError(
								"Index value for list must be a whole number greater or equal to 0.");
						if (d >= list.size())
							throw new RuntimeError("List index out of range of '" + (list.size() - 1) + "'.");
						list.set((int) Math.round(d), value);
					} else {
						throw new RuntimeError(
								"Can only get objects from maps, instead getting from '" + settable.getClass() + "'.");
					}
				}

				case OP_GET_CLASS -> {
					byte keep = this.readByte();

					String key = (String) this.peekStack();
					Object gettable = this.peekStack(1);

					if (keep == 0) {
						key = (String) this.popStack();
						gettable = this.popStack();
					}

					if (!(gettable instanceof RoboScriptObject object))
						throw new RuntimeError(
								"Can only use '.' to get fields from an object. Instead got '" + gettable.getClass() + "'.");
					if (object.settable)
						this.pushStack(this.getFieldInObject(object, key));
					else this.pushStack(this.getFunctionInClass(object.clazz, object, key));
				}

				case OP_SET_CLASS -> {
					Object value = this.popStack();
					String key = (String) this.popStack();
					Object settable = this.popStack();
					this.pushStack(value);
					if (!(settable instanceof RoboScriptObject object))
						throw new RuntimeError(
								"Can only use '.' to set fields from an object. Instead got '" + settable.getClass() + "'.");
					if (!object.settable)
						throw new RuntimeError("Cannot edit fields of a 'super' keyword.");
					object.fields.put(key, value);
				}
				case OP_INHERIT -> {
					Object superclassObject = this.popStack();
					RoboScriptClass clazz = (RoboScriptClass) this.peekStack();

					if (!(superclassObject instanceof RoboScriptClass superclass))
						throw new RuntimeError("Superclass does not exist.");

					clazz.superclass = superclass;
				}
				case OP_END -> {
					return;
				}
			}
		}
	}

	/**
	 * Gets a field and binds it to the object if the field is a function.
	 *
	 * @param object    The RoboScriptObject the field is being gotten from.
	 * @param fieldName The name of the field.
	 * @return A field from the object or a binded method from an objects function.
	 */
	private Object getFieldInObject(RoboScriptObject object, String fieldName) {
		if (object.fields.containsKey(fieldName))
			return object.fields.get(fieldName);
		RoboScriptMethod function = this.getFunctionInClass(object.clazz, object, fieldName);
		if (function == null)
			throw new RuntimeError("Class does not contain field '" + fieldName + "'.");
		return function;
	}

	/**
	 * Gets a function from a class and if it is not found the function will be called again for the superclass.
	 *
	 * @param clazz     The class the function is being gotten from.
	 * @param object    The object the function should be bound to.
	 * @param fieldName The name of the function
	 * @return The method after the function is bound, or null if the function is never found.
	 */
	private RoboScriptMethod getFunctionInClass(RoboScriptClass clazz, RoboScriptObject object, String fieldName) {
		if (clazz.functions.containsKey(fieldName))
			return new RoboScriptMethod(clazz.functions.get(fieldName), object);
		if (clazz.superclass != null) {
			RoboScriptMethod method = this.getFunctionInClass(clazz.superclass, object, fieldName);
			if (method == null) throw new RuntimeError("Superclass does not contain function '" + fieldName + "'.");
			return method;
		}
		return null;
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
		} else if (!(a instanceof Double && b instanceof Double))
			throw new RuntimeError("Addition must be between two numbers or a string.");
		this.pushStack((double) a + (double) b);
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
				if (!(a instanceof Double && b instanceof Double))
					throw new RuntimeError("Subtraction must be between two numbers.");
				this.pushStack((double) a - (double) b);
			}
			case '*' -> {
				if (!(a instanceof Double && b instanceof Double))
					throw new RuntimeError("Multiplication must be between two numbers.");
				this.pushStack((double) a * (double) b);
			}
			case '/' -> {
				if (!(a instanceof Double && b instanceof Double))
					throw new RuntimeError("Multiplication must be between two numbers.");
				if ((double) b == 0) throw new RuntimeError("Cannot divide by 0.");
				this.pushStack((double) a / (double) b);
			}
			case '%' -> {
				if (!(a instanceof Double && b instanceof Double))
					throw new RuntimeError("Modulo must be between two numbers.");
				if ((double) b == 0) throw new RuntimeError("Cannot divide by 0.");
				this.pushStack((double) a % (double) b);
			}
			case '^' -> {
				if (!(a instanceof Double && b instanceof Double))
					throw new RuntimeError("Exponents must be between two numbers.");
				this.pushStack(Math.pow((double) a, (double) b));
			}
			case '>' -> {
				if (!(a instanceof Double && b instanceof Double))
					throw new RuntimeError("Comparison using '>' must be between two numbers.");
				this.pushStack((double) a > (double) b);
			}
			case '<' -> {
				if (!(a instanceof Double && b instanceof Double))
					throw new RuntimeError("Comparison using '<' must be between two numbers.");
				this.pushStack((double) a < (double) b);
			}
			case 'g' -> { // >=
				if (!(a instanceof Double && b instanceof Double))
					throw new RuntimeError("Comparison using '>=' must be between two numbers.");
				this.pushStack((double) a >= (double) b);
			}
			case 'l' -> { // <=
				if (!(a instanceof Double && b instanceof Double))
					throw new RuntimeError("Comparison using '<=' must be between two numbers.");
				this.pushStack((double) a <= (double) b);
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
}
