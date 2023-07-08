package com.workert.robotics.base.roboscriptbytecode;
import static com.workert.robotics.base.roboscriptbytecode.OpCode.*;
import static com.workert.robotics.base.roboscriptbytecode.Token.TokenType.ERROR;
import static com.workert.robotics.base.roboscriptbytecode.Token.TokenType.RIGHT_PAREN;

public final class Compiler {

	final RoboScript roboScriptInstance;
	Scanner scanner;
	Chunk chunk = new Chunk();
	Token current;
	Token previous;


	Compiler(RoboScript roboScriptInstance) {
		this.roboScriptInstance = roboScriptInstance;
	}


	/**
	 * The current chunk the compiler is writing to.
	 *
	 * @return The current chunk the compiler is writing to.
	 */
	private Chunk getCurrentChunk() {
		return this.chunk;
	}


	/**
	 * Scans tokens and writes to a chunk.
	 *
	 * @param source The source code of the program.
	 */
	protected void compile(String source) {
		try {
			this.scanner = new Scanner(source);

			// Get a chunk value for current. Previous is still 'null'.
			this.advance();

			// Looks for a single expression and ends the compiler. Temporary
			this.expression();
			this.endCompiler();

		} catch (CompileError e) {
			// synchronize eventually
		}
	}


	/**
	 * Ends the compiler.
	 */
	private void endCompiler() {
		this.emitReturn();
	}

	/**
	 * Performs binary operations such as `+`, `-`, `*`, `/`, `^`, and `%`.
	 * Called externally through a parse rule.
	 */
	protected void binary() {
		Token.TokenType operatorType = this.previous.type;
		ParseRule rule = operatorType.getParseRule();
		this.parsePrecedence(rule.precedence + 1);

		switch (operatorType) {
			case PLUS -> this.emitByte(OP_ADD);
			case MINUS -> this.emitByte(OP_SUBTRACT);
			case STAR -> this.emitByte(OP_MULTIPLY);
			case SLASH -> this.emitByte(OP_DIVIDE);
			default -> {
				return;
			}
		}
	}

	/**
	 * Parses an expression
	 */
	private void expression() {
		this.parsePrecedence(Precedence.ASSIGNMENT);
	}

	/**
	 * Parses a constant and writes it to the current chunk
	 */
	protected void number() {
		double value = Double.parseDouble(this.previous.lexeme);
		this.emitConstant(value);
	}

	/**
	 * Parses an expression starting from when the `(` has already been consumed.
	 * Expects a `)` after an expression.
	 */
	protected void grouping() {
		this.expression();
		this.consumeIfMatches(RIGHT_PAREN, "Expect ')' after expression.");

	}


	/**
	 * Performs unary operations such as negation (`-`) and not (`!`).
	 */
	protected void unary() {
		Token.TokenType operatorType = this.previous.type;
		this.parsePrecedence(Precedence.UNARY);
		switch (operatorType) {
			case MINUS -> this.emitByte(OP_NEGATE);
			case BANG -> this.emitByte(OP_NOT);
			default -> {
				return; // unreachable
			}
		}
	}


	private void parsePrecedence(int precedence) {
		this.advance();
		ParseFunction prefixRule = this.previous.type.getParseRule().prefix;
		if (prefixRule == null) {
			throw this.error("Expect expression.");
		}
		prefixRule.apply(this);
		while (precedence <= this.current.type.getParseRule().precedence) {
			this.advance();
			ParseFunction infixRule = this.previous.type.getParseRule().infix;
			infixRule.apply(this);
		}
	}


	private void emitByte(byte b) {
		this.getCurrentChunk().writeCode(b, this.previous.line);
	}

	private void emitBytes(byte... b) {
		for (byte currentByte : b) {
			this.emitByte(currentByte);
		}
	}

	private void emitReturn() {
		this.emitByte(OP_RETURN);
	}

	private void emitConstant(Object value) {
		int constant = this.getCurrentChunk().addConstant(value);
		if (constant > 255) {
			throw this.error("Too many constants in one chunk.");
		}
		this.emitBytes(OP_CONSTANT, (byte) constant);
	}


	private void advance() {
		this.previous = this.current;

		for (; ; ) {
			this.current = this.scanner.scanToken();
			if (this.current.type != ERROR) break;

			throw this.errorAtCurrent(this.current.lexeme);
		}
	}

	private void consumeIfMatches(Token.TokenType type, String message) {
		if (this.current.type == type) {
			this.advance();
			return;
		}
		throw this.errorAtCurrent(message);
	}


	private CompileError error(String message) {
		return this.errorAt(this.previous, message);
	}

	private CompileError errorAtCurrent(String message) {
		return this.errorAt(this.current, message);
	}

	private CompileError errorAt(Token token, String message) {
		String finalMessage = "Error";
		if (token.type == Token.TokenType.EOF) {
			finalMessage += " at end";
		} else if (token.type == Token.TokenType.ERROR) {

		} else {
			finalMessage += " at " + token.lexeme;
		}
		finalMessage += ": '" + message + "'";
		this.roboScriptInstance.reportCompileError(token.line, message);
		return new CompileError();
	}


	protected static class CompileError extends RuntimeException {
	}

	protected static class ParseRule {
		ParseFunction prefix;
		ParseFunction infix;
		byte precedence;


		ParseRule(ParseFunction prefix, ParseFunction infix, byte precedence) {
			this.prefix = prefix;
			this.infix = infix;
			this.precedence = precedence;
		}
	}

	protected interface Precedence {
		byte NONE = 0;
		byte ASSIGNMENT = 1; // =
		byte OR = 2; // or
		byte AND = 3; // and
		byte EQUALITY = 4; // == !=
		byte COMPARISON = 5; // < > <= >=
		byte TERM = 6; // + -
		byte FACTOR = 7; // * /
		byte UNARY = 8; // ! -
		byte CALL = 9; // . ()
		byte PRIMARY = 10;
	}


}
