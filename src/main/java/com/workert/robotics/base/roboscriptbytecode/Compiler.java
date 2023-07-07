package com.workert.robotics.base.roboscriptbytecode;
import java.util.ArrayList;
import java.util.List;

import static com.workert.robotics.base.roboscriptbytecode.OpCode.OP_CONSTANT;
import static com.workert.robotics.base.roboscriptbytecode.OpCode.OP_RETURN;
import static com.workert.robotics.base.roboscriptbytecode.Token.TokenType.EOF;
import static com.workert.robotics.base.roboscriptbytecode.Token.TokenType.ERROR;

public final class Compiler {
	// ⚠️⚠️⚠️⚠️i have no idea what im doing        ⚠️⚠️⚠️⚠️

	final RoboScript roboScriptInstance;
	Scanner scanner;
	Chunk chunk = new Chunk();
	Token current;
	Token previous;
	List<Token> tokens = new ArrayList<>();


	Compiler(RoboScript roboScriptInstance) {
		this.roboScriptInstance = roboScriptInstance;
	}

	private Chunk getCurrentChunk() {
		return this.chunk;
	}


	protected void compile(String source) {
		Scanner scanner = new Scanner(source);
		this.scanner = new Scanner(source);
		this.advance();
		this.expression();
		this.consumeIfMatches(EOF, "Expect end of expression.");
	}

	private void endCompiler() {
		this.emitReturn();
	}

	private void expression() {

	}

	private void number() {
		double value = Double.parseDouble(this.previous.lexeme);

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
			this.error("Too many constants in one chunk.");
			return;
		}
		this.emitBytes(OP_CONSTANT, (byte) constant);
	}


	private void advance() {
		this.previous = this.current;

		while (true) {
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


	private static class CompileError extends RuntimeException {
	}


}
