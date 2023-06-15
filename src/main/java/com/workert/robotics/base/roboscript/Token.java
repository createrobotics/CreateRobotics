package com.workert.robotics.base.roboscript;
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
		MINUS,
		PLUS,
		CARET,
		SEMICOLON,
		SLASH,
		STAR,
		COLON,

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
		AND,
		CLASS,
		ELSE,
		FALSE,
		FUNC,
		FOR,
		FOREACH,
		IF,
		NULL,
		OR,
		RETURN,
		EXTENDS,
		BREAK,
		SUPER,
		THIS,
		TRUE,
		VAR,
		WHILE,
		PERSISTENT,
		INSTANCEOF,

		// Types
		STRING,
		DOUBLE,
		BOOLEAN,
		ARRAY,
		FUNCTION,


		EOF
	}
}