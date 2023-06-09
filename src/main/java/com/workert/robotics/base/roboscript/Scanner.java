package com.workert.robotics.base.roboscript;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.workert.robotics.base.roboscript.Token.TokenType.*;

public class Scanner {
	private final RoboScript roboScriptInstance;
	private final String source;
	private final List<Token> tokens = new ArrayList<>();

	private int start = 0;
	private int current = 0;
	private int line = 1;

	private static final Map<String, Token.TokenType> keywords;

	static {
		keywords = new HashMap<>();
		keywords.put("and", AND);
		keywords.put("class", CLASS);
		keywords.put("else", ELSE);
		keywords.put("false", FALSE);
		keywords.put("for", FOR);
		keywords.put("func", FUNC);
		keywords.put("if", IF);
		keywords.put("null", NULL);
		keywords.put("or", OR);
		keywords.put("return", RETURN);
		keywords.put("break", BREAK);
		keywords.put("super", SUPER);
		keywords.put("extends", EXTENDS);
		keywords.put("this", THIS);
		keywords.put("true", TRUE);
		keywords.put("var", VAR);
		keywords.put("while", WHILE);
		keywords.put("static", STATIC);
	}

	Scanner(RoboScript roboScriptInstance, String source) {
		this.roboScriptInstance = roboScriptInstance;
		this.source = source;
	}

	List<Token> scanTokens() {
		while (!this.isAtEnd()) {
			// Beginning of the next lexeme.
			this.start = this.current;
			this.scanToken();
		}

		this.tokens.add(new Token(EOF, "", null, this.line));
		return this.tokens;
	}

	private void scanToken() {
		char c = this.consumeNextChar();
		switch (c) {
			case '(':
				this.addToken(LEFT_PAREN);
				break;
			case ')':
				this.addToken(RIGHT_PAREN);
				break;
			case '{':
				this.addToken(LEFT_BRACE);
				break;
			case '}':
				this.addToken(RIGHT_BRACE);
				break;
			case ',':
				this.addToken(COMMA);
				break;
			case '.':
				this.addToken(DOT);
				break;
			case '-':
				if (this.consumeIfNextCharMatches('-')) {
					this.addToken(MINUS_MINUS);
					break;
				}
				this.addToken(this.consumeIfNextCharMatches('=') ? MINUS_EQUAL : MINUS);
				break;
			case '+':
				if (this.consumeIfNextCharMatches('+')) {
					this.addToken(PLUS_PLUS);
					break;
				}
				this.addToken(this.consumeIfNextCharMatches('=') ? PLUS_EQUAL : PLUS);
				break;
			case ';':
				this.addToken(SEMICOLON);
				break;
			case '*':
				this.addToken(this.consumeIfNextCharMatches('=') ? STAR_EQUAL : STAR);
				break;
			case '^':
				this.addToken(this.consumeIfNextCharMatches('=') ? CARET_EQUAL : CARET);
				break;
			case '!':
				this.addToken(this.consumeIfNextCharMatches('=') ? BANG_EQUAL : BANG);
				break;
			case '=':
				this.addToken(this.consumeIfNextCharMatches('=') ? EQUAL_EQUAL : EQUAL);
				break;
			case '<':
				this.addToken(this.consumeIfNextCharMatches('=') ? LESS_EQUAL : LESS);
				break;
			case '>':
				this.addToken(this.consumeIfNextCharMatches('=') ? GREATER_EQUAL : GREATER);
				break;
			case '/':
				if (this.consumeIfNextCharMatches('/')) {
					while (this.getCurrentChar() != '\n' && !this.isAtEnd()) {
						this.consumeNextChar();
					}
				} else {
					this.addToken(this.consumeIfNextCharMatches('=') ? SLASH_EQUAL : SLASH);
				}
				break;

			case ' ':
			case '\r':
			case '\t':
				break;

			case '\n':
				this.line++;
				break;

			case '"':
				this.string();
				break;

			default:
				if (this.isDigit(c)) {
					this.number();
				} else if (this.isAlpha(c)) {
					this.identifier();
				} else {
					this.roboScriptInstance.error(this.line, "Unexpected character.");
				}
				break;
		}
	}

	private void identifier() {
		while (this.isAlphaNumeric(this.getCurrentChar())) {
			this.consumeNextChar();
		}

		String text = this.source.substring(this.start, this.current);
		Token.TokenType type = keywords.get(text);
		if (type == null) type = IDENTIFIER;
		this.addToken(type);
	}

	private void number() {
		while (this.isDigit(this.getCurrentChar())) {
			this.consumeNextChar();
		}

		// Fractional part.
		if (this.getCurrentChar() == '.' && this.isDigit(this.getNextChar())) {
			// Consume the "."
			this.consumeNextChar();

			while (this.isDigit(this.getCurrentChar())) {
				this.consumeNextChar();
			}
		}

		this.addToken(NUMBER, Double.parseDouble(this.source.substring(this.start, this.current)));
	}

	private void string() {
		while (this.getCurrentChar() != '"' && !this.isAtEnd()) {
			if (this.getCurrentChar() == '\n') this.line++;
			this.consumeNextChar();
		}

		if (this.isAtEnd()) {
			this.roboScriptInstance.error(this.line, "Unclosed string.");
			return;
		}

		// The closing ".
		this.consumeNextChar();

		// Trim the surrounding quotes.
		String value = this.source.substring(this.start + 1, this.current - 1);
		this.addToken(STRING, value);
	}

	/**
	 * Checks if next character matches <code>expected</code> and consumes it if so.
	 *
	 * @param expected The expected next character.
	 * @return If the next character matches the expected one.
	 **/
	private boolean consumeIfNextCharMatches(char expected) {
		if (this.isAtEnd()) return false;
		if (this.source.charAt(this.current) != expected) return false;

		this.current++;
		return true;
	}

	private boolean isAtEnd() {
		return this.current >= this.source.length();
	}

	private char consumeNextChar() {
		return this.source.charAt(this.current++);
	}

	private char getCurrentChar() {
		if (this.isAtEnd()) return '\0';
		return this.source.charAt(this.current);
	}

	private char getNextChar() { // getNextChar
		if (this.current + 1 >= this.source.length()) return '\0';
		return this.source.charAt(this.current + 1);
	}

	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}

	private boolean isAlphaNumeric(char c) {
		return this.isAlpha(c) || this.isDigit(c);
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private void addToken(Token.TokenType type) {
		this.addToken(type, null);
	}

	private void addToken(Token.TokenType type, Object literal) {
		String text = this.source.substring(this.start, this.current);
		this.tokens.add(new Token(type, text, literal, this.line));
	}
}
