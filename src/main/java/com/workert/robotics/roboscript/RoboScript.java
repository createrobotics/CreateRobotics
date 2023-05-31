package com.workert.robotics.roboscript;

import java.util.List;
import java.util.function.BiFunction;

public class RoboScript {
	private final Interpreter interpreter = new Interpreter(this);

	private boolean hadError = false;

	/**
	 * Registers a function for use by this RoboScript instance.<br> The provided arguments from the {@link BiFunction} may
	 * be an empty array if no arguments are provided.
	 *
	 * @param name     the name of the command, like <code>goTo</code> for
	 *                 <code>robot.goTo(x, y, z)</code>. May only contain a-Z and should start with a lowercase letter.
	 * @param function a {@link BiFunction} with two arguments: the Interpreter and a {@link List} with all
	 *                 provided arguments to the command. May return an object.
	 */
	public void defineFunction(String name, int expectedArgumentSize, BiFunction<Interpreter, List<Object>, Object> function) {
		this.interpreter.globals.define(name, new RoboScriptCallable() {
			@Override
			public int expectedArgumentSize() {
				return expectedArgumentSize;
			}

			@Override
			public Object call(Interpreter interpreter, List<Object> arguments) {
				return function.apply(interpreter, arguments);
			}

			@Override
			public String toString() {
				return "<native function " + name + ">";
			}
		});
	}

	public void run(String source) {
		this.hadError = false;

		Scanner scanner = new Scanner(this, source);
		List<Token> tokens = scanner.scanTokens();

		Parser parser = new Parser(this, tokens);
		List<Stmt> statements = parser.parse();

		// Stop if there was a syntax error.
		if (this.hadError) return;

		Resolver resolver = new Resolver(this.interpreter);
		resolver.resolve(statements);

		if (this.hadError) return;

		this.interpreter.interpret(statements);
	}

	public void error(Token token, String message) {
		if (token.type == Token.TokenType.EOF) {
			this.report(token.line, " at end", message);
		} else {
			this.report(token.line, " at '" + token.lexeme + "'", message);
		}
	}

	public void error(int line, String message) {
		this.report(line, "", message);
	}

	public void runtimeError(RuntimeError error) {
		System.err.println("[line " + error.token.line + "] Runtime Error: " + error.getMessage());
	}

	private void report(int line, String where, String message) {
		System.err.println("[line " + line + "] Error" + where + ": " + message);
		this.hadError = true;
	}
}
