package com.workert.robotics.base.roboscript;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public abstract class RoboScript {
	private final Interpreter interpreter = new Interpreter(this);

	private boolean hadError = false;

	public RoboScript() {
		this.defineDefaultFunctions();
	}

	/**
	 * Registers a function for use by this RoboScript instance.<p> The provided arguments from the {@link BiFunction} may
	 * be an empty array if no arguments are provided.
	 *
	 * @param name     the name of the command, like <code>goTo</code> for
	 *                 <code>robot.goTo(x, y, z)</code>. May only contain a-Z and should start with a lowercase letter.
	 * @param function a {@link BiFunction} with two arguments: the Interpreter and a {@link List} with all
	 *                 provided arguments to the command. May return an object.
	 */
	public final void defineFunction(String name, int expectedArgumentSize, BiFunction<Interpreter, List<Object>, Object> function) {
		this.interpreter.environment.define(name, new RoboScriptCallable() {
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
		}, false);
	}

	/**
	 * Scans, parses, resolves and interprets a string <i>asynchronously</i>.<p>
	 * This method won't block the thread and can be called from the main game thread.
	 *
	 * @param source the string to execute. May contain multiple statements.
	 */
	public final void runString(String source) {
		this.interpreter.reset();
		new Thread(() -> {
			this.hadError = false;

			Scanner scanner = new Scanner(this, source);
			List<Token> tokens = scanner.scanTokens();

			Parser parser = new Parser(this, tokens);
			List<Statement> statements = parser.parse();

			// Stop if there was a syntax error.
			if (this.hadError) return;

			Resolver resolver = new Resolver(this.interpreter);
			resolver.resolve(statements);

			if (this.hadError) return;

			this.interpreter.interpret(statements);

		}).start();
	}

	/**
	 * Interprets a function call <i>asynchronously</i>.<p>
	 * This method won't block the thread and can be called from the main game thread.<p>
	 * It will be called from a new thread and <i>not</i> the same as the already running program!
	 *
	 * @param function the function identifier to execute.
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
	 * If you override this method to define own functions <b>do not forget</b> to call <code>super.defineDefaultFunctions()</code> at the end of your code!
	 */
	public void defineDefaultFunctions() {
		DefaultFunctionHelper.defineDefaultFunctions(this);
	}

	public final Map<String, RoboScriptVariable> getVariables() {
		return this.interpreter.getValues();
	}

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

	final void runtimeError(RuntimeError error) {
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

	public abstract void print(String message);

	public abstract void reportCompileError(String error);

	public final void requestStop() {
		this.interpreter.requestStop();
	}
}
