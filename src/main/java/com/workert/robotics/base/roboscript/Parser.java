package com.workert.robotics.base.roboscript;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import static com.workert.robotics.base.roboscript.Token.TokenType.*;

public final class Parser {
	final RoboScript roboScriptInstance;
	Scanner scanner;

	Token current;
	Token previous;


	Parser(RoboScript roboScriptInstance) {
		this.roboScriptInstance = roboScriptInstance;
	}

	List<Statement> parse() {
		List<Statement> statements = new ArrayList<>();
		while (!this.isAtEnd()) {
			statements.add(this.declaration());
		}
		return statements;
	}

	private List<Statement> block() {
		List<Statement> block = new ArrayList<>();
		while (!this.isNextToken(RIGHT_BRACE) && !this.isAtEnd()) {
			block.add(this.declaration());
		}
		this.consumeOrThrow(RIGHT_BRACE, "Expected '}' after block.");
		return block;
	}

	private Statement declaration() {
		try {
			if (this.checkAndConsumeIfMatches(VAR)) {
				return this.varDeclaration();
			} else if (this.checkAndConsumeIfMatches(FUNCTION)) {
				return this.functionDeclaration();
			} else {
				return this.statement();
			}
		} catch (ParseError error) {
			// eventually synchronize
			return null;
		}
	}

	private Statement.Var varDeclaration() {
		// Token var = this.previous;
		Statement.VarDeclaration declaration = this.parseVariable("variable");
		Expression initializer = this.checkAndConsumeIfMatches(EQUAL) ? this.expression() : null;
		this.consumeOrThrow(SEMICOLON, "Expected ';' after variable declaration.");
		return new Statement.Var(declaration, initializer);
	}

	private Statement.Function functionDeclaration() {
		this.consumeOrThrow(IDENTIFIER, "Expected function name after 'func'.");
		Token name = this.previous;

		this.consumeOrThrow(LEFT_PAREN, "Expected '(' after function name.");
		List<Statement.VarDeclaration> parameters = new ArrayList<>();
		if (!this.isNextToken(RIGHT_PAREN)) {
			do {
				parameters.add(this.parseVariable("parameter"));
			} while (this.checkAndConsumeIfMatches(COMMA));
		}
		List<Statement> body = this.block();
		this.consumeOrThrow(RIGHT_PAREN, "Expected ')' after function parameters.");

		return new Statement.Function(name, body, parameters);
	}

	private Statement.VarDeclaration parseVariable(String variableType) {
		this.consumeOrThrow(IDENTIFIER, "Expected " + variableType + " name after 'var'.");
		Token name = this.previous;
		Token type = null;
		boolean nullSafe = true;
		if (this.checkAndConsumeIfMatches(COLON)) {
			// a type is defined
			if (!this.checkAndConsumeIfMatches(IDENTIFIER, ANY, NUMBER, STRING, BOOL, LIST, RANGE)) {
				throw this.error("Expected a type after ':' in variable declaration.");
			}
			type = this.previous;
			nullSafe = this.checkAndConsumeIfMatches(QUESTION);
		}
		return new Statement.VarDeclaration(name, type, nullSafe);
	}

	private Statement statement() {
		if (this.checkAndConsumeIfMatches(IF)) {
			return this.ifStatement();
		} else if (this.checkAndConsumeIfMatches(WHILE)) {
			return this.whileStatement();
		} else if (this.checkAndConsumeIfMatches(FOR)) {
			return this.forStatement();
		} else if (this.checkAndConsumeIfMatches(LOOP)) {
			return this.loopStatement();
		} else if (this.checkAndConsumeIfMatches(RETURN)) {
			return this.returnStatement();
		} else {
			return this.expressionStatement();
		}
	}

	private Statement.If ifStatement() {
		Expression condition = this.expression();
		this.consumeOrThrow(LEFT_BRACE, "Expected '{' after if condition expression.");
		List<Statement> thenBlock = this.block();
		List<Statement> elseBlock = null;
		if (this.checkAndConsumeIfMatches(ELSE)) {
			this.consumeOrThrow(LEFT_BRACE, "Expected '{' after 'else'.");
			elseBlock = this.block();
		} else if (this.checkAndConsumeIfMatches(ELIF)) {
			elseBlock = List.of(this.ifStatement());
		}
		return new Statement.If(condition, thenBlock, elseBlock);
	}

	private Statement.While whileStatement() {
		Expression condition = this.expression();
		this.consumeOrThrow(LEFT_BRACE, "Expected '{' after while condition expression.");
		List<Statement> body = this.block();
		return new Statement.While(condition, body);
	}

	private Statement.For forStatement() {
		Statement.VarDeclaration loopVariable = this.parseVariable("variable");
		this.consumeOrThrow(COLON, "Expected colon after for loop variable declaration.");
		Expression iterable = this.expression();
		this.consumeOrThrow(LEFT_BRACE, "Expected '{' after for loop iterable.");
		List<Statement> body = this.block();
		return new Statement.For(loopVariable, iterable, body);
	}

	private Statement.Loop loopStatement() {
		this.consumeOrThrow(LEFT_BRACE, "Expected '{' after 'loop'.");
		return new Statement.Loop(this.block());
	}

	private Statement.Return returnStatement() {
		Token keyword = this.previous;
		Expression returnValue = null;
		if (!this.isNextToken(SEMICOLON)) {
			returnValue = this.expression();
		}
		this.consumeOrThrow(SEMICOLON, "Expected semicolon after return statement.");
		return new Statement.Return(keyword, returnValue);
	}

	private Statement.Expression expressionStatement() {
		Expression expression = this.expression();
		this.consumeOrThrow(SEMICOLON, "Expected ';' after expression.");
		return new Statement.Expression(expression);
	}

	private Expression expression() {
		return this.assignment(); // start the precedence chain
	}

	private Expression assignment() {
		Expression expression = this.or();
		if (this.checkAndConsumeIfMatches(EQUAL)) {
			Token equals = this.previous;
			Expression value = this.assignment();
			if (expression instanceof Expression.Variable var)
				return new Expression.Assign(var, value);
			this.errorAt(equals, "Invalid assignment target.");
		}
		return expression;
	}

	private Expression or() {
		Expression expression = this.and();
		while (this.checkAndConsumeIfMatches(OR)) {
			Token operator = this.previous;
			Expression right = this.and();
			expression = new Expression.Logical(expression, operator, right);
		}
		return expression;
	}

	private Expression and() {
		Expression expression = this.equality();
		while (this.checkAndConsumeIfMatches(OR)) {
			Token operator = this.previous;
			Expression right = this.and();
			expression = new Expression.Logical(expression, operator, right);
		}
		return expression;
	}

	private Expression equality() {
		Expression expression = this.comparison();
		while (this.checkAndConsumeIfMatches(BANG_EQUAL, EQUAL_EQUAL)) {
			Token operator = this.previous;
			Expression right = this.comparison();
			expression = new Expression.Binary(expression, operator, right);
		}
		return expression;
	}

	private Expression comparison() {
		Expression expression = this.modulo();
		while (this.checkAndConsumeIfMatches(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
			Token operator = this.previous;
			Expression right = this.modulo();
			expression = new Expression.Binary(expression, operator, right);
		}
		return expression;
	}

	private Expression modulo() {
		Expression expression = this.term();
		while (this.checkAndConsumeIfMatches(PERCENT)) {
			Token operator = this.previous;
			Expression right = this.term();
			expression = new Expression.Binary(expression, operator, right);
		}
		return expression;
	}

	private Expression term() {
		Expression expression = this.factor();
		while (this.checkAndConsumeIfMatches(MINUS, PLUS)) {
			Token operator = this.previous;
			Expression right = this.factor();
			expression = new Expression.Binary(expression, operator, right);
		}
		return expression;
	}

	private Expression factor() {
		Expression expression = this.exponent();
		while (this.checkAndConsumeIfMatches(SLASH, STAR)) {
			Token operator = this.previous;
			Expression right = this.exponent();
			expression = new Expression.Binary(expression, operator, right);
		}
		return expression;
	}

	private Expression exponent() {
		Expression expression = this.unary();
		return expression;
	}

	private Expression unary() {
		if (this.checkAndConsumeIfMatches(BANG, MINUS)) {
			Token operator = this.previous;
			Expression right = this.unary();
			return new Expression.Unary(operator, right);
		}
		return this.call();
	}

	private Expression call() {
		Expression expression = this.primary();

		while (true) {
			if (this.checkAndConsumeIfMatches(LEFT_PAREN)) {
				expression = this.finishCall(expression);
			} else if (this.checkAndConsumeIfMatches(DOT)) {
				// getting from classes
			} else if (this.checkAndConsumeIfMatches(LEFT_BRACKET)) {
				// getting from iterables
			} else {
				break;
			}
		}
		return expression;
	}

	private Expression primary() {
		if (this.checkAndConsumeIfMatches(FALSE)) return new Expression.BoolLiteral(false);
		if (this.checkAndConsumeIfMatches(TRUE)) return new Expression.BoolLiteral(true);
		if (this.checkAndConsumeIfMatches(NULL)) return new Expression.NullLiteral();

		if (this.checkAndConsumeIfMatches(DOUBLE_VALUE))
			return new Expression.DoubleLiteral(Double.parseDouble(this.previous.lexeme));

		if (this.checkAndConsumeIfMatches(IDENTIFIER))
			return new Expression.Variable(this.previous);
		if (this.checkAndConsumeIfMatches(LEFT_PAREN)) {
			// TODO: if i feel like adding tuples come back and fix this
			Expression expression = this.expression();
			this.consumeOrThrow(RIGHT_PAREN, "Expected ')' after expression.");
			return new Expression.Grouping(expression);
		}

		if (this.checkAndConsumeIfMatches(LEFT_BRACKET)) {
			// TODO: Come back for arrays
		}

		if (this.checkAndConsumeIfMatches(LEFT_BRACE)) {
			// TODO: Come back for maps
		}
		throw this.errorAtCurrent("Expected expression.");
	}

	/**
	 * Generates an Expression.Call for any expressions that are called using () (functions, classes).
	 *
	 * @param callee The expression being called
	 * @return An Expression.Call syntax node.
	 */
	private Expression.Call finishCall(Expression callee) {
		List<Expression> arguments = new ArrayList<>();
		if (!this.isNextToken(RIGHT_PAREN)) {
			do {
				arguments.add(this.expression());
			} while (this.checkAndConsumeIfMatches(COMMA));
		}
		this.consumeOrThrow(RIGHT_PAREN, "Expected ')' after arguments.");

		return new Expression.Call(callee, this.previous, arguments);
	}


	boolean isAtEnd() {
		return this.current.type == EOF;
	}


	private void advance() {
		this.previous = this.current;

		this.current = this.scanner.scanToken();
		if (this.current.type == ERROR)
			throw this.errorAtCurrent(this.current.lexeme);
	}


	private void consumeOrThrow(Token.TokenType type, String message) {
		if (this.current.type == type) {
			this.advance();
			return;
		}
		throw this.errorAtCurrent(message);
	}

	private boolean checkAndConsumeIfMatches(Token.TokenType type) {
		if (this.current.type != type) return false;
		this.advance();
		return true;
	}

	private boolean checkAndConsumeIfMatches(Token.TokenType... types) {
		if (this.isNextToken(types)) {
			this.advance();
			return true;
		}
		return false;
	}

	private boolean isNextToken(Token.TokenType type) {
		return this.current.type == type;
	}

	private boolean isNextToken(Token.TokenType... types) {
		for (Token.TokenType type : types) {
			if (this.current.type == type) {
				return true;
			}
		}
		return false;
	}


	private ParseError error(String message) {
		return this.errorAt(this.previous, message);
	}

	private ParseError errorAtCurrent(String message) {
		return this.errorAt(this.current, message);
	}

	private ParseError errorAt(Token token, String message) {
		String finalMessage = "Error";
		if (token.type == EOF) {
			finalMessage += " at end";
		} else if (token.type != ERROR) {
			finalMessage += " at " + token.lexeme;
		} else {
			finalMessage += " with scanning";
		}
		finalMessage += ": '" + message + "'";
		this.roboScriptInstance.reportCompileError(token.line, finalMessage);
		return new ParseError();
	}


	private static class ParseError extends RuntimeException {
		@Serial
		private static final long serialVersionUID = -187581590684984588L;
	}
}
