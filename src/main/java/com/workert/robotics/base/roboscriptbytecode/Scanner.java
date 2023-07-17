package com.workert.robotics.base.roboscriptbytecode;
import java.util.ArrayList;
import java.util.List;

import static com.workert.robotics.base.roboscriptbytecode.Token.TokenType.*;

/**
 * The Scanner class is responsible for tokenizing the source code of a RoboScript program.
 * It scans the source code character by character and converts it into a list of tokens.
 */
final class Scanner {
	// private final com.workert.robotics.base.roboscriptast.RoboScript roboScriptInstance;
	private final String source;

	private int start = 0;
	private int current = 0;
	private int line = 1;

	/**
	 * Constructs a new Scanner object.
	 *
	 * @param source The source code of the RoboScript program.
	 */
	Scanner(/*RoboScript roboScriptInstance,*/ String source) {
		// this.roboScriptInstance = roboScriptInstance;
		this.source = source;
	}

	/**
	 * Scans the source code and returns the list of tokens.
	 *
	 * @return The list of tokens.
	 */
	List<Token> scanTokens() {
		List<Token> tokens = new ArrayList<>();
		while (true) {
			this.start = this.current;
			Token t = this.scanToken();
			tokens.add(t);
			if (t.type == EOF) break;
		}
		return tokens;
	}

	/**
	 * Scans the next token from the source code.
	 */
	Token scanToken() {
		this.skipWhiteSpace();
		this.start = this.current;
		if (this.isAtEnd()) return new Token(EOF, "", this.line);
		char c = this.consumeCurrentChar();
		switch (c) {
			case '(':
				return this.addToken(LEFT_PAREN);
			case ')':
				return this.addToken(RIGHT_PAREN);
			case '{':
				return this.addToken(LEFT_BRACE);
			case '}':
				return this.addToken(RIGHT_BRACE);
			case '[':
				return this.addToken(LEFT_BRACKET);
			case ']':
				return this.addToken(RIGHT_BRACKET);
			case ',':
				return this.addToken(COMMA);
			case '.':
				return this.addToken(DOT);
			case '+':
				if (this.consumeIfNextCharMatches('+')) {
					return this.addToken(PLUS_PLUS);
				}
				return this.addToken(this.consumeIfNextCharMatches('=') ? PLUS_EQUAL : PLUS);
			case '-':
				if (this.consumeIfNextCharMatches('-')) {
					return this.addToken(MINUS_MINUS);
				}
				return this.addToken(this.consumeIfNextCharMatches('=') ? MINUS_EQUAL : MINUS);
			case '*':
				return this.addToken(this.consumeIfNextCharMatches('=') ? STAR_EQUAL : STAR);
			case '/':
				if (this.consumeIfNextCharMatches('/')) {
					while (this.getCurrentChar() != '\n' && !this.isAtEnd()) {
						this.consumeCurrentChar();
					}
				} else {
					return this.addToken(
							this.consumeIfNextCharMatches('=') ? SLASH_EQUAL : SLASH);
				}
			case '%':
				return this.addToken(PERCENT);
			case '^':
				return this.addToken(this.consumeIfNextCharMatches('=') ? CARET_EQUAL : CARET);
			case ':':
				return this.addToken(COLON);
			case ';':
				return this.addToken(SEMICOLON);
			case '!':
				return this.addToken(this.consumeIfNextCharMatches('=') ? BANG_EQUAL : BANG);
			case '=':
				return this.addToken(this.consumeIfNextCharMatches('=') ? EQUAL_EQUAL : EQUAL);
			case '<':
				return this.addToken(this.consumeIfNextCharMatches('=') ? LESS_EQUAL : LESS);
			case '>':
				return this.addToken(
						this.consumeIfNextCharMatches('=') ? GREATER_EQUAL : GREATER);
			case '"':
				return this.string();
			default:
				if (this.isDigit(c)) {
					return this.number();
				} else if (this.isAlpha(c)) {
					return this.identifier();
				} else {
					// this.roboScriptInstance.reportCompileError(this.line, "Unexpected character.");
					return this.errorToken("Unexpected character.");
				}
		}
	}

	private void skipWhiteSpace() {
		while (true) {
			if (this.isAtEnd()) return;
			char c = this.getCurrentChar();
			switch (c) {
				case ' ', '\r', '\t' -> this.consumeCurrentChar();
				case '\n' -> {
					this.consumeCurrentChar();
					this.line++;
				}
				default -> {
					return;
				}
			}
		}
	}

	/**
	 * Scans a string literal from the source code.
	 */
	private Token string() {
		this.start = this.current;
		while (this.getCurrentChar() != '"' && !this.isAtEnd()) {
			if (this.getCurrentChar() == '\n') {
				this.line++;
			}
			this.consumeCurrentChar();
		}

		if (this.isAtEnd()) {
			// this.roboScriptInstance.reportCompileError(this.line, "Unclosed string.");

			return this.errorToken("Unterminated string.");
		}
		Token finalToken = this.addToken(STRING_VALUE);
		this.consumeCurrentChar();
		return finalToken;
	}

	/**
	 * Scans a number literal from the source code.
	 */
	private Token number() {
		while (this.isDigit(this.getCurrentChar())) {
			this.consumeCurrentChar();
		}

		if (this.getCurrentChar() == '.' && this.isDigit(this.getNextChar())) {
			this.consumeCurrentChar();
			while (this.isDigit(this.getCurrentChar())) {
				this.consumeCurrentChar();
			}
		}

		String value = this.source.substring(this.start, this.current);
		return this.addToken(DOUBLE_VALUE);
	}

	/**
	 * Scans an identifier or keyword from the source code.
	 */
	private Token identifier() {
		while (this.isAlphaNumeric(this.getCurrentChar())) {
			this.consumeCurrentChar();
		}
		return this.addToken(this.identifierToken());
	}

	// TODO find a cleaner and faster method to detect the Identifier Token
	private Token.TokenType identifierToken() {
		switch (this.source.charAt(this.start)) {
			// one path keywords ; initial characters that only share on keyword

			case 'a' -> {
				return this.checkKeyword(1, 2, "nd", AND);
			}
			case 'c' -> {
				return this.checkKeyword(1, 4, "lass", CLASS);
			}
			case 'e' -> {
				return this.checkKeyword(1, 3, "lse", ELSE);
			}
			case 'i' -> {
				return this.checkKeyword(1, 1, "f", IF);
			}
			case 'n' -> {
				return this.checkKeyword(1, 3, "ull", NULL);
			}
			case 'o' -> {
				return this.checkKeyword(1, 1, "r", OR);
			}
			case 'r' -> {
				return this.checkKeyword(1, 5, "eturn", RETURN);
			}
			case 's' -> {
				return this.checkKeyword(1, 4, "uper", SUPER);
			}
			case 'v' -> {
				return this.checkKeyword(1, 2, "ar", VAR);
			}
			case 'w' -> {
				return this.checkKeyword(1, 4, "hile", WHILE);
			}
			case 'f' -> {
				if (this.current - this.start > 1)
					switch (this.source.charAt(this.start + 1)) {
						case 'a' -> {
							return this.checkKeyword(2, 3, "lse", FALSE);
						}
						case 'o' -> {
							return this.checkKeyword(2, 1, "r", FOR);
						}
						case 'u' -> {
							return this.checkKeyword(2, 6, "nction", FUNCTION);
						}
					}
			}
			case 't' -> {
				if (this.current - this.start > 1)
					switch (this.source.charAt(this.start + 1)) {
						case 'h' -> {
							return this.checkKeyword(2, 2, "is", THIS);
						}
						case 'r' -> {
							return this.checkKeyword(2, 2, "ue", TRUE);
						}
					}
			}


			case 'l' -> {
				return this.checkKeyword(1, 2, "og", LOG);
			}
		}

		return IDENTIFIER;
	}

	private Token.TokenType checkKeyword(int start, int length, String rest, Token.TokenType type) {
		if (this.current - this.start == start + length && rest.equals(
				this.source.substring(this.start + start, this.current))) return type;
		return IDENTIFIER;
	}

	/**
	 * Adds a token with the given type and literal value to the token list.
	 *
	 * @param type The type of the token.
	 */
	private Token addToken(Token.TokenType type) {
		String lexeme = this.source.substring(this.start, this.current);
		this.start = this.current;
		return new Token(type, lexeme, this.line);
	}

	private Token errorToken(String message) {
		this.start = this.current;
		return new Token(ERROR, message, this.line);
	}

	/**
	 * Consumes and returns the consumed character.
	 *
	 * @return The consumed char.
	 */
	private char consumeCurrentChar() {
		return this.source.charAt(this.current++);
	}

	/**
	 * Checks if the next character matches the given expected character and consumes it if true.
	 *
	 * @param expected The expected character.
	 * @return True if the next character matches the expected character, false otherwise.
	 */
	private boolean consumeIfNextCharMatches(char expected) {
		if (this.isAtEnd()) {
			return false;
		}

		if (this.source.charAt(this.current) != expected) {
			return false;
		}

		this.current++;
		return true;
	}

	/**
	 * Returns the current character in the source code.
	 *
	 * @return The current character.
	 */
	private char getCurrentChar() {
		if (this.isAtEnd()) {
			return '\0';
		}
		return this.source.charAt(this.current);
	}

	/**
	 * Returns the next character in the source code.
	 *
	 * @return The next character.
	 */
	private char getNextChar() {
		if (this.current + 1 >= this.source.length()) {
			return '\0';
		}
		return this.source.charAt(this.current + 1);
	}

	/**
	 * Checks if the given character is a digit.
	 *
	 * @param c The character to check.
	 * @return True if the character is a digit, false otherwise.
	 */
	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	/**
	 * Checks if the given character is an alphabetic character or an underscore.
	 *
	 * @param c The character to check.
	 * @return True if the character is alphabetic or an underscore, false otherwise.
	 */
	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}

	/**
	 * Checks if the given character is alphanumeric or an underscore.
	 *
	 * @param c The character to check.
	 * @return True if the character is alphanumeric or an underscore, false otherwise.
	 */
	private boolean isAlphaNumeric(char c) {
		return this.isAlpha(c) || this.isDigit(c);
	}

	/**
	 * Checks if the scanner has reached the end of the source code.
	 *
	 * @return True if at the end of the source code, false otherwise.
	 */
	private boolean isAtEnd() {
		return this.current >= this.source.length();
	}
}
