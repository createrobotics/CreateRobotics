package com.workert.robotics.base.roboscriptbytecode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.workert.robotics.base.roboscriptbytecode.Token.TokenType.*;

/**
 * The Scanner class is responsible for tokenizing the source code of a RoboScript program.
 * It scans the source code character by character and converts it into a list of tokens.
 */
final class Scanner {
	private final RoboScript roboScriptInstance;
	private final String source;
	private final List<Token> tokens = new ArrayList<>();

	private int start = 0;
	private int current = 0;
	private int line = 1;

	private static final Map<String, Token.TokenType> keywords = initializeKeywords();

	/**
	 * Initializes the keyword map.
	 *
	 * @return The map of keywords and their corresponding token types.
	 */
	private static Map<String, Token.TokenType> initializeKeywords() {
		Map<String, Token.TokenType> keywords = new HashMap<>();

		keywords.put("class", CLASS);
		keywords.put("function", FUNCTION);

		keywords.put("for", FOR);
		keywords.put("foreach", FOREACH);

		keywords.put("if", IF);
		keywords.put("else", ELSE);
		keywords.put("while", WHILE);

		keywords.put("true", TRUE);
		keywords.put("false", FALSE);

		keywords.put("and", AND);
		keywords.put("or", OR);

		keywords.put("null", NULL);

		keywords.put("extends", EXTENDS);
		keywords.put("super", SUPER);
		keywords.put("this", THIS);
		keywords.put("return", RETURN);
		keywords.put("break", BREAK);
		keywords.put("var", VAR);
		keywords.put("persistent", PERSISTENT);
		keywords.put("instanceof", INSTANCEOF);

		keywords.put("String", STRING);
		keywords.put("double", DOUBLE);
		keywords.put("boolean", BOOLEAN);
		keywords.put("Array", ARRAY);
		keywords.put("Object", OBJECT);

		return keywords;
	}

	/**
	 * Constructs a new Scanner object.
	 *
	 * @param roboScriptInstance The instance of the RoboScript interpreter.
	 * @param source             The source code of the RoboScript program.
	 */
	Scanner(RoboScript roboScriptInstance, String source) {
		this.roboScriptInstance = roboScriptInstance;
		this.source = source;
	}

	/**
	 * Scans the source code and returns the list of tokens.
	 *
	 * @return The list of tokens.
	 */
	List<Token> scanTokens() {
		while (!this.isAtEnd()) {
			this.start = this.current;
			this.scanToken();
		}
		this.tokens.add(new Token(EOF, "", null, this.line));
		return this.tokens;
	}

	/**
	 * Scans the next token from the source code.
	 */
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
			case '[':
				this.addToken(LEFT_BRACKET);
				break;
			case ']':
				this.addToken(RIGHT_BRACKET);
				break;
			case ',':
				this.addToken(COMMA);
				break;
			case '.':
				this.addToken(DOT);
				break;
			case '+':
				if (this.consumeIfNextCharMatches('+')) {
					this.addToken(PLUS_PLUS);
					break;
				}
				this.addToken(this.consumeIfNextCharMatches('=') ? PLUS_EQUAL : PLUS);
				break;
			case '-':
				if (this.consumeIfNextCharMatches('-')) {
					this.addToken(MINUS_MINUS);
					break;
				}
				this.addToken(this.consumeIfNextCharMatches('=') ? MINUS_EQUAL : MINUS);
				break;
			case '*':
				this.addToken(this.consumeIfNextCharMatches('=') ? STAR_EQUAL : STAR);
				break;
			case '/':
				if (this.consumeIfNextCharMatches('/')) {
					while (this.getCurrentChar() != '\n' && !this.isAtEnd()) {
						this.consumeNextChar();
					}
				} else {
					this.addToken(
							this.consumeIfNextCharMatches('=') ? SLASH_EQUAL : SLASH);
				}
				break;
			case '%':
				this.addToken(PERCENT);
				break;
			case '^':
				this.addToken(this.consumeIfNextCharMatches('=') ? CARET_EQUAL : CARET);
				break;
			case ':':
				this.addToken(COLON);
				break;
			case ';':
				this.addToken(SEMICOLON);
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
				this.addToken(
						this.consumeIfNextCharMatches('=') ? GREATER_EQUAL : GREATER);
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
					this.roboScriptInstance.reportScanError(this.line, "Unexpected character.");
				}
				break;
		}
	}

	/**
	 * Scans a string literal from the source code.
	 */
	private void string() {
		while (this.getCurrentChar() != '"' && !this.isAtEnd()) {
			if (this.getCurrentChar() == '\n') {
				this.line++;
			}
			this.consumeNextChar();
		}

		if (this.isAtEnd()) {
			this.roboScriptInstance.reportScanError(this.line, "Unclosed string.");
			return;
		}

		this.consumeNextChar();

		String value = this.source.substring(this.start + 1, this.current - 1);
		this.addToken(STRING_VALUE, value);
	}

	/**
	 * Scans a number literal from the source code.
	 */
	private void number() {
		while (this.isDigit(this.getCurrentChar())) {
			this.consumeNextChar();
		}

		if (this.getCurrentChar() == '.' && this.isDigit(this.getNextChar())) {
			this.consumeNextChar();
			while (this.isDigit(this.getCurrentChar())) {
				this.consumeNextChar();
			}
		}

		String value = this.source.substring(this.start, this.current);
		this.addToken(DOUBLE_VALUE, Double.parseDouble(value));
	}

	/**
	 * Scans an identifier or keyword from the source code.
	 */
	private void identifier() {
		while (this.isAlphaNumeric(this.getCurrentChar())) {
			this.consumeNextChar();
		}

		String text = this.source.substring(this.start, this.current);
		Token.TokenType type = keywords.getOrDefault(text, IDENTIFIER);
		this.addToken(type);
	}

	/**
	 * Adds a token with the given type to the token list.
	 *
	 * @param type The type of the token.
	 */
	private void addToken(Token.TokenType type) {
		this.addToken(type, null);
	}

	/**
	 * Adds a token with the given type and literal value to the token list.
	 *
	 * @param type    The type of the token.
	 * @param literal The literal value of the token.
	 */
	private void addToken(Token.TokenType type, Object literal) {
		String lexeme = this.source.substring(this.start, this.current);
		this.tokens.add(new Token(type, lexeme, literal, this.line));
	}

	/**
	 * Consumes and returns the next character in the source code.
	 *
	 * @return The next character.
	 */
	private char consumeNextChar() {
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
