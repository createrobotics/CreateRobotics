package com.workert.robotics.base.roboscriptbytecode;
import java.util.ArrayList;
import java.util.List;

import static com.workert.robotics.base.roboscriptbytecode.OpCode.*;
import static com.workert.robotics.base.roboscriptbytecode.Token.TokenType.*;

public final class Compiler {
	// ⚠️⚠️⚠️⚠️i have no idea what im doing        ⚠️⚠️⚠️⚠️

	final RoboScript roboScriptInstance;
	Chunk chunk = new Chunk();
	Token current;
	Token previous;
	List<Token> tokens = new ArrayList<>();

	Compiler(RoboScript roboScriptInstance) {
		this.roboScriptInstance = roboScriptInstance;
	}

	protected void compile(String source) {
		Scanner scanner = new Scanner(source);
		this.tokens = scanner.scanTokens();
		this.advance();
		this.expression();
		this.consume(EOF, "Expect end of expression.");
	}


	private void advance() {
		this.previous = this.current;

		while (true) {
			this.current = scanToken();
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

	private void expression() {
		this.parsePrecedence(Precedence.PREC_ASSIGNMENT);
	}

	private void number() {
		double value = Double.parseDouble(this.previous.lexeme);

	}


	private void emitByte(byte b) {
		this.chunk.writeCode(b, this.previous.line);
	}

	private void emitReturn() {
		this.emitByte(OP_RETURN);
	}

	private void emitBytes(byte... b) {
		for (byte by : b) {
			this.emitByte(by);
		}
	}

	private void emitConstant(Object value) {
		this.emitBytes(OP_CONSTANT, this.makeConstant(value));
	}

	private byte makeConstant(Object object) {
		int constant = this.chunk.addConstant(object);
		if (constant > 255) {
			this.error("Too many constants in one chunk.");
			return 0;
		}
		return (byte) constant;
	}

	private void grouping() {
		this.expression();
		this.consumeIfMatches(RIGHT_PAREN, "Expect ')' after expression.");
	}

	private void unary() {
		Token.TokenType operator = this.previous.type;
		this.expression();
		this.parsePrecedence(Precedence.PREC_UNARY);
		switch (operator) {
			case MINUS -> this.emitByte(OP_NEGATE);
			default -> {
				return; // unreachable
			}
		}
	}

	private void binary() {
		Token.TokenType operator = this.previous.type;
		ParseRule rule = this.getRule(operator);
		this.parsePrecedence(rule.precedence + 1);
	}

	private void parsePrecedence(byte p) {

	}

	private void endCompiler() {
		this.emitReturn();
	}


	private static class CompileError extends RuntimeException {
	}

	private interface Precedence {
		byte PREC_NONE = 0;
		byte PREC_ASSIGNMENT = 1;  // = += -= *= /=
		byte PREC_OR = 2;          // or
		byte PREC_AND = 3;         // and
		byte PREC_EQUALITY = 4;    // == !=
		byte PREC_COMPARISON = 5;  // < > <= >=
		byte PREC_TERM = 6;        // + -
		byte PREC_FACTOR = 7;      // * /
		byte PREC_UNARY = 8;       // ! -
		byte PREC_CALL = 9;        // . ()
		byte PREC_PRIMARY = 10;
	}

	private class ParseRule {
		ParseFunction prefix;
		ParseFunction infix;
		int precedence;

		ParseRule(ParseFunction prefix, ParseFunction infix, int precedence) {
			this.prefix = prefix;
			this.infix = infix;
			this.precedence = precedence;
		}
	}

	private enum ParseFunction {
		grouping,
		number,
		unary,
		binary,
	}

	List<ParseRule> rules = this.initializeRules();

	private List<ParseRule> initializeRules() {
		List<ParseRule> rules = new ArrayList<>();
		rules.add(new ParseRule(ParseFunction.grouping, null, Precedence.PREC_NONE)); // (
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // )
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // {
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // }
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // ,
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // .
		rules.add(new ParseRule(ParseFunction.unary, ParseFunction.binary, Precedence.PREC_TERM)); // -
		rules.add(new ParseRule(null, ParseFunction.binary, Precedence.PREC_TERM)); // +
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // ;
		rules.add(new ParseRule(null, ParseFunction.binary, Precedence.PREC_FACTOR)); // /
		rules.add(new ParseRule(null, ParseFunction.binary, Precedence.PREC_FACTOR)); // *
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // !
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // !=
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // =
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // ==
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // >
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // >=
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // <
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // <=
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // identifier
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // string
		rules.add(new ParseRule(ParseFunction.number, null, Precedence.PREC_NONE)); // number
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // and
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // class
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // else
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // false
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // for
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // function
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // if
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // null
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // or
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // return
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // super
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // this
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // true
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // var
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // while
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // error
		rules.add(new ParseRule(null, null, Precedence.PREC_NONE)); // EOF
		return rules;
	}

	private ParseRule getRule(Token.TokenType type) {
		return this.rules.get(type.ordinal());
	}


}
