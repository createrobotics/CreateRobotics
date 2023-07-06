package com.workert.robotics.base.roboscriptbytecode;
import java.util.ArrayList;
import java.util.List;

import static com.workert.robotics.base.roboscriptbytecode.Token.TokenType.*;

public class Scanner {
	private int start = 0;
	private int current = 0;
	private int line = 1;

	private String source;

	List<Token> tokens = new ArrayList<>();

	protected void scan(String source) {
		this.source = source;
		while (true) {
			Token token = this.scanToken();
			this.line = token.line;

			if (token.type == EOF) break;
		}


	}


	private Token scanToken() {
		this.skipWhiteSpace();
		this.start = this.current;
		if (this.isAtEnd()) return this.makeToken(EOF);
		char c = this.consume();
		Token.TokenType type = null;
		switch (c) {

			// single character tokens
			case '(' -> type = LEFT_PAREN;
			case ')' -> type = RIGHT_PAREN;
			case '{' -> type = LEFT_BRACKET;
			case '}' -> type = RIGHT_BRACKET;
			case ';' -> type = SEMICOLON;
			case ',' -> type = COMMA;
			case '.' -> type = DOT;

			// two character tokens

			case '-' -> type = this.consumeIfMatch('=') ? MINUS_EQUAL : MINUS;
			case '+' -> type = this.consumeIfMatch('=') ? PLUS_EQUAL : PLUS;
			case '/' -> type = this.consumeIfMatch('=') ? SLASH_EQUAL : SLASH;
			case '*' -> type = this.consumeIfMatch('=') ? STAR_EQUAL : STAR;
			case '!' -> type = this.consumeIfMatch('=') ? BANG_EQUAL : BANG;
			case '=' -> type = this.consumeIfMatch('=') ? EQUAL_EQUAL : EQUAL;
			case '<' -> type = this.consumeIfMatch('=') ? LESS_EQUAL : LESS;
			case '>' -> type = this.consumeIfMatch('=') ? GREATER_EQUAL : GREATER;
		}
		if (type != null) return this.makeToken(type);
		return this.errorToken("Unexpected character");
	}


	private boolean isAtEnd() {
		return this.current >= this.source.length();
	}

	private Token makeToken(Token.TokenType type) {
		return new Token(type, this.source.substring(this.start, this.current), this.line);
	}

	private Token errorToken(String message) {
		return new Token(Token.TokenType.ERROR, message, this.line);
	}

	private char consume() {
		return this.source.charAt(this.current++);
	}

	private char peek() {
		return this.source.charAt(this.current);
	}

	private char peekNext() {
		if (this.isAtEnd()) return '\0';
		return this.source.charAt(this.current + 1);
	}

	private void skipWhiteSpace() {
		while (true) {
			char c = this.peek();
			switch (c) {
				case ' ':
				case '\r':
				case '\t':
					this.consume();
					break;
				case '\n':
					// if we ever want support for 0 semicolons like js, create a custom token type for \n and create it here
					this.line++;
					this.consume();
					break;
				default:
					return;
			}
		}
	}


	private boolean consumeIfMatch(char expected) {
		if (this.isAtEnd()) return false;
		if (this.source.charAt(this.current) != expected) return false;
		this.current++;
		return true;
	}
}
