package com.workert.robotics.base.roboscript;
public class Token {
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
		MINUS_EQUAL,
		STAR_EQUAL,
		SLASH_EQUAL,
		CARET_EQUAL,
		GREATER,
		GREATER_EQUAL,
		LESS,
		LESS_EQUAL,

		// Literals.
		IDENTIFIER,
		STRING,
		NUMBER,

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

		EOF
	}
}