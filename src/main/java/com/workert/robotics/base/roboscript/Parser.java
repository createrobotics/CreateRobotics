package com.workert.robotics.base.roboscript;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
	private final RoboScript roboScriptInstance;
	private final List<Token> tokens;
	private int current = 0;

	Parser(RoboScript roboScriptInstance, List<Token> tokens) {
		this.roboScriptInstance = roboScriptInstance;
		this.tokens = tokens;
	}

	public List<Statement> parse() {
		List<Statement> statements = new ArrayList<>();
		while (!this.isAtEnd()) {
			statements.add(this.declaration());
		}
		return statements;
	}

	private Expression expression() {
		return this.assignment();
	}

	private Statement declaration() {
		try {
			if (this.advanceIfNextTokenMatches(Token.TokenType.CLASS)) return this.classDeclaration();
			if (this.advanceIfNextTokenMatches(Token.TokenType.FUNC)) return this.function("function");
			if (this.advanceIfNextTokenMatches(Token.TokenType.STATIC)) {
				this.consumeIfNextTokenMatches(Token.TokenType.VAR, "Expected keyword 'var' after keyword 'static'.");
				return this.varDeclaration(true);
			}
			if (this.advanceIfNextTokenMatches(Token.TokenType.VAR)) return this.varDeclaration(false);

			return this.statement();
		} catch (ParseError error) {
			this.synchronize();
			return null;
		}
	}

	private Statement classDeclaration() {
		Token name = this.consumeIfNextTokenMatches(Token.TokenType.IDENTIFIER, "Expected class name.");

		Expression.Variable superclass = null;
		if (this.advanceIfNextTokenMatches(Token.TokenType.EXTENDS)) {
			this.consumeIfNextTokenMatches(Token.TokenType.IDENTIFIER, "Expected superclass name.");
			superclass = new Expression.Variable(this.getPreviousToken());
		}

		this.consumeIfNextTokenMatches(Token.TokenType.LEFT_BRACE, "Expected '{' before class body.");

		List<Statement.Function> methods = new ArrayList<>();
		while (!this.isNextToken(Token.TokenType.RIGHT_BRACE) && !this.isAtEnd()) {
			this.consumeIfNextTokenMatches(Token.TokenType.FUNC, "Expected 'func' token.");
			methods.add(this.function("method"));
		}

		this.consumeIfNextTokenMatches(Token.TokenType.RIGHT_BRACE, "Expected '}' after class body.");

		return new Statement.Class(name, superclass, methods);
	}

	private Statement statement() {
		if (this.advanceIfNextTokenMatches(Token.TokenType.FOR)) return this.forStatement();
		if (this.advanceIfNextTokenMatches(Token.TokenType.RETURN)) return this.returnStatement();
		if (this.advanceIfNextTokenMatches(Token.TokenType.BREAK)) return this.breakStatement();
		if (this.advanceIfNextTokenMatches(Token.TokenType.LEFT_BRACE)) return new Statement.Block(this.block());
		if (this.advanceIfNextTokenMatches(Token.TokenType.IF)) return this.ifStatement();
		if (this.advanceIfNextTokenMatches(Token.TokenType.WHILE)) return this.whileStatement();

		return this.expressionStatement();
	}

	private Statement forStatement() {
		this.consumeIfNextTokenMatches(Token.TokenType.LEFT_PAREN, "Expected '(' after 'for'.");

		Statement initializer;
		if (this.advanceIfNextTokenMatches(Token.TokenType.SEMICOLON)) {
			initializer = null;
		} else if (this.advanceIfNextTokenMatches(Token.TokenType.VAR)) {
			initializer = this.varDeclaration(false);
		} else {
			initializer = this.expressionStatement();
		}

		Expression condition = null;
		if (!this.isNextToken(Token.TokenType.SEMICOLON)) {
			condition = this.expression();
		}
		this.consumeIfNextTokenMatches(Token.TokenType.SEMICOLON, "Expected ';' after for loop condition.");

		Expression increment = null;
		if (!this.isNextToken(Token.TokenType.RIGHT_PAREN)) {
			increment = this.expression();
		}
		this.consumeIfNextTokenMatches(Token.TokenType.RIGHT_PAREN, "Expected ')' after for clauses.");

		Statement body = this.statement();

		// Make a while loop out of the for loop
		if (increment != null) {
			body = new Statement.Block(Arrays.asList(body, new Statement.Expression(increment)));
		}

		if (condition == null) condition = new Expression.Literal(true);
		body = new Statement.While(condition, body);

		if (initializer != null) {
			body = new Statement.Block(Arrays.asList(initializer, body));
		}


		return body;
	}

	private Statement ifStatement() {
		this.consumeIfNextTokenMatches(Token.TokenType.LEFT_PAREN, "Expect '(' after 'if'.");
		Expression condition = this.expression();
		this.consumeIfNextTokenMatches(Token.TokenType.RIGHT_PAREN, "Expect ')' after if condition.");

		Statement thenBranch = this.statement();
		Statement elseBranch = null;
		if (this.advanceIfNextTokenMatches(Token.TokenType.ELSE)) {
			elseBranch = this.statement();
		}

		return new Statement.If(condition, thenBranch, elseBranch);
	}

	private Statement returnStatement() {
		Token keyword = this.getPreviousToken();
		Expression value = null;
		if (!this.isNextToken(Token.TokenType.SEMICOLON)) {
			value = this.expression();
		}

		this.consumeIfNextTokenMatches(Token.TokenType.SEMICOLON, "Expected ';' after return value.");
		return new Statement.Return(keyword, value);
	}

	private Statement breakStatement() {
		Token keyword = this.getPreviousToken();
		this.consumeIfNextTokenMatches(Token.TokenType.SEMICOLON, "Expected ';' after break.");
		return new Statement.Break(keyword);
	}

	private Statement varDeclaration(boolean staticc) {
		Token name = this.consumeIfNextTokenMatches(Token.TokenType.IDENTIFIER, "Expected variable name.");

		Expression initializer = null;
		if (this.advanceIfNextTokenMatches(Token.TokenType.EQUAL)) {
			initializer = this.expression();
		}

		this.consumeIfNextTokenMatches(Token.TokenType.SEMICOLON, "Expected ';' after variable declaration.");
		return new Statement.Var(name, initializer, staticc);
	}

	private Statement whileStatement() {
		this.consumeIfNextTokenMatches(Token.TokenType.LEFT_PAREN, "Expected '(' after 'while'.");
		Expression condition = this.expression();
		this.consumeIfNextTokenMatches(Token.TokenType.RIGHT_PAREN, "Expected ')' after condition.");
		Statement body = this.statement();

		return new Statement.While(condition, body);
	}

	private Statement expressionStatement() {
		Expression expression = this.expression();
		this.consumeIfNextTokenMatches(Token.TokenType.SEMICOLON, "Expected ';' after expression.");
		return new Statement.Expression(expression);
	}

	private Statement.Function function(String kind) {
		Token name = this.consumeIfNextTokenMatches(Token.TokenType.IDENTIFIER, "Expected " + kind + " name.");

		this.consumeIfNextTokenMatches(Token.TokenType.LEFT_PAREN, "Expected '(' after " + kind + " name.");
		List<Token> parameters = new ArrayList<>();
		if (!this.isNextToken(Token.TokenType.RIGHT_PAREN)) {
			do {
				parameters.add(this.consumeIfNextTokenMatches(Token.TokenType.IDENTIFIER, "Expected parameter name."));
			} while (this.advanceIfNextTokenMatches(Token.TokenType.COMMA));
		}
		this.consumeIfNextTokenMatches(Token.TokenType.RIGHT_PAREN, "Expected ')' after parameters.");

		this.consumeIfNextTokenMatches(Token.TokenType.LEFT_BRACE, "Expected '{' before " + kind + " body.");
		List<Statement> body = this.block();
		return new Statement.Function(name, parameters, body);
	}

	/**
	 * This function assumes the <code>LEFT_BRACE</code> token has already been consumed.
	 **/
	private List<Statement> block() {
		List<Statement> statements = new ArrayList<>();

		while (!this.isNextToken(Token.TokenType.RIGHT_BRACE) && !this.isAtEnd()) {
			statements.add(this.declaration());
		}

		this.consumeIfNextTokenMatches(Token.TokenType.RIGHT_BRACE, "Expected '}' after block.");
		return statements;
	}

	private Expression assignment() {
		Expression expression = this.or();

		if (this.advanceIfNextTokenMatches(Token.TokenType.EQUAL)) {
			Token equals = this.getPreviousToken();
			Expression value = this.assignment();

			if (expression instanceof Expression.Variable var) return new Expression.Assign(var.name, value);
			if (expression instanceof Expression.Get get) return new Expression.Set(get.object, get.name, value);

			this.error(equals, "Invalid assignment target.");
		} else if (this.advanceIfNextTokenMatches(Token.TokenType.PLUS_PLUS, Token.TokenType.MINUS_MINUS)) {
			Token operator = this.getPreviousToken();
			if (expression instanceof Expression.Variable var)
				return new Expression.Assign(var.name,
						new Expression.Binary(expression, operator, new Expression.Literal(1.0d)));
			if (expression instanceof Expression.Get get)
				return new Expression.Set(get.object, get.name,
						new Expression.Binary(expression, operator, new Expression.Literal(1.0d)));
		} else if (this.advanceIfNextTokenMatches(
				Token.TokenType.PLUS_EQUAL, Token.TokenType.MINUS_EQUAL, Token.TokenType.STAR_EQUAL,
				Token.TokenType.SLASH_EQUAL, Token.TokenType.CARET_EQUAL)) {
			Token operatorEqual = this.getPreviousToken();
			Token operator = this.createTokenFromOperatorEquals(operatorEqual);
			Expression value = this.assignment();
			if (expression instanceof Expression.Variable var)
				return new Expression.Assign(var.name, new Expression.Binary(expression, operator, value));
			if (expression instanceof Expression.Get get)
				return new Expression.Set(get.object, get.name, new Expression.Binary(expression, operator, value));
		}

		return expression;
	}

	private Expression or() {
		Expression expression = this.and();

		while (this.advanceIfNextTokenMatches(Token.TokenType.OR)) {
			Token operator = this.getPreviousToken();
			Expression right = this.and();
			expression = new Expression.Logical(expression, operator, right);
		}

		return expression;
	}

	private Expression and() {
		Expression expression = this.equality();

		while (this.advanceIfNextTokenMatches(Token.TokenType.AND)) {
			Token operator = this.getPreviousToken();
			Expression right = this.equality();
			expression = new Expression.Logical(expression, operator, right);
		}

		return expression;
	}

	private Expression equality() {
		Expression expression = this.comparison();

		while (this.advanceIfNextTokenMatches(Token.TokenType.BANG_EQUAL, Token.TokenType.EQUAL_EQUAL)) {
			Token operator = this.getPreviousToken();
			Expression right = this.comparison();
			expression = new Expression.Binary(expression, operator, right);
		}

		return expression;
	}

	private Expression comparison() {
		Expression expression = this.term();

		while (this.advanceIfNextTokenMatches(
				Token.TokenType.GREATER, Token.TokenType.GREATER_EQUAL, Token.TokenType.LESS,
				Token.TokenType.LESS_EQUAL)) {
			Token operator = this.getPreviousToken();
			Expression right = this.term();
			expression = new Expression.Binary(expression, operator, right);
		}

		return expression;
	}

	private Expression term() {
		Expression expression = this.factor();

		while (this.advanceIfNextTokenMatches(Token.TokenType.MINUS, Token.TokenType.PLUS)) {
			Token operator = this.getPreviousToken();
			Expression right = this.factor();
			expression = new Expression.Binary(expression, operator, right);
		}

		return expression;
	}

	private Expression factor() {
		Expression expression = this.exponent();

		while (this.advanceIfNextTokenMatches(Token.TokenType.SLASH, Token.TokenType.STAR)) {
			Token operator = this.getPreviousToken();
			Expression right = this.exponent();
			expression = new Expression.Binary(expression, operator, right);
		}

		return expression;
	}

	private Expression exponent() {
		Expression expression = this.unary();

		while (this.advanceIfNextTokenMatches(Token.TokenType.CARET)) {
			Token operator = this.getPreviousToken();
			Expression right = this.unary();
			expression = new Expression.Binary(expression, operator, right);
		}

		return expression;
	}

	private Expression unary() {
		if (this.advanceIfNextTokenMatches(Token.TokenType.BANG, Token.TokenType.MINUS)) {
			Token operator = this.getPreviousToken();
			Expression right = this.unary();
			return new Expression.Unary(operator, right);
		}

		return this.call();
	}

	private Expression call() {
		Expression expression = this.primary();

		while (true) {
			if (this.advanceIfNextTokenMatches(Token.TokenType.LEFT_PAREN)) {
				expression = this.finishCall(expression);
			} else if (this.advanceIfNextTokenMatches(Token.TokenType.DOT)) {
				Token name = this.consumeIfNextTokenMatches(Token.TokenType.IDENTIFIER,
						"Expected property name after '.'.");
				expression = new Expression.Get(expression, name);
			} else {
				break;
			}
		}

		return expression;
	}

	private Expression finishCall(Expression callee) {
		List<Expression> arguments = new ArrayList<>();
		if (!this.isNextToken(Token.TokenType.RIGHT_PAREN)) {
			do {
				arguments.add(this.expression());
			} while (this.advanceIfNextTokenMatches(Token.TokenType.COMMA));
		}

		Token paren = this.consumeIfNextTokenMatches(Token.TokenType.RIGHT_PAREN, "Expected ')' after arguments.");

		return new Expression.Call(callee, paren, arguments);
	}

	private Expression primary() {
		if (this.advanceIfNextTokenMatches(Token.TokenType.FALSE)) return new Expression.Literal(false);
		if (this.advanceIfNextTokenMatches(Token.TokenType.TRUE)) return new Expression.Literal(true);
		if (this.advanceIfNextTokenMatches(Token.TokenType.NULL)) return new Expression.Literal(null);

		if (this.advanceIfNextTokenMatches(Token.TokenType.NUMBER, Token.TokenType.STRING)) {
			return new Expression.Literal(this.getPreviousToken().literal);
		}

		if (this.advanceIfNextTokenMatches(Token.TokenType.SUPER)) {
			Token keyword = this.getPreviousToken();
			this.consumeIfNextTokenMatches(Token.TokenType.DOT, "Expected '.' after 'super'.");
			Token method = this.consumeIfNextTokenMatches(Token.TokenType.IDENTIFIER,
					"Expected superclass method name.");
			return new Expression.Super(keyword, method);
		}

		if (this.advanceIfNextTokenMatches(Token.TokenType.THIS)) return new Expression.This(this.getPreviousToken());

		if (this.advanceIfNextTokenMatches(Token.TokenType.IDENTIFIER)) {
			return new Expression.Variable(this.getPreviousToken());
		}

		if (this.advanceIfNextTokenMatches(Token.TokenType.LEFT_PAREN)) {
			Expression expression = this.expression();
			this.consumeIfNextTokenMatches(Token.TokenType.RIGHT_PAREN, "Expected ')' after expression.");
			return new Expression.Grouping(expression);
		}

		throw this.error(this.getCurrentToken(), "Expected expression.");
	}

	// Helpers

	/**
	 * Returns if next Token matches <code>expected</code>.
	 *
	 * @param expectedTokenTypes The expected next {@link Token.TokenType}, can be multiple.
	 * @return If next Token matches <code>expected</code>.
	 **/
	private boolean advanceIfNextTokenMatches(Token.TokenType... expectedTokenTypes) {
		for (Token.TokenType type : expectedTokenTypes) {
			if (this.isNextToken(type)) {
				this.advance();
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if next Token matches <code>expectedTokenType</code> and consumes it if so.
	 * <br>
	 * If the next Token doesn't match, throws an error with <code>errorMessage</code> as message.
	 *
	 * @param expectedTokenType The expectedTokenType next {@link Token.TokenType}.
	 * @return The previous Token if <code>expectedTokenType</code> matches.
	 **/
	private Token consumeIfNextTokenMatches(Token.TokenType expectedTokenType, String errorMessage) {
		if (this.isNextToken(expectedTokenType)) return this.advance();

		throw this.error(this.getCurrentToken(), errorMessage);
	}

	private boolean isNextToken(Token.TokenType type) {
		if (this.isAtEnd()) return false;
		return this.getCurrentToken().type == type;
	}

	private Token advance() {
		if (!this.isAtEnd()) this.current++;
		return this.getPreviousToken();
	}

	private boolean isAtEnd() {
		return this.getCurrentToken().type == Token.TokenType.EOF;
	}

	private Token getCurrentToken() {
		return this.tokens.get(this.current);
	}

	private Token getPreviousToken() {
		return this.tokens.get(this.current - 1);
	}

	private Token createTokenFromOperatorEquals(Token operatorEqual) {
		switch (operatorEqual.type) {
			case PLUS_EQUAL:
				return new Token(Token.TokenType.PLUS, "+", null, operatorEqual.line);
			case MINUS_EQUAL:
				return new Token(Token.TokenType.MINUS, "-", null, operatorEqual.line);
			case STAR_EQUAL:
				return new Token(Token.TokenType.STAR, "*", null, operatorEqual.line);
			case SLASH_EQUAL:
				return new Token(Token.TokenType.SLASH, "/", null, operatorEqual.line);
			case CARET_EQUAL:
				return new Token(Token.TokenType.CARET, "^", null, operatorEqual.line);
		}
		//Unreachable
		return null;
	}

	private ParseError error(Token token, String message) {
		this.roboScriptInstance.error(token, message);
		return new ParseError();
	}

	private void synchronize() {
		this.advance();

		while (!this.isAtEnd()) {
			if (this.getPreviousToken().type == Token.TokenType.SEMICOLON) return;

			switch (this.getCurrentToken().type) {
				case CLASS, FUNC, VAR, FOR, IF, WHILE, RETURN -> {
					return;
				}
			}

			this.advance();
		}
	}

	private static class ParseError extends RuntimeException {
	}
}
