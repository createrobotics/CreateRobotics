package com.workert.robotics.base.roboscript;
import static com.workert.robotics.base.roboscript.LegacyCompiler.Precedence.*;

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
		LEFT_PAREN(new LegacyCompiler.ParseRule(LegacyCompiler::grouping, LegacyCompiler::call, CALL)),
		RIGHT_PAREN(empty()),
		LEFT_BRACE(new LegacyCompiler.ParseRule(LegacyCompiler::map, null, NONE)),
		RIGHT_BRACE(empty()),
		LEFT_BRACKET(new LegacyCompiler.ParseRule(LegacyCompiler::list, LegacyCompiler::index, CALL)),
		RIGHT_BRACKET(empty()),
		COMMA(empty()),
		DOT(new LegacyCompiler.ParseRule(null, LegacyCompiler::dot, CALL)),
		PLUS(new LegacyCompiler.ParseRule(null, LegacyCompiler::binary, TERM)),
		MINUS(new LegacyCompiler.ParseRule(LegacyCompiler::unary, LegacyCompiler::binary, TERM)),
		STAR(new LegacyCompiler.ParseRule(null, LegacyCompiler::binary, FACTOR)),
		SLASH(new LegacyCompiler.ParseRule(null, LegacyCompiler::binary, FACTOR)),

		PERCENT(new LegacyCompiler.ParseRule(null, LegacyCompiler::binary, FACTOR)),
		CARET(new LegacyCompiler.ParseRule(null, LegacyCompiler::binary, POWER)),

		COLON(empty()),
		SEMICOLON(empty()),
		QUESTION(empty()),

		// One or two character tokens.
		BANG(new LegacyCompiler.ParseRule(LegacyCompiler::unary, null, NONE)),
		BANG_EQUAL(new LegacyCompiler.ParseRule(null, LegacyCompiler::binary, EQUALITY)),
		EQUAL(empty()),
		EQUAL_EQUAL(new LegacyCompiler.ParseRule(null, LegacyCompiler::binary, EQUALITY)),
		PLUS_EQUAL(empty()),
		PLUS_PLUS(empty()),
		MINUS_EQUAL(empty()),
		MINUS_MINUS(empty()),
		STAR_EQUAL(empty()),
		SLASH_EQUAL(empty()),
		CARET_EQUAL(empty()),
		PERCENT_EQUAL(empty()),
		GREATER(new LegacyCompiler.ParseRule(null, LegacyCompiler::binary, COMPARISON)),
		GREATER_EQUAL(new LegacyCompiler.ParseRule(null, LegacyCompiler::binary, COMPARISON)),
		LESS(new LegacyCompiler.ParseRule(null, LegacyCompiler::binary, COMPARISON)),
		LESS_EQUAL(new LegacyCompiler.ParseRule(null, LegacyCompiler::binary, COMPARISON)),

		// Literals.
		IDENTIFIER(new LegacyCompiler.ParseRule(LegacyCompiler::variable, null, NONE)),
		STRING_VALUE(new LegacyCompiler.ParseRule(LegacyCompiler::literal, null, NONE)),
		DOUBLE_VALUE(new LegacyCompiler.ParseRule(LegacyCompiler::number, null, NONE)),

		// Keywords.
		CLASS(empty()),
		FUNCTION(new LegacyCompiler.ParseRule(LegacyCompiler::lambda, null, NONE)),

		FOR(empty()),
		FOREACH(empty()),

		IF(new LegacyCompiler.ParseRule(LegacyCompiler::ternary, null, TERNARY)),
		ELSE(empty()),
		ELIF(empty()),
		WHILE(empty()),
		LOOP(empty()),

		TRUE(new LegacyCompiler.ParseRule(LegacyCompiler::literal, null, NONE)),
		FALSE(new LegacyCompiler.ParseRule(LegacyCompiler::literal, null, NONE)),

		AND(new LegacyCompiler.ParseRule(null, LegacyCompiler::and, LegacyCompiler.Precedence.AND)),
		OR(new LegacyCompiler.ParseRule(null, LegacyCompiler::or, LegacyCompiler.Precedence.OR)),

		NULL(new LegacyCompiler.ParseRule(LegacyCompiler::literal, null, NONE)),

		RETURN(empty()),
		BREAK(empty()),
		VAR(empty()),

		INSTANCEOF(empty()),

		ANY(empty()),
		STRING(empty()),
		NUMBER(empty()),
		BOOL(empty()),
		RANGE(empty()),
		LIST(empty()),


		EOF(empty()),
		ERROR(empty()),
		NA(empty());

		private final LegacyCompiler.ParseRule parseRule;

		TokenType(LegacyCompiler.ParseRule rule) {
			this.parseRule = rule;
		}

		public LegacyCompiler.ParseRule getParseRule() {
			return this.parseRule;
		}
	}

	@Override
	public String toString() {
		return "Token: " + this.type + ' ' + '"' + this.lexeme + '"';
	}


	private static LegacyCompiler.ParseRule empty() {
		return new LegacyCompiler.ParseRule(null, null, NONE);
	}

}