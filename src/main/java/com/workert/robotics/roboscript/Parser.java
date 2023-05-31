package com.workert.robotics.roboscript;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.workert.robotics.roboscript.Token.TokenType.*;

public class Parser {
	private final RoboScript roboScriptInstance;
	private final List<Token> tokens;
	private int current = 0;

	Parser(RoboScript roboScriptInstance, List<Token> tokens) {
		this.roboScriptInstance = roboScriptInstance;
		this.tokens = tokens;
	}

	List<Stmt> parse() {
		List<Stmt> statements = new ArrayList<>();
		while (!this.isAtEnd()) {
			statements.add(this.declaration());
		}
		return statements;
	}

	private Expr expression() {
		return this.assignment();
	}

	private Stmt declaration() {
		try {
			if (this.advanceIfNextTokenMatches(CLASS)) return this.classDeclaration();
			if (this.advanceIfNextTokenMatches(FUNC)) return this.function("function");
			if (this.advanceIfNextTokenMatches(VAR)) return this.varDeclaration();

			return this.statement();
		} catch (ParseError error) {
			this.synchronize();
			return null;
		}
	}

	private Stmt classDeclaration() {
		Token name = this.consumeIfNextTokenMatches(IDENTIFIER, "Expected class name.");

		Expr.Variable superclass = null;
		if (this.advanceIfNextTokenMatches(EXTENDS)) {
			this.consumeIfNextTokenMatches(IDENTIFIER, "Expected superclass name.");
			superclass = new Expr.Variable(this.getPreviousToken());
		}

		this.consumeIfNextTokenMatches(LEFT_BRACE, "Expected '{' before class body.");

		List<Stmt.Function> methods = new ArrayList<>();
		while (!this.isNextToken(RIGHT_BRACE) && !this.isAtEnd()) {
			this.consumeIfNextTokenMatches(FUNC, "Expected 'func' token.");
			methods.add(this.function("method"));
		}

		this.consumeIfNextTokenMatches(RIGHT_BRACE, "Expected '}' after class body.");

		return new Stmt.Class(name, superclass, methods);
	}

	private Stmt statement() {
		if (this.advanceIfNextTokenMatches(FOR)) return this.forStatement();
		if (this.advanceIfNextTokenMatches(RETURN)) return this.returnStatement();
		if (this.advanceIfNextTokenMatches(BREAK)) return this.breakStatement();
		if (this.advanceIfNextTokenMatches(LEFT_BRACE)) return new Stmt.Block(this.block());
		if (this.advanceIfNextTokenMatches(IF)) return this.ifStatement();
		if (this.advanceIfNextTokenMatches(WHILE)) return this.whileStatement();

		return this.expressionStatement();
	}

	private Stmt forStatement() {
		this.consumeIfNextTokenMatches(LEFT_PAREN, "Expected '(' after 'for'.");

		Stmt initializer;
		if (this.advanceIfNextTokenMatches(SEMICOLON)) {
			initializer = null;
		} else if (this.advanceIfNextTokenMatches(VAR)) {
			initializer = this.varDeclaration();
		} else {
			initializer = this.expressionStatement();
		}

		Expr condition = null;
		if (!this.isNextToken(SEMICOLON)) {
			condition = this.expression();
		}
		this.consumeIfNextTokenMatches(SEMICOLON, "Expected ';' after for loop condition.");

		Expr increment = null;
		if (!this.isNextToken(RIGHT_PAREN)) {
			increment = this.expression();
		}
		this.consumeIfNextTokenMatches(RIGHT_PAREN, "Expected ')' after for clauses.");

		Stmt body = this.statement();

		// Make a while loop out of the for loop
		if (increment != null) {
			body = new Stmt.Block(Arrays.asList(body, new Stmt.Expression(increment)));
		}

		if (condition == null) condition = new Expr.Literal(true);
		body = new Stmt.While(condition, body);

		if (initializer != null) {
			body = new Stmt.Block(Arrays.asList(initializer, body));
		}


		return body;
	}

	private Stmt ifStatement() {
		this.consumeIfNextTokenMatches(LEFT_PAREN, "Expect '(' after 'if'.");
		Expr condition = this.expression();
		this.consumeIfNextTokenMatches(RIGHT_PAREN, "Expect ')' after if condition.");

		Stmt thenBranch = this.statement();
		Stmt elseBranch = null;
		if (this.advanceIfNextTokenMatches(ELSE)) {
			elseBranch = this.statement();
		}

		return new Stmt.If(condition, thenBranch, elseBranch);
	}

	private Stmt returnStatement() {
		Token keyword = this.getPreviousToken();
		Expr value = null;
		if (!this.isNextToken(SEMICOLON)) {
			value = this.expression();
		}

		this.consumeIfNextTokenMatches(SEMICOLON, "Expected ';' after return value.");
		return new Stmt.Return(keyword, value);
	}

	private Stmt breakStatement() {
		Token keyword = this.getPreviousToken();
		this.consumeIfNextTokenMatches(SEMICOLON, "Expected ';' after break.");
		return new Stmt.Break(keyword);
	}

	private Stmt varDeclaration() {
		Token name = this.consumeIfNextTokenMatches(IDENTIFIER, "Expected variable name.");

		Expr initializer = null;
		if (this.advanceIfNextTokenMatches(EQUAL)) {
			initializer = this.expression();
		}

		this.consumeIfNextTokenMatches(SEMICOLON, "Expected ';' after variable declaration.");
		return new Stmt.Var(name, initializer);
	}

	private Stmt whileStatement() {
		this.consumeIfNextTokenMatches(LEFT_PAREN, "Expected '(' after 'while'.");
		Expr condition = this.expression();
		this.consumeIfNextTokenMatches(RIGHT_PAREN, "Expected ')' after condition.");
		Stmt body = this.statement();

		return new Stmt.While(condition, body);
	}

	private Stmt expressionStatement() {
		Expr expr = this.expression();
		this.consumeIfNextTokenMatches(SEMICOLON, "Expected ';' after expression.");
		return new Stmt.Expression(expr);
	}

	private Stmt.Function function(String kind) {
		Token name = this.consumeIfNextTokenMatches(IDENTIFIER, "Expected " + kind + " name.");

		this.consumeIfNextTokenMatches(LEFT_PAREN, "Expected '(' after " + kind + " name.");
		List<Token> parameters = new ArrayList<>();
		if (!this.isNextToken(RIGHT_PAREN)) {
			do {
				parameters.add(this.consumeIfNextTokenMatches(IDENTIFIER, "Expected parameter name."));
			} while (this.advanceIfNextTokenMatches(COMMA));
		}
		this.consumeIfNextTokenMatches(RIGHT_PAREN, "Expected ')' after parameters.");

		this.consumeIfNextTokenMatches(LEFT_BRACE, "Expected '{' before " + kind + " body.");
		List<Stmt> body = this.block();
		return new Stmt.Function(name, parameters, body);
	}

	/**
	 * This function assumes the <code>LEFT_BRACE</code> token has already been consumed.
	 **/
	private List<Stmt> block() {
		List<Stmt> statements = new ArrayList<>();

		while (!this.isNextToken(RIGHT_BRACE) && !this.isAtEnd()) {
			statements.add(this.declaration());
		}

		this.consumeIfNextTokenMatches(RIGHT_BRACE, "Expected '}' after block.");
		return statements;
	}

	private Expr assignment() {
		Expr expr = this.or();

		if (this.advanceIfNextTokenMatches(EQUAL)) {
			Token equals = this.getPreviousToken();
			Expr value = this.assignment();

			if (expr instanceof Expr.Variable) {
				Token name = ((Expr.Variable) expr).name;
				return new Expr.Assign(name, value);
			} else if (expr instanceof Expr.Get) {
				Expr.Get get = (Expr.Get) expr;
				return new Expr.Set(get.object, get.name, value);
			}
			this.error(equals, "Invalid assignment target.");
		}

		return expr;
	}

	private Expr or() {
		Expr expr = this.and();

		while (this.advanceIfNextTokenMatches(OR)) {
			Token operator = this.getPreviousToken();
			Expr right = this.and();
			expr = new Expr.Logical(expr, operator, right);
		}

		return expr;
	}

	private Expr and() {
		Expr expr = this.equality();

		while (this.advanceIfNextTokenMatches(AND)) {
			Token operator = this.getPreviousToken();
			Expr right = this.equality();
			expr = new Expr.Logical(expr, operator, right);
		}

		return expr;
	}

	private Expr equality() {
		Expr expr = this.comparison();

		while (this.advanceIfNextTokenMatches(BANG_EQUAL, EQUAL_EQUAL)) {
			Token operator = this.getPreviousToken();
			Expr right = this.comparison();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr comparison() {
		Expr expr = this.term();

		while (this.advanceIfNextTokenMatches(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
			Token operator = this.getPreviousToken();
			Expr right = this.term();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr term() {
		Expr expr = this.factor();

		while (this.advanceIfNextTokenMatches(MINUS, PLUS)) {
			Token operator = this.getPreviousToken();
			Expr right = this.factor();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr factor() {
		Expr expr = this.unary();

		while (this.advanceIfNextTokenMatches(SLASH, STAR)) {
			Token operator = this.getPreviousToken();
			Expr right = this.unary();
			expr = new Expr.Binary(expr, operator, right);
		}

		return expr;
	}

	private Expr unary() {
		if (this.advanceIfNextTokenMatches(BANG, MINUS)) {
			Token operator = this.getPreviousToken();
			Expr right = this.unary();
			return new Expr.Unary(operator, right);
		}

		return this.call();
	}

	private Expr call() {
		Expr expr = this.primary();

		while (true) {
			if (this.advanceIfNextTokenMatches(LEFT_PAREN)) {
				expr = this.finishCall(expr);
			} else if (this.advanceIfNextTokenMatches(DOT)) {
				Token name = this.consumeIfNextTokenMatches(IDENTIFIER, "Expected property name after '.'.");
				expr = new Expr.Get(expr, name);
			} else {
				break;
			}
		}

		return expr;
	}

	private Expr finishCall(Expr callee) {
		List<Expr> arguments = new ArrayList<>();
		if (!this.isNextToken(RIGHT_PAREN)) {
			do {
				arguments.add(this.expression());
			} while (this.advanceIfNextTokenMatches(COMMA));
		}

		Token paren = this.consumeIfNextTokenMatches(RIGHT_PAREN, "Expected ')' after arguments.");

		return new Expr.Call(callee, paren, arguments);
	}

	private Expr primary() {
		if (this.advanceIfNextTokenMatches(FALSE)) return new Expr.Literal(false);
		if (this.advanceIfNextTokenMatches(TRUE)) return new Expr.Literal(true);
		if (this.advanceIfNextTokenMatches(NULL)) return new Expr.Literal(null);

		if (this.advanceIfNextTokenMatches(NUMBER, STRING)) {
			return new Expr.Literal(this.getPreviousToken().literal);
		}

		if (this.advanceIfNextTokenMatches(SUPER)) {
			Token keyword = this.getPreviousToken();
			this.consumeIfNextTokenMatches(DOT, "Expected '.' after 'super'.");
			Token method = this.consumeIfNextTokenMatches(IDENTIFIER, "Expected superclass method name.");
			return new Expr.Super(keyword, method);
		}

		if (this.advanceIfNextTokenMatches(THIS)) return new Expr.This(this.getPreviousToken());

		if (this.advanceIfNextTokenMatches(IDENTIFIER)) {
			return new Expr.Variable(this.getPreviousToken());
		}

		if (this.advanceIfNextTokenMatches(LEFT_PAREN)) {
			Expr expr = this.expression();
			this.consumeIfNextTokenMatches(RIGHT_PAREN, "Expected ')' after expression.");
			return new Expr.Grouping(expr);
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
		return this.getCurrentToken().type == EOF;
	}

	private Token getCurrentToken() {
		return this.tokens.get(this.current);
	}

	private Token getPreviousToken() {
		return this.tokens.get(this.current - 1);
	}

	private ParseError error(Token token, String message) {
		this.roboScriptInstance.error(token, message);
		return new ParseError();
	}

	private void synchronize() {
		this.advance();

		while (!this.isAtEnd()) {
			if (this.getPreviousToken().type == SEMICOLON) return;

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
