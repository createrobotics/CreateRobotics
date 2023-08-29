package com.workert.robotics.base.roboscript;
import com.workert.robotics.base.roboscript.util.RoboScriptArgumentPredicates;
import com.workert.robotics.base.roboscript.util.RoboScriptObjectConversions;

import java.util.*;

import static com.workert.robotics.base.roboscript.OpCode.*;

/**
 * <b>Warning!</b>
 * <p>
 * This Class contains code that is difficult to read and does <i>not</i> follow the Java Code Conventions.
 * <p>
 * It prioritizes high-speed execution, often using boilerplate alternatives instead of more concise code.
 * <p>
 * Do not reuse this code. Do not instantiate this class.<br>
 * Instead, look at RoboScript AST for better written but slower running Examples.
 * <p>
 * If you want to make your own Scripting Language the Create Robotics Team recommends reading <a href="https://craftinginterpreters.com/">Crafting Interpreters</a>, a Book which has helped us a lot with implementing RoboScript.
 */
public final class VirtualMachine {

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
	 * The constants defined during runtime
	 */
	final Object[] runtimeConstants = new Object[256];

	/**
	 * The size of runtimeConstants
	 */
	int runtimeConstantsSize = 0;

	/**
	 * The index of the current instruction.
	 */
	int ip = 0;

	/**
	 * The running state of WRITTEN program.
	 */
	boolean running = false;

	boolean threadRunning = false;

	boolean inSignal = false;


	/**
	 * The size of the stack when a new function is entered.
	 */
	int bp = 0;

	/**
	 * Variables defined in the global scope; Can be accessed from anywhere.
	 */
	private final Object[] globalVariables = new Object[256];

	/**
	 * Global functions that are defined natively and use Java code.
	 */
	RoboScriptNativeFunction[] nativeFunctions = new RoboScriptNativeFunction[Short.MAX_VALUE];

	/**
	 * Halts the program when true.
	 */
	boolean stopQueued = false;


	/**
	 * The amount of all Native Functions in the `nativeFunctions` array.
	 */
	int nativeFunctionSize = 0;

	Queue<ExecutingSignal> signalQueue = new LinkedList<>();

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
		this.ip = 0;
		this.bp = 3;
		this.stackSize = 0;
		try {
			this.running = true;
			this.threadRunning = true;
			this.pushStack(-1);
			this.pushStack(-1);
			this.pushStack(null);
			this.run();
			this.threadRunning = false;
		} catch (RuntimeError e) {
			this.roboScriptInstance.handleErrorMessage(
					"[line " + this.chunk.finalLines[this.ip] + "] " + e.message);
		}
	}

	private void interpretButDontRun() {
		this.ip = 0;
		this.stackSize = 0;
		this.bp = 0;
		try {
			this.threadRunning = true;
			this.run();
			this.threadRunning = false;
		} catch (RuntimeError e) {
			this.roboScriptInstance.handleErrorMessage(
					"[line " + this.chunk.finalLines[this.ip] + "] " + e.message);
		}
	}

	/**
	 * Executes the signal queue. Should only call while the program is running
	 */
	void executeSignal() {
		if (this.signalQueue.isEmpty() || this.inSignal) return;

		ExecutingSignal signal = this.signalQueue.remove();

		for (Object arg : signal.args) {
			this.pushStack(arg);
		}
		try {
			signal.callable.call(this, (byte) signal.args.length, true);
		} catch (RuntimeError e) {
			this.roboScriptInstance.handleErrorMessage(
					"[line " + this.chunk.finalLines[this.ip] + "] " + e.message);
		}
	}

	void addSignalToQueue(ExecutingSignal signal) {
		this.signalQueue.add(signal);
		if (this.threadRunning)
			return;

		new Thread(() -> {
			this.running = false; // it should be false already but its always nice to make sure
			this.threadRunning = true;
			this.executeSignal();
			this.threadRunning = false;
		}).start();
	}

	/**
	 * Makes the program stop after the current instruction is finished.
	 */
	void queueStop() {
		this.stopQueued = true;
	}

	/**
	 * The main part of the VM
	 */
	void run() {
		while (true) {
			if (this.stopQueued) {
				this.stopQueued = false;
				return;
			}
			this.executeSignal();
			switch (this.readByte()) {
				case OP_CONSTANT -> {
					Object o = this.chunk.finalConstants[this.readShort()];
					if (o instanceof List l) {
						this.pushStack(new ArrayList<Object>(l));
						break;
					}
					this.pushStack(o);
				}

				case OP_NULL -> this.pushStack(null);

				case OP_TRUE -> this.pushStack(true);

				case OP_FALSE -> this.pushStack(false);

				case OP_POP -> this.popStack();

				case OP_GET_LOCAL -> this.pushStack(this.stack[this.bp + this.readByte()]);

				case OP_SET_LOCAL -> this.stack[this.bp + this.readByte()] = this.peekStack();

				case OP_GET_NATIVE -> this.pushStack(this.nativeFunctions[this.readByte()]);

				case OP_GET_GLOBAL -> this.pushStack(this.globalVariables[this.readByte()]);

				case OP_DEFINE_GLOBAL -> this.globalVariables[this.readByte()] = this.popStack();

				case OP_SET_GLOBAL -> this.globalVariables[this.readByte()] = this.peekStack();

				case OP_ADD_CONSTANT -> this.pushConstant(this.popStack());

				case OP_GET_CONSTANT -> this.pushStack(this.runtimeConstants[this.readByte()]);

				case OP_EQUAL -> this.binaryEqual();

				case OP_NOT_EQUAL -> this.binaryNotEqual();

				case OP_LESS -> this.binaryLessThan();

				case OP_LESS_EQUAL -> this.binaryLessEqual();

				case OP_GREATER -> this.binaryGreaterThan();

				case OP_GREATER_EQUAL -> this.binaryGreaterEqual();

				case OP_ADD -> this.binaryAdd();

				case OP_INCREMENT_GLOBAL -> {
					byte global = this.readByte();
					if (!(this.globalVariables[global] instanceof Double increment))
						throw new RuntimeError("Incrementing variable must be a number.");

					this.globalVariables[global] = increment + 1;
					this.pushStack(increment);
				}

				case OP_INCREMENT_LOCAL -> {
					byte local = this.readByte();
					if (!(this.stack[this.bp + local] instanceof Double increment))
						throw new RuntimeError("Incrementing variable must be a number.");
					this.stack[this.bp + local] = increment + 1;
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
					RoboScriptField field = object.fields.get(key);
					if (field.isFinal) throw new RuntimeError("Field '" + key + "' is final and cannot be edited.");
					if (!(field.value instanceof Double increment))
						throw new RuntimeError("Incrementing field must be a number.");
					this.pushStack(increment);
					field.value = increment + 1;
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
					if (!(this.stack[this.bp + local] instanceof Double increment))
						throw new RuntimeError("Decrementing variable must be a number.");
					this.stack[this.bp + local] = increment - 1;
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
					RoboScriptField field = object.fields.get(key);
					if (field.isFinal) throw new RuntimeError("Field '" + key + "' is final and cannot be edited.");
					if (!(field.value instanceof Double increment))
						throw new RuntimeError("Decrementing field must be a number.");
					this.pushStack(increment);
					field.value = increment - 1;
				}

				case OP_SUBTRACT -> this.binarySubtract();

				case OP_MULTIPLY -> this.binaryMultiply();

				case OP_DIVIDE -> this.binaryDivision();

				case OP_MODULO -> this.binaryModulo();

				case OP_POWER -> this.binaryPower();

				case OP_NOT -> this.stack[this.stackSize - 1] = !isTruthy(this.peekStack());

				case OP_NEGATE -> {
					if (!(this.peekStack() instanceof Double d)) throw new RuntimeError("Can only negate numbers.");
					this.stack[this.stackSize - 1] = -(double) d;
				}

				case OP_JUMP -> {
					short offset = this.readShort();
					this.ip += offset;
				}

				case OP_JUMP_IF_FALSE -> {
					short offset = this.readShort();
					if (!isTruthy(this.peekStack())) this.ip += offset;
				}

				case OP_LOOP -> {
					short offset = this.readShort();
					this.ip -= offset;
				}

				case OP_CALL -> {
					byte argumentCount = this.readByte();

					Object callable = this.peekStack(argumentCount);
					if (callable instanceof RoboScriptCallable c)
						c.call(this, argumentCount, false);
					else if (callable != null)
						throw new RuntimeError("Can only call functions, instead got '" + callable.getClass() + "'.");
					else
						throw new RuntimeError("Can only call functions, instead got 'null'.");
				}

				case OP_RETURN -> {
					byte argCount = this.readByte();
					Object returnValue = this.popStack();
					this.bp = (int) this.popStack();
					this.ip = (int) this.popStack();
					if (this.bp == -1 && this.ip == -1) {
						this.running = false;
						this.inSignal = false;
						this.executeSignal();
						return;
					}
					this.stackSize -= argCount; // pops args
					if (this.peekStack() instanceof RoboScriptFunction f) {
						this.stackSize--; // pops function
						if (!f.runningAsSignal)
							this.pushStack(returnValue);
						else {
							f.runningAsSignal = false;
							this.inSignal = false;

						}
					} else if (!(this.peekStack() instanceof RoboScriptObject)) {
						throw new IllegalArgumentException(
								"Expected a function or object in this place. Rework the compiler. Got '" + this.peekStack()
										.getClass() + "'. At line " + this.chunk.finalLines[
										this.ip] + "'.");
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

					if (gettable instanceof List l) {
						this.pushStack(this.getListNative(l, key));
						break;
					}
					if (gettable instanceof String s) {
						this.pushStack(this.getStringNative(s, key));
						break;
					}
					if (gettable instanceof RoboScriptSignal s) {
						this.pushStack(this.getSignalNative(s, key));
						break;
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
					if (object.fields.containsKey(key) && object.fields.get(key).isFinal)


						if (object.fields.containsKey(key)) {
							if (object.fields.get(key).isFinal)
								throw new RuntimeError("Field '" + key + "' is final and cannot be edited.");
							else object.fields.get(key).value = value;
						} else object.fields.put(key, new RoboScriptField(value, false));
				}
				case OP_INHERIT -> {
					Object superclassObject = this.popStack();
					RoboScriptClass clazz = (RoboScriptClass) this.peekStack();

					if (!(superclassObject instanceof RoboScriptClass superclass))
						throw new RuntimeError("Superclass does not exist.");

					clazz.superclass = superclass;
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
			return object.fields.get(fieldName).value;
		RoboScriptCallable function = this.getFunctionInClass(object.clazz, object, fieldName);
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
	RoboScriptCallable getFunctionInClass(RoboScriptClass clazz, RoboScriptObject object, String fieldName) {
		if (clazz.functions.containsKey(fieldName)) {
			if (clazz.functions.get(fieldName) instanceof RoboScriptFunction f) {
				return new RoboScriptMethod(f, object);
			} else if (clazz.functions.get(fieldName) instanceof RoboScriptNativeFunction f) {
				return new RoboScriptNativeMethod(f, object);
			} else {
				throw new IllegalArgumentException(
						"why is there not a proper function in the functions part of a class??");
			}

		}
		if (clazz.superclass != null) {
			return this.getFunctionInClass(clazz.superclass, object, fieldName);
		}
		return null;
	}

	/**
	 * Gets a native method from RoboScript's built-in list "object".
	 *
	 * @param list The list the method is being gotten from.
	 * @param key  The name of the method being gotten.
	 * @return The method gotten from the list.
	 */
	private RoboScriptNativeMethod getListNative(List list, String key) {
		switch (key) {
			case "add", "append", "push" -> {
				RoboScriptNativeMethod method = new RoboScriptNativeMethod(list, (byte) 1);
				method.function = (vm, fun) -> {
					((List) method.instance).add(VirtualMachine.this.popStack());
					return null;
				};
				return method;
			}
			case "size" -> {
				RoboScriptNativeMethod method = new RoboScriptNativeMethod(list, (byte) 0);
				method.function = (vm, fun) -> (double) (((List) (method.instance)).size());
				return method;
			}
			case "pop" -> {
				RoboScriptNativeMethod method = new RoboScriptNativeMethod(list, (byte) 0);
				method.function = (vm, fun) -> {
					List l = ((List) method.instance);
					Object ret = l.get(l.size() - 1);
					l.remove(l.size() - 1);
					return ret;
				};
				return method;
			}
			case "peek" -> {
				RoboScriptNativeMethod method = new RoboScriptNativeMethod(list, (byte) 0);
				method.function = (vm, fun) -> ((List) method.instance).get(((List) method.instance).size() - 1);
				return method;
			}
			case "remove" -> {
				RoboScriptNativeMethod method = new RoboScriptNativeMethod(list, (byte) 1);
				method.function = (vm, fun) -> {
					int i = RoboScriptArgumentPredicates.asPositiveFullNumber(VirtualMachine.this.popStack(), true);
					List l = ((List) method.instance);
					if (i >= l.size()) throw new RuntimeError("List index out of range of '" + (l.size() - 1) + "'.");
					Object ret = l.get(l.size() - 1);
					l.remove(i);
					return ret;
				};
				return method;
			}
			default -> throw new RuntimeError("Built-in type 'List' does not have method '" + key + "'.");
		}
	}

	private RoboScriptNativeMethod getStringNative(String string, String key) {
		switch (key) {
			case "replaceAt" -> {
				RoboScriptNativeMethod method = new RoboScriptNativeMethod(string, (byte) 2);
				method.function = (vm, fun) -> {
					if (!(VirtualMachine.this.popStack() instanceof String s))
						throw new RuntimeError("Expected a string as the second argument of 'replaceAt'.");
					if (!(VirtualMachine.this.popStack() instanceof Double location))
						throw new RuntimeError("Expected a number as the first argument of 'replaceAt'.");

					if (!isWhole(location) || isNegative(location)) throw new RuntimeError(
							"Index value for string in first argument of 'replaceAt' must be a whole number greater or equal to 0.");
					if (location >= ((String) method.getInstance()).length())
						throw new RuntimeError(
								"String index in first argument of 'replaceAt' out of range of '" + (((String) method.getInstance()).length() - 1) + "'.");

					StringBuilder builder = new StringBuilder((String) method.getInstance());
					builder.replace((int) Math.round(location), (int) Math.round(location) + 1, s);
					return builder.toString();
				};
				return method;
			}

			case "split" -> {
				RoboScriptNativeMethod method = new RoboScriptNativeMethod(string, (byte) 1);
				method.function = (vm, fun) -> {
					if (!(VirtualMachine.this.popStack() instanceof String regex))
						throw new RuntimeError(
								"Expected a Regular Expression string as the argument of 'split'.");
					return Arrays.asList(((String) method.getInstance()).split(regex));
				};
				return method;
			}
			default -> throw new RuntimeError("Built-in type 'String' does not have method '" + key + "'.");
		}
	}

	private RoboScriptNativeMethod getSignalNative(RoboScriptSignal signal, String key) {
		switch (key) {
			case "connect" -> {
				RoboScriptNativeMethod method = new RoboScriptNativeMethod(signal, (byte) 1);
				method.function = (vm, fun) -> {
					if (!(this.popStack() instanceof RoboScriptCallable callable))
						throw new RuntimeError("Expected a function or method as the first argument of 'connect'.");
					if (callable instanceof RoboScriptClass)
						throw new RuntimeError("Expected a function or method as the first argument of 'connect'.");
					((RoboScriptSignal) method.getInstance()).callable = callable;
					return null;
				};
				return method;
			}
			default -> throw new RuntimeError("Built-in type 'Signal' does not have method '" + key + "'.");
		}
	}


	/**
	 * Reads the byte at the current index of instructionPointer.
	 *
	 * @return The byte at the current index of instructionPointer.
	 */
	private byte readByte() {
		return this.chunk.finalCode[this.ip++];
	}

	/**
	 * Reads the current and next bytes from instructionPointer and combines them into a short
	 *
	 * @return The short value of the current and next byte.
	 */
	private short readShort() {
		this.ip += 2;
		return (short) ((this.chunk.finalCode[this.ip - 2] << 8) | (this.chunk.finalCode[this.ip - 1] & 0xFF));
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
	public Object popStack() {
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

	void pushConstant(Object o) {
		this.runtimeConstants[this.runtimeConstantsSize++] = o;
	}

	/**
	 * Adds two numbers.
	 */
	private void binaryAdd() {
		Object b = this.popStack();
		Object a = this.popStack();
		if (a instanceof String || b instanceof String) {
			this.pushStack(RoboScriptObjectConversions.stringify(a) + RoboScriptObjectConversions.stringify(b));
			return;
		} else if (!(a instanceof Double && b instanceof Double))
			throw new RuntimeError("Addition must be between two numbers or a string.");
		this.pushStack((double) a + (double) b);
	}

	/**
	 * Subtracts two numbers.
	 */
	private void binarySubtract() {
		Object b = this.popStack();
		Object a = this.popStack();
		if (!(a instanceof Double && b instanceof Double))
			throw new RuntimeError("Subtraction must be between two numbers.");
		this.pushStack((double) a - (double) b);
	}

	/**
	 * Multiplies two numbers
	 */
	private void binaryMultiply() {
		Object b = this.popStack();
		Object a = this.popStack();
		if (a instanceof String string && b instanceof Double) {
			StringBuilder newString = new StringBuilder(string);
			for (int i = 1; i < (double) b; i++) {
				newString.append(string);
			}
			this.pushStack(newString.toString());
			return;
		} else if (a instanceof List l && b instanceof Double) {
			List toBeAdded = new ArrayList(l);
			for (int i = 1; i < (double) b; i++) {
				l.addAll(toBeAdded);
			}
			this.pushStack(l);
			return;
		}
		if (!(a instanceof Double && b instanceof Double))
			throw new RuntimeError("Multiplication must be between two numbers or an array and a number.");
		this.pushStack((double) a * (double) b);
	}

	/**
	 * Divides two numbers.
	 */
	private void binaryDivision() {
		Object b = this.popStack();
		Object a = this.popStack();
		if (!(a instanceof Double && b instanceof Double))
			throw new RuntimeError("Multiplication must be between two numbers.");
		if ((double) b == 0) throw new RuntimeError("Cannot divide by 0.");
		this.pushStack((double) a / (double) b);
	}

	/**
	 * Modulo-s two numbers.
	 */
	private void binaryModulo() {
		Object b = this.popStack();
		Object a = this.popStack();
		if (!(a instanceof Double && b instanceof Double))
			throw new RuntimeError("Modulo must be between two numbers.");
		if ((double) b == 0) throw new RuntimeError("Cannot divide by 0.");
		this.pushStack((double) a % (double) b);
	}

	/**
	 * Puts a number power to the next.
	 */
	private void binaryPower() {
		Object b = this.popStack();
		Object a = this.popStack();
		if (!(a instanceof Double && b instanceof Double))
			throw new RuntimeError("Exponents must be between two numbers.");
		this.pushStack(Math.pow((double) a, (double) b));
	}

	/**
	 * Compares two numbers using >.
	 */
	private void binaryGreaterThan() {
		Object b = this.popStack();
		Object a = this.popStack();
		if (!(a instanceof Double && b instanceof Double))
			throw new RuntimeError("Comparison using '>' must be between two numbers.");
		this.pushStack((double) a > (double) b);
	}

	/**
	 * Compares two numbers using <.
	 */
	private void binaryLessThan() {
		Object b = this.popStack();
		Object a = this.popStack();
		if (!(a instanceof Double && b instanceof Double))
			throw new RuntimeError("Comparison using '<' must be between two numbers.");
		this.pushStack((double) a < (double) b);
	}

	/**
	 * Compares two numbers using >=.
	 */
	private void binaryGreaterEqual() {
		Object b = this.popStack();
		Object a = this.popStack();
		if (!(a instanceof Double && b instanceof Double))
			throw new RuntimeError("Comparison using '>=' must be between two numbers.");
		this.pushStack((double) a >= (double) b);
	}

	/**
	 * Compares two numbers using <=.
	 */
	private void binaryLessEqual() {
		Object b = this.popStack();
		Object a = this.popStack();
		if (!(a instanceof Double && b instanceof Double))
			throw new RuntimeError("Comparison using '<=' must be between two numbers.");
		this.pushStack((double) a <= (double) b);
	}

	/**
	 * Compares two numbers directly.
	 */
	private void binaryEqual() {
		Object b = this.popStack();
		Object a = this.popStack();
		this.pushStack(a.equals(b));
	}

	/**
	 * Compares two numbers directly and toggle the result.
	 */
	private void binaryNotEqual() {
		Object b = this.popStack();
		Object a = this.popStack();
		this.pushStack(!a.equals(b));
	}

	/**
	 * Returns the truthy value of the passed in object.
	 *
	 * @param o The object being evaluated.
	 * @return The truthy value of the passed in object.
	 */
	public static boolean isTruthy(Object o) {
		if (o == null) return false;
		if (o instanceof Boolean b) return b;
		if (o instanceof Double d) return d != 0;
		return true;
	}

	/**
	 * Checks if a double value is a whole number
	 *
	 * @param d Double being checked for a whole number
	 * @return The boolean value of the double being a whole number.
	 */
	public static boolean isWhole(double d) {
		return d == Math.floor(d);
	}

	/**
	 * Checks if a double value is negative
	 *
	 * @param d Double being checked for a negative value.
	 * @return The boolean value of the double being negative
	 */
	public static boolean isNegative(double d) {
		return d < 0;
	}


	@Override
	public String toString() {
		return "VirtualMachine, you must have really done something wrong for this to show up.";
	}


	static class ExecutingSignal {
		Object[] args;
		RoboScriptCallable callable;

		ExecutingSignal(RoboScriptCallable callable, Object[] args) {
			this.callable = callable;
			this.args = args;
		}
	}

	enum RunningState {
		STOPPED,
		RUNNING,
		RUNNING_SIGNAL,
		RUNNING_EXTERNAL_SIGNAL
	}
}
