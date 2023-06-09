package com.workert.robotics.base.roboscript;

import com.workert.robotics.base.roboscript.ingame.ConsoleOutputProvider;
import com.workert.robotics.base.roboscript.ingame.VariableDataExternalSavingProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public abstract class RoboScript implements ConsoleOutputProvider, VariableDataExternalSavingProvider {
	private final Interpreter interpreter = new Interpreter(this);

	private boolean hadError = false;

	private boolean isRunning = false;

	private final String consoleOutput = "";

	private boolean printToJVMConsole;

	public RoboScript() {
		this(false);
	}

	public RoboScript(boolean printToJVMConsole) {
		this.printToJVMConsole = printToJVMConsole;
		this.defineDefaultFunctions();
	}


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

	public void defineDefaultFunctions() {
		this.defineFunction("print", 1, (interpreter, arguments) -> {
			this.printToConsole(Interpreter.stringify(arguments.get(0)) + "\n");
			return null;
		});
	}

	/**
	 * Scans, parses, resolves and interprets a string <i>asynchronously</i>.<br>
	 * This method won't block the thread and can be called from the main game thread.
	 *
	 * @param source the string to execute. May contain multiple statements.
	 */
	public void runString(String source) {
		new Thread(() -> {

			this.isRunning = true;
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

			this.isRunning = false;
		}).start();
	}

	/**
	 * Interprets a function call <i>asynchronously</i>.<br>
	 * This method won't block the thread and can be called from the main game thread.<br>
	 * It will be called from a new thread and <i>not</i> the same as the already running program!
	 *
	 * @param function the function identifier to execute.
	 */
	public void runFunction(String function, List<Object> arguments) {
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
		this.printToConsole("[line " + error.token.line + "] Runtime Error: " + error.getMessage() + "\n");
	}

	private void report(int line, String where, String message) {
		this.printToConsole("[line " + line + "] ERROR" + where + ": " + message + "\n");
		this.hadError = true;
	}

	private void printToConsole(String message) {
		if (this.printToJVMConsole) {
			System.out.print(message);
		} else {
			this.consoleOutput.concat(message);
		}
	}

	public void requestStop() {
		this.interpreter.requestStop();
	}

	@Override
	public String getConsoleOutput() {
		return this.consoleOutput;
	}

	@Override
	public RunningState getRunningState() {
		return this.isRunning ? RunningState.RUNNING : RunningState.STOPPED;
	}
}
