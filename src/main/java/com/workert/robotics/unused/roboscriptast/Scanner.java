package com.workert.robotics.unused.roboscriptast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

		keywords.put("class", Token.TokenType.CLASS);
		keywords.put("function", Token.TokenType.FUNCTION);

		keywords.put("for", Token.TokenType.FOR);
		keywords.put("foreach", Token.TokenType.FOREACH);

		keywords.put("if", Token.TokenType.IF);
		keywords.put("else", Token.TokenType.ELSE);
		keywords.put("while", Token.TokenType.WHILE);

		keywords.put("true", Token.TokenType.TRUE);
		keywords.put("false", Token.TokenType.FALSE);

		keywords.put("and", Token.TokenType.AND);
		keywords.put("or", Token.TokenType.OR);

		keywords.put("null", Token.TokenType.NULL);

		keywords.put("extends", Token.TokenType.EXTENDS);
		keywords.put("super", Token.TokenType.SUPER);
		keywords.put("this", Token.TokenType.THIS);
		keywords.put("return", Token.TokenType.RETURN);
		keywords.put("break", Token.TokenType.BREAK);
		keywords.put("var", Token.TokenType.VAR);
		keywords.put("persistent", Token.TokenType.PERSISTENT);
		keywords.put("instanceof", Token.TokenType.INSTANCEOF);

		keywords.put("String", Token.TokenType.STRING);
		keywords.put("double", Token.TokenType.DOUBLE);
		keywords.put("boolean", Token.TokenType.BOOLEAN);
		keywords.put("Array", Token.TokenType.ARRAY);
		keywords.put("Object", Token.TokenType.OBJECT);

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
		this.tokens.add(new Token(Token.TokenType.EOF, "", null, this.line));
		return this.tokens;
	}

	/**
	 * Scans the next token from the source code.
	 */
	private void scanToken() {
		char c = this.consumeNextChar();
		switch (c) {
			case '(':
				this.addToken(Token.TokenType.LEFT_PAREN);
				break;
			case ')':
				this.addToken(Token.TokenType.RIGHT_PAREN);
				break;
			case '{':
				this.addToken(Token.TokenType.LEFT_BRACE);
				break;
			case '}':
				this.addToken(Token.TokenType.RIGHT_BRACE);
				break;
			case '[':
				this.addToken(Token.TokenType.LEFT_BRACKET);
				break;
			case ']':
				this.addToken(Token.TokenType.RIGHT_BRACKET);
				break;
			case ',':
				this.addToken(Token.TokenType.COMMA);
				break;
			case '.':
				this.addToken(Token.TokenType.DOT);
				break;
			case '+':
				if (this.consumeIfNextCharMatches('+')) {
					this.addToken(Token.TokenType.PLUS_PLUS);
					break;
				}
				this.addToken(this.consumeIfNextCharMatches('=') ? Token.TokenType.PLUS_EQUAL : Token.TokenType.PLUS);
				break;
			case '-':
				if (this.consumeIfNextCharMatches('-')) {
					this.addToken(Token.TokenType.MINUS_MINUS);
					break;
				}
				this.addToken(this.consumeIfNextCharMatches('=') ? Token.TokenType.MINUS_EQUAL : Token.TokenType.MINUS);
				break;
			case '*':
				this.addToken(this.consumeIfNextCharMatches('=') ? Token.TokenType.STAR_EQUAL : Token.TokenType.STAR);
				break;
			case '/':
				if (this.consumeIfNextCharMatches('/')) {
					while (this.getCurrentChar() != '\n' && !this.isAtEnd()) {
						this.consumeNextChar();
					}
				} else {
					this.addToken(
							this.consumeIfNextCharMatches('=') ? Token.TokenType.SLASH_EQUAL : Token.TokenType.SLASH);
				}
				break;
			case '%':
				this.addToken(Token.TokenType.PERCENT);
				break;
			case '^':
				this.addToken(this.consumeIfNextCharMatches('=') ? Token.TokenType.CARET_EQUAL : Token.TokenType.CARET);
				break;
			case ':':
				this.addToken(Token.TokenType.COLON);
				break;
			case ';':
				this.addToken(Token.TokenType.SEMICOLON);
				break;
			case '!':
				this.addToken(this.consumeIfNextCharMatches('=') ? Token.TokenType.BANG_EQUAL : Token.TokenType.BANG);
				break;
			case '=':
				this.addToken(this.consumeIfNextCharMatches('=') ? Token.TokenType.EQUAL_EQUAL : Token.TokenType.EQUAL);
				break;
			case '<':
				this.addToken(this.consumeIfNextCharMatches('=') ? Token.TokenType.LESS_EQUAL : Token.TokenType.LESS);
				break;
			case '>':
				this.addToken(
						this.consumeIfNextCharMatches('=') ? Token.TokenType.GREATER_EQUAL : Token.TokenType.GREATER);
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
					this.roboScriptInstance.reportCompileError(this.line, "Unexpected character.");
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
			this.roboScriptInstance.reportCompileError(this.line, "Unclosed string.");
			return;
		}

		this.consumeNextChar();

		String value = this.source.substring(this.start + 1, this.current - 1);
		this.addToken(Token.TokenType.STRING_VALUE, value);
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
		this.addToken(Token.TokenType.DOUBLE_VALUE, Double.parseDouble(value));
	}

	/**
	 * Scans an identifier or keyword from the source code.
	 */
	private void identifier() {
		while (this.isAlphaNumeric(this.getCurrentChar())) {
			this.consumeNextChar();
		}

		String text = this.source.substring(this.start, this.current);
		Token.TokenType type = keywords.getOrDefault(text, Token.TokenType.IDENTIFIER);
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
