package com.workert.robotics.base.roboscriptast;
public final class Token {
	final TokenType type;
	final String lexeme;
	final Object literal;
	final int line;

	Token(TokenType type, String lexeme, Object literal, int line) {
		this.type = type;
		this.lexeme = lexeme;
		this.literal = literal;
		this.line = line;
	}

	@Override
	public String toString() {
		return this.type + " " + this.lexeme + " " + this.literal;
	}

	public enum TokenType {
		// Single-character tokens.
		LEFT_PAREN,
		RIGHT_PAREN,
		LEFT_BRACE,
		RIGHT_BRACE,
		LEFT_BRACKET,
		RIGHT_BRACKET,
		COMMA,
		DOT,
		PLUS,
		MINUS,
		STAR,
		SLASH,
		PERCENT,
		CARET,
		COLON,
		SEMICOLON,

		// One or two character tokens.
		BANG,
		BANG_EQUAL,
		EQUAL,
		EQUAL_EQUAL,
		PLUS_EQUAL,
		PLUS_PLUS,
		MINUS_EQUAL,
		MINUS_MINUS,
		STAR_EQUAL,
		SLASH_EQUAL,
		CARET_EQUAL,
		GREATER,
		GREATER_EQUAL,
		LESS,
		LESS_EQUAL,

		// Literals.
		IDENTIFIER,
		STRING_VALUE,
		DOUBLE_VALUE,

		// Keywords.
		CLASS,
		FUNCTION,

		FOR,
		FOREACH,

		IF,
		ELSE,
		WHILE,

		TRUE,
		FALSE,

		AND,
		OR,

		NULL,


		EXTENDS,
		SUPER,
		THIS,
		RETURN,
		BREAK,
		VAR,

		PERSISTENT,
		INSTANCEOF,

		STRING,
		DOUBLE,
		BOOLEAN,
		ARRAY,
		OBJECT,

		// End of File
		EOF
	}
}