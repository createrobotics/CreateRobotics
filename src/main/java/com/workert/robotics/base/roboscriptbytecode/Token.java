package com.workert.robotics.base.roboscriptbytecode;
import static com.workert.robotics.base.roboscriptbytecode.Compiler.Precedence.*;

final class Token {
	final TokenType type;
	final String lexeme;
	final int line;

	Token(TokenType type, String lexeme, int line) {
		this.type = type;
		this.lexeme = lexeme;
		this.line = line;
	}

	public enum TokenType {
		// Single-character tokens.
		LEFT_PAREN(new Compiler.ParseRule(Compiler::grouping, Compiler::call, CALL)),
		RIGHT_PAREN(empty()),
		LEFT_BRACE(new Compiler.ParseRule(Compiler::map, null, NONE)),
		RIGHT_BRACE(empty()),
		LEFT_BRACKET(new Compiler.ParseRule(Compiler::list, Compiler::index, CALL)),
		RIGHT_BRACKET(empty()),
		COMMA(empty()),
		DOT(empty()),
		PLUS(new Compiler.ParseRule(null, Compiler::binary, TERM)),
		MINUS(new Compiler.ParseRule(Compiler::unary, Compiler::binary, TERM)),
		STAR(new Compiler.ParseRule(null, Compiler::binary, FACTOR)),
		SLASH(new Compiler.ParseRule(null, Compiler::binary, FACTOR)),

		PERCENT(new Compiler.ParseRule(null, Compiler::binary, FACTOR)),
		CARET(new Compiler.ParseRule(null, Compiler::binary, POWER)),

		COLON(empty()),
		SEMICOLON(empty()),

		// One or two character tokens.
		BANG(new Compiler.ParseRule(Compiler::unary, null, NONE)),
		BANG_EQUAL(new Compiler.ParseRule(null, Compiler::binary, EQUALITY)),
		EQUAL(empty()),
		EQUAL_EQUAL(new Compiler.ParseRule(null, Compiler::binary, EQUALITY)),
		PLUS_EQUAL(empty()),
		PLUS_PLUS(empty()),
		MINUS_EQUAL(empty()),
		MINUS_MINUS(empty()),
		STAR_EQUAL(empty()),
		SLASH_EQUAL(empty()),
		CARET_EQUAL(empty()),
		PERCENT_EQUAL(empty()),
		GREATER(new Compiler.ParseRule(null, Compiler::binary, COMPARISON)),
		GREATER_EQUAL(new Compiler.ParseRule(null, Compiler::binary, COMPARISON)),
		LESS(new Compiler.ParseRule(null, Compiler::binary, COMPARISON)),
		LESS_EQUAL(new Compiler.ParseRule(null, Compiler::binary, COMPARISON)),

		// Literals.
		IDENTIFIER(new Compiler.ParseRule(Compiler::variable, null, NONE)),
		STRING_VALUE(new Compiler.ParseRule(Compiler::literal, null, NONE)),
		DOUBLE_VALUE(new Compiler.ParseRule(Compiler::number, null, NONE)),

		// Keywords.
		CLASS(empty()),
		FUNCTION(empty()),

		FOR(empty()),
		FOREACH(empty()),

		IF(empty()),
		ELSE(empty()),
		WHILE(empty()),

		TRUE(new Compiler.ParseRule(Compiler::literal, null, NONE)),
		FALSE(new Compiler.ParseRule(Compiler::literal, null, NONE)),

		AND(new Compiler.ParseRule(null, Compiler::and, Compiler.Precedence.AND)),
		OR(new Compiler.ParseRule(null, Compiler::or, Compiler.Precedence.OR)),

		NULL(new Compiler.ParseRule(Compiler::literal, null, NONE)),


		EXTENDS(empty()),
		SUPER(empty()),
		THIS(empty()),
		RETURN(empty()),
		BREAK(empty()),
		VAR(empty()),

		PERSISTENT(empty()),
		INSTANCEOF(empty()),

		STRING(empty()),
		DOUBLE(empty()),
		BOOLEAN(empty()),
		ARRAY(empty()),
		OBJECT(empty()),


		EOF(empty()),
		ERROR(empty());

		private final Compiler.ParseRule parseRule;

		TokenType(Compiler.ParseRule rule) {
			this.parseRule = rule;
		}

		public Compiler.ParseRule getParseRule() {
			return this.parseRule;
		}
	}

	@Override
	public String toString() {
		return "Token: " + this.type + ' ' + '"' + this.lexeme + '"';
	}


	private static Compiler.ParseRule empty() {
		return new Compiler.ParseRule(null, null, NONE);
	}

}