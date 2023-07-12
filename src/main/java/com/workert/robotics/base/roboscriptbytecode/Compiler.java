package com.workert.robotics.base.roboscriptbytecode;
import java.util.HashMap;
import java.util.Map;

import static com.workert.robotics.base.roboscriptbytecode.OpCode.*;
import static com.workert.robotics.base.roboscriptbytecode.Token.TokenType.*;

public final class Compiler {
	final RoboScript roboScriptInstance;
	Scanner scanner;
	Chunk chunk = new Chunk();
	Token current;
	Token previous;

	private Map<String, Byte> globalVariableLookup = new HashMap<>();


	Compiler(RoboScript roboScriptInstance) {
		this.roboScriptInstance = roboScriptInstance;
	}

	private Chunk getCurrentChunk() {
		return this.chunk;
	}


	void compile(String source) {
		try {
			this.scanner = new Scanner(source);
			this.advance();
			while (!this.checkIfMatches(EOF)) {
				this.declaration();
			}
			this.endCompiler();
		} catch (CompileError e) {
			// synchronize eventually
		}

	}

	private void endCompiler() {
		this.emitReturn();
	}


	private void expression() {
		this.parsePrecedence(Precedence.ASSIGNMENT);
	}

	private void expressionStatement() {
		this.expression();
		this.consumeIfMatches(SEMICOLON, "Expect ';' after expression.");
		this.emitByte(OP_POP);
	}


	private void declaration() {
		try {

			if (this.checkIfMatches(VAR)) {
				this.varDeclaration();
			} else {
				this.statement();
			}

		} catch (CompileError e) {
			this.synchronize();
		}

	}

	private void varDeclaration() {
		byte global = this.parseVariable("Expect variable name.");

		if (this.checkIfMatches(EQUAL)) {
			this.expression();
		} else {
			this.emitByte(OP_NULL);
		}
		this.consumeIfMatches(SEMICOLON, "Expect ';' after variable declaration.");
		this.emitBytes(OP_DEFINE_GLOBAL, global);
	}

	private void statement() {
		if (false) {

		} else {
			this.expressionStatement();
		}
	}

	void variable(boolean canAssign) {
		byte variable;
		if (this.globalVariableLookup.containsKey(this.previous.lexeme)) {
			variable = this.globalVariableLookup.get(this.previous.lexeme);
			if (canAssign && this.checkIfMatches(EQUAL)) {
				this.expression();
				this.emitBytes(OP_SET_GLOBAL, variable);
			} else
				this.emitBytes(OP_GET_GLOBAL, variable);
		} else
			throw this.error("Variable '" + this.previous.lexeme + "' has not been defined.");
	}

	void grouping(boolean canAssign) {
		this.expression();
		this.consumeIfMatches(RIGHT_PAREN, "Expect ')' after expression.");
	}

	void number(boolean canAssign) {
		double value = Double.parseDouble(this.previous.lexeme);
		this.emitConstant(value);
	}

	void literal(boolean canAssign) {
		switch (this.previous.type) {
			case FALSE -> this.emitByte(OP_FALSE);
			case TRUE -> this.emitByte(OP_TRUE);
			case STRING_VALUE -> this.emitConstant(this.previous.lexeme);
			case NULL -> this.emitByte(OP_NULL);
			default -> {
				return; // unreachable
			}
		}
	}

	void unary(boolean canAssign) {
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

	void binary(boolean canAssign) {
		Token.TokenType operatorType = this.previous.type;
		ParseRule rule = operatorType.getParseRule();
		this.parsePrecedence(rule.precedence + 1);

		switch (operatorType) {
			case PLUS -> this.emitByte(OP_ADD);
			case MINUS -> this.emitByte(OP_SUBTRACT);
			case STAR -> this.emitByte(OP_MULTIPLY);
			case SLASH -> this.emitByte(OP_DIVIDE);
			case GREATER -> this.emitByte(OP_GREATER);
			case GREATER_EQUAL -> this.emitByte(OP_GREATER_EQUAL);
			case LESS -> this.emitByte(OP_LESS_EQUAL);
			case EQUAL_EQUAL -> this.emitByte(OP_EQUAL);
			case BANG_EQUAL -> this.emitByte(OP_NOT_EQUAL);
			default -> {
				return;
			}
		}
	}


	private void parsePrecedence(int precedence) {
		this.advance();
		ParseFunction prefixRule = this.previous.type.getParseRule().prefix;
		if (prefixRule == null) {
			throw this.error("Expect expression.");
		}
		boolean canAssign = precedence <= Precedence.ASSIGNMENT;
		prefixRule.apply(this, canAssign);
		while (precedence <= this.current.type.getParseRule().precedence) {
			this.advance();
			ParseFunction infixRule = this.previous.type.getParseRule().infix;
			infixRule.apply(this, canAssign);
		}

		if (canAssign && this.checkIfMatches(EQUAL)) {
			this.error("Invalid assignment target.");
		}
	}

	private byte parseVariable(String message) {
		this.consumeIfMatches(IDENTIFIER, message);
		byte variable = (byte) this.globalVariableLookup.size();
		this.globalVariableLookup.put(this.previous.lexeme, variable);
		return variable;
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

	private boolean checkIfMatches(Token.TokenType type) {
		if (this.current.type != type) return false;
		this.advance();
		return true;
	}


	private CompileError error(String message) {
		return this.errorAt(this.previous, message);
	}

	private CompileError errorAtCurrent(String message) {
		return this.errorAt(this.current, message);
	}

	private CompileError errorAt(Token token, String message) {
		String finalMessage = "Error";
		if (token.type == EOF) {
			finalMessage += " at end";
		} else if (token.type == Token.TokenType.ERROR) {

		} else {
			finalMessage += " at " + token.lexeme;
		}
		finalMessage += ": '" + message + "'";
		this.roboScriptInstance.reportCompileError(token.line, message);
		return new CompileError();
	}

	private void synchronize() {
		while (this.current.type != EOF) {
			if (this.previous.type == SEMICOLON) return;
			switch (this.current.type) {
				case CLASS, FUNCTION, VAR, FOR, IF, WHILE, RETURN -> {
					return;
				}
			}
			this.advance();
		}
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
