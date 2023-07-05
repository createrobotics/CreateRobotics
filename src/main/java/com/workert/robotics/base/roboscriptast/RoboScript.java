package com.workert.robotics.base.roboscriptast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The RoboScript class represents an abstract base class for executing and interpreting RoboScript code.
 * <p>
 * It provides methods for defining functions, running code asynchronously, and managing variables.
 */
public abstract class RoboScript {

	private final Interpreter interpreter = new Interpreter(this);
	private boolean hadError = false;

	/**
	 * Creates a new instance of the RoboScript class and defines default functions.
	 */
	public RoboScript() {
		this.defineDefaultFunctions();
	}

	/**
	 * Registers a function for use by this RoboScript instance.
	 *
	 * @param name                 the name of the command. Must contain only alphabetic characters (a-Z) and start with a lowercase letter.
	 * @param expectedArgumentSize the expected number of arguments for the function.
	 * @param function             a BiFunction representing the function to be executed. It takes the Interpreter and a List of arguments and returns an object.
	 */
	public final void defineFunction(String name, int expectedArgumentSize, RoboScriptCallableFunction function) {
		this.interpreter.environment.define(name, defineCallable(name, expectedArgumentSize, function), false);
	}

	/**
	 * Creates a RoboScriptCallable object for a function.
	 *
	 * @param name                 the name of the function.
	 * @param expectedArgumentSize the expected number of arguments for the function.
	 * @param function             a RoboScriptCallableFunction representing the function to be executed.
	 * @return a RoboScriptCallable object.
	 */
	public static RoboScriptCallable defineCallable(String name, int expectedArgumentSize, RoboScriptCallableFunction function) {
		return new RoboScriptCallable() {
			@Override
			public int expectedArgumentSize() {
				return expectedArgumentSize;
			}

			@Override
			public Object call(Interpreter interpreter, List<Object> arguments, Token errorToken) {
				return function.apply(interpreter, arguments, errorToken);
			}

			@Override
			public String toString() {
				return "<native function " + name + ">";
			}
		};
	}

	/**
	 * Runs a string of RoboScript code asynchronously.
	 *
	 * @param source the string to execute, which may contain multiple statements.
	 */
	public final void runString(String source) {
		this.interpreter.reset();
		new Thread(() -> {
			this.hadError = false;

			Scanner scanner = new Scanner(this, source);
			List<Token> tokens = scanner.scanTokens();

			Parser parser = new Parser(this, tokens);
			List<Statement> statements = parser.parse();

			if (this.hadError) return;

			Resolver resolver = new Resolver(this.interpreter);
			resolver.resolve(statements);

			if (this.hadError) return;

			this.interpreter.interpret(statements);

		}).start();
	}

	/**
	 * Runs a function asynchronously.
	 *
	 * @param function  the function identifier to execute.
	 * @param arguments a List of arguments to be passed to the function.
	 */
	public final void runFunction(String function, List<Object> arguments) {
		CompletableFuture.runAsync(() -> {
			List<Expression> argumentExpressionList = new ArrayList<>();
			for (Object argument : arguments) {
				argumentExpressionList.add(new Expression.Literal(argument));
			}
			this.interpreter.interpret(List.of(new Statement.Expression(new Expression.Call(
					new Expression.Variable(new Token(Token.TokenType.IDENTIFIER, function, function, 0)),
					new Token(Token.TokenType.LEFT_PAREN, "(", "(", 0), argumentExpressionList))));
		});
	}

	/**
	 * Defines default functions for the RoboScript instance.
	 * If overriding this method, make sure to call super.defineDefaultFunctions() at the end of the implementation.
	 */
	public void defineDefaultFunctions() {
		DefaultFunctionHelper.defineDefaultFunctions(this);
	}

	/**
	 * Retrieves the persistent variables from the interpreter.
	 *
	 * @return a Map containing the persistent variables.
	 */
	public final Map<String, RoboScriptVariable> getPersistentVariables() {
		Map<String, RoboScriptVariable> persistentVariables = new HashMap<>();
		for (Map.Entry<String, RoboScriptVariable> entry : this.interpreter.getValues().entrySet()) {
			if (entry.getValue().staticc) persistentVariables.put(entry.getKey(), entry.getValue());
		}
		return persistentVariables;
	}

	/**
	 * Puts the given variables into the interpreter's environment.
	 *
	 * @param values a Map containing the variables to be put.
	 */
	public final void putVariables(Map<String, RoboScriptVariable> values) {
		for (Map.Entry<String, RoboScriptVariable> entry : values.entrySet()) {
			this.interpreter.environment.define(entry.getKey(), entry.getValue(), false);
		}
	}

	final void reportCompileError(Token token, String message) {
		if (token.type == Token.TokenType.EOF) {
			this.report(token.line, " at end", message);
		} else {
			this.report(token.line, " at '" + token.lexeme + "'", message);
		}
	}

	final void reportCompileError(int line, String message) {
		this.report(line, "", message);
	}

	final void runtimeError(RoboScriptRuntimeError error) {
		if (error.token.line == 0) {
			this.reportCompileError("An external error occurred (possibly a Signal): " + error.getMessage());
		} else {
			this.reportCompileError("[line " + error.token.line + "] Runtime Error: " + error.getMessage());
		}
	}

	private void report(int line, String where, String message) {
		this.reportCompileError("[line " + line + "] Error" + where + ": " + message);
		this.hadError = true;
	}

	/**
	 * Prints the given message.
	 *
	 * @param message the message to print.
	 */
	public abstract void print(String message);

	/**
	 * Reports a compile error.
	 *
	 * @param error the compile error message.
	 */
	public abstract void reportCompileError(String error);

	/**
	 * Requests the interpreter to stop execution.
	 */
	public final void requestStop() {
		this.interpreter.requestStop();
	}
}
