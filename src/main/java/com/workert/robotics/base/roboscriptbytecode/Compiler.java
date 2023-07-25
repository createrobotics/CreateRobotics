package com.workert.robotics.base.roboscriptbytecode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.workert.robotics.base.roboscriptbytecode.OpCode.*;
import static com.workert.robotics.base.roboscriptbytecode.Token.TokenType.*;

public final class Compiler {
	final RoboScript roboScriptInstance;
	Scanner scanner;
	Chunk chunk = new Chunk();

	List<Byte> currentCodeList = new ArrayList<>();
	List<Integer> currentLineList = new ArrayList<>();
	boolean inFunction = false;
	boolean emitPop = true;

	List<Integer> functions = new ArrayList<>();

	Token current;
	Token previous;

	private final Map<String, Byte> globalVariableLookup = new HashMap<>();
	final Map<String, Byte> nativeFunctionLookup = new HashMap<>();
	private final List<Local> locals = new ArrayList<>();
	private int scopeDepth = 0;

	Compiler(RoboScript roboScriptInstance) {
		this.roboScriptInstance = roboScriptInstance;
	}

	void compile(String source) {
		System.out.println("Started compiling.");
		long timeBefore = System.currentTimeMillis();
		try {
			this.scanner = new Scanner(source);
			this.advance();
			while (!this.checkAndConsumeIfMatches(EOF)) {
				this.declaration();
			}
			this.emitEnd();
			this.createFinalChunk();
		} catch (CompileError e) {
			this.synchronize();
		}
		System.out.println("Compiled in " + (System.currentTimeMillis() - timeBefore) + "ms.");
	}

	private void expression() {
		this.parsePrecedence(Precedence.ASSIGNMENT);
	}

	private void expressionStatement() {
		this.expression();
		this.consumeOrInsertSemicolon("Expected ';' or new line after expression.");
		if (this.emitPop)
			this.emitByte(OP_POP);
		this.emitPop = true;
	}


	private void declaration() {
		try {
			if (this.checkAndConsumeIfMatches(CLASS)) {
				this.classDeclaration();
			} else if (this.checkAndConsumeIfMatches(VAR)) {
				this.varDeclaration();
			} else if (this.checkAndConsumeIfMatches(FUNCTION)) {
				this.funcDeclaration();
			} else {
				this.statement();
			}
		} catch (CompileError e) {
			this.synchronize();
		}
	}

	private void fieldDeclaration() {
		try {
			if (this.checkAndConsumeIfMatches(VAR)) {
				this.varDeclarationForClass();
			} else if (this.checkAndConsumeIfMatches(FUNCTION)) {
				this.methodDeclaration();
			} else {
				throw new RuntimeError("Only declarations are allowed in main class body.");
			}
		} catch (CompileError e) {
			this.synchronize();
		}
	}

	private void varDeclarationForClass() {
		this.consumeOrThrow(IDENTIFIER, "Expected variable name.");
		String name = this.previous.lexeme;
		this.emitConstant(name);
		if (this.checkAndConsumeIfMatches(EQUAL)) {
			this.expression();
		} else {
			this.emitByte(OP_NULL);
		}

		this.consumeOrInsertSemicolon("Expected ';' or new line after variable declaration.");

		this.emitByte(OP_PUT);
	}

	private void methodDeclaration() {
		// the actual putting
		this.consumeOrThrow(IDENTIFIER, "Expected method name");
		String name = this.previous.lexeme;
		this.emitConstant(name);
		int constantIndex = this.emitConstant(null);
		this.emitByte(OP_PUT);

		// the function stuff
		List<Byte> previousCodeList = this.currentCodeList;
		this.currentCodeList = new ArrayList<>();
		List<Integer> previousLineList = this.currentLineList;
		this.currentLineList = new ArrayList<>();
		boolean wasInFunction = this.inFunction;
		this.inFunction = true;

		this.beginScope();
		this.consumeOrThrow(LEFT_PAREN, "Expected '(' after function name.");

		int argumentCount = 0;
		if (!this.isNextToken(RIGHT_PAREN)) {
			do {
				byte constant = this.parseVariable("Expected parameter name.");
				if (this.previous.lexeme.equals("this"))
					throw new RuntimeError("Methods must not contain parameter 'this', as it is added by default.");
				this.defineVariable(constant, this.previous.lexeme);
				argumentCount++;
			} while (this.checkAndConsumeIfMatches(COMMA));
		}
		this.declareVariable(new Token(IDENTIFIER, "this", this.previous.line));
		this.defineVariable((byte) 0, "this");
		this.consumeOrThrow(RIGHT_PAREN, "Expected ')' after function parameters.");
		this.consumeOrThrow(LEFT_BRACE, "Expected '{' before function body");
		this.block();
		this.endFunctionScope();
		this.emitBytes(OP_NULL, OP_RETURN);

		CompilerFunction function = new CompilerFunction(this.currentCodeList, this.currentLineList, argumentCount);

		this.currentCodeList = previousCodeList;
		this.currentLineList = previousLineList;
		this.inFunction = wasInFunction;

		this.chunk.setConstant(constantIndex, function);
		this.functions.add(constantIndex);
	}

	private void classDeclaration() {
		byte global = this.parseVariable("Expected class name");
		String name = this.previous.lexeme;
		Map<String, Object> fields = new HashMap<>();
		RoboScriptClass clazz = new RoboScriptClass(fields);
		this.emitConstant(clazz);
		this.defineVariable(global, name);
		this.emitConstant(clazz);
		this.consumeOrThrow(LEFT_BRACE, "Expected '{' after class name.");

		while (!this.isNextToken(RIGHT_BRACE) && !this.isNextToken(EOF)) {
			this.fieldDeclaration();
		}
		this.consumeOrThrow(RIGHT_BRACE, "Expected '}' after class body.");
	}

	private void varDeclaration() {
		byte global = this.parseVariable("Expected variable name.");
		String name = this.previous.lexeme;

		if (this.checkAndConsumeIfMatches(EQUAL)) {
			this.expression();
		} else {
			this.emitByte(OP_NULL);
		}
		this.consumeOrInsertSemicolon("Expected ';' or new line after variable declaration.");

		this.defineVariable(global, name);
	}

	private void funcDeclaration() {
		byte global = this.parseVariable("Expected function name.");
		String name = this.previous.lexeme;
		int constantIndex = this.emitConstant(null);
		this.defineVariable(global, name);

		List<Byte> previousCodeList = this.currentCodeList;
		this.currentCodeList = new ArrayList<>();
		List<Integer> previousLineList = this.currentLineList;
		this.currentLineList = new ArrayList<>();
		boolean wasInFunction = this.inFunction;
		this.inFunction = true;

		this.beginScope();
		this.consumeOrThrow(LEFT_PAREN, "Expected '(' after function name.");

		int argumentCount = 0;
		if (!this.isNextToken(RIGHT_PAREN)) {
			do {
				byte constant = this.parseVariable("Expected parameter name.");
				this.defineVariable(constant, this.previous.lexeme);
				argumentCount++;
			} while (this.checkAndConsumeIfMatches(COMMA));
		}

		this.consumeOrThrow(RIGHT_PAREN, "Expected ')' after function parameters.");

		this.consumeOrThrow(LEFT_BRACE, "Expected '{' before function body");
		this.block();
		this.endFunctionScope();
		this.emitBytes(OP_NULL, OP_RETURN);

		CompilerFunction function = new CompilerFunction(this.currentCodeList, this.currentLineList, argumentCount);

		this.currentCodeList = previousCodeList;
		this.currentLineList = previousLineList;
		this.inFunction = wasInFunction;

		this.chunk.setConstant(constantIndex, function);
		this.functions.add(constantIndex);
	}

	private void declareVariable() {
		this.declareVariable(this.previous);
	}

	private void declareVariable(Token name) {
		if (this.scopeDepth == 0) return;
		for (int i = this.locals.size() - 1; i >= 0; i--) {
			Local local = this.locals.get(i);
			if (local.depth != -1 && local.depth < this.scopeDepth) break;
			if (name.lexeme.equals(local.name.lexeme))
				throw this.error("A variable with the name '" + name.lexeme + "' already exists in the current scope");
		}
		this.addLocal(name);
	}

	private void defineVariable(byte global, String name) {
		if (this.scopeDepth > 0) {
			this.markInitialized();
			return;
		}
		if (this.globalVariableLookup.containsKey(name))
			throw this.error("Variable '" + name + "' already declared in the public scope.");
		this.globalVariableLookup.put(name, global);
		this.emitBytes(OP_DEFINE_GLOBAL, global);
	}

	void variable(boolean canAssign) {
		byte variable = this.resolveLocal(this.previous);
		if (variable != -1) { // Inside a Scope
			this.emitVariable(OP_GET_LOCAL, OP_SET_LOCAL, OP_INCREMENT_LOCAL, OP_DECREMENT_LOCAL, variable, canAssign);
		} else { // Outside a Scope -> global
			if (this.globalVariableLookup.containsKey(this.previous.lexeme)) {
				variable = this.globalVariableLookup.get(this.previous.lexeme);
				this.emitVariable(OP_GET_GLOBAL, OP_SET_GLOBAL, OP_INCREMENT_GLOBAL, OP_DECREMENT_GLOBAL, variable,
						canAssign);
			} else if (this.nativeFunctionLookup.containsKey(this.previous.lexeme)) {
				variable = this.nativeFunctionLookup.get(this.previous.lexeme);
				this.emitNativeFunction(this.previous, variable, canAssign);
			} else
				throw this.error("Variable '" + this.previous.lexeme + "' has not been declared.");
		}
	}


	void dot(boolean canAssign) {
		this.consumeOrThrow(IDENTIFIER, "Expected field name after '.'.");
		String name = this.previous.lexeme;
		this.emitConstant(name);
		this.emitGetVariable(OP_LIST_MAP_GET, canAssign);
	}

	void map(boolean canAssign) {
		Map<Object, Object> map = new HashMap<>();
		this.emitConstant(map);
		if (!this.isNextToken(RIGHT_BRACE)) {
			do {
				this.expression();
				this.consumeOrThrow(COLON, "Expected ':' after map key.");
				this.expression();
				this.emitByte(OP_PUT);
			} while (this.checkAndConsumeIfMatches(COMMA));
		}
		this.consumeOrThrow(RIGHT_BRACE, "Expected '}' after map expression.");
	}

	private void addLocal(Token name) {
		if (this.locals.size() >= 256)
			throw this.error("Too many local variables in function.");
		this.locals.add(new Local(name, -1));
	}

	private byte resolveLocal(Token name) {
		for (int i = (this.locals.size() - 1); i >= 0; i--) {
			Local local = this.locals.get(i);
			if (name.lexeme.equals(local.name.lexeme)) {
				if (local.depth == -1) {
					throw this.error("Cannot read local variable in its own initializer.");
				}
				return (byte) i;
			}
		}
		return -1;
	}

	private void statement() {
		if (this.checkAndConsumeIfMatches(IF)) {
			this.ifStatement();
		} else if (this.checkAndConsumeIfMatches(WHILE)) {
			this.whileStatement();
		} else if (this.checkAndConsumeIfMatches(FOR)) {
			this.forStatement();
		} else if (this.checkAndConsumeIfMatches(RETURN)) {
			this.returnStatement();
		} else {
			this.expressionStatement();
		}
	}

	private void block() {
		while (!this.isNextToken(RIGHT_BRACE) && !this.isNextToken(EOF)) {
			this.declaration();
		}
		this.consumeOrThrow(RIGHT_BRACE, "Expected '}' after block.");
	}

	private void statementOrBlock() {
		if (this.checkAndConsumeIfMatches(LEFT_BRACE)) {
			this.beginScope();
			this.block();
			this.endScope();
		} else {
			this.statement();
		}
	}


	private void ifStatement() {
		this.consumeOrThrow(LEFT_PAREN, "Expected '(' after 'if'.");
		this.expression();
		this.consumeOrThrow(RIGHT_PAREN, "Expected ')' after condition.");
		int thenJump = this.emitJump(OP_JUMP_IF_FALSE);
		this.emitByte(OP_POP);
		this.statementOrBlock();
		int elseJump = this.emitJump(OP_JUMP);
		this.patchJump(thenJump);
		this.emitByte(OP_POP);
		if (this.checkAndConsumeIfMatches(ELSE)) this.statementOrBlock();
		this.patchJump(elseJump);
	}

	private void whileStatement() {
		int loopStart = this.currentCodeList.size();
		this.consumeOrThrow(LEFT_PAREN, "Expected '(' after 'while'.");
		this.expression();
		this.consumeOrThrow(RIGHT_PAREN, "Expected ')' after condition.");
		int exitJump = this.emitJump(OP_JUMP_IF_FALSE);
		this.emitByte(OP_POP);
		this.statementOrBlock();
		this.emitLoop(loopStart);
		this.patchJump(exitJump);
		this.emitByte(OP_POP);
	}

	private void forStatement() {
		this.beginScope();
		this.consumeOrThrow(LEFT_PAREN, "Expected '(' after 'for'.");
		if (!this.checkAndConsumeIfMatches(SEMICOLON)) {
			if (this.checkAndConsumeIfMatches(VAR)) {
				this.varDeclaration();
			} else {
				this.expressionStatement();
			}
		}

		int loopStart = this.currentCodeList.size();
		int exitJump = -1;
		if (!this.checkAndConsumeIfMatches(SEMICOLON)) {
			this.expression();
			this.consumeOrThrow(SEMICOLON, "Expected ';' after loop condition.");
			exitJump = this.emitJump(OP_JUMP_IF_FALSE);
			this.emitByte(OP_POP);
		}
		if (!this.checkAndConsumeIfMatches(RIGHT_PAREN)) {
			int bodyJump = this.emitJump(OP_JUMP);
			int incrementStart = this.currentCodeList.size();
			this.expression();
			this.emitByte(OP_POP);
			this.consumeOrThrow(RIGHT_PAREN, "Expect ')' after for clauses");
			this.emitLoop(loopStart);
			loopStart = incrementStart;
			this.patchJump(bodyJump);
		}
		this.statementOrBlock();
		this.emitLoop(loopStart);
		if (exitJump != -1) {
			this.patchJump(exitJump);
			this.emitByte(OP_POP);
		}
		this.endScope();
	}

	private void returnStatement() {
		if (!this.inFunction) throw this.error("Can only return inside of functions.");
		if (this.checkAndConsumeIfMatches(SEMICOLON)) {
			this.emitBytes(OP_NULL, OP_RETURN);
			return;
		}
		this.expression();
		this.consumeOrInsertSemicolon("Expected ';' or new line after return expression.");
		this.emitByte(OP_RETURN);
	}

	private int emitJump(byte instruction) {
		this.emitByte(instruction);
		this.emitByte((byte) 0xFF);
		this.emitByte((byte) 0xFF);
		return this.currentCodeList.size() - 2;
	}

	private void emitLoop(int loopStart) {
		this.emitByte(OP_LOOP);
		int offset = this.currentCodeList.size() - loopStart + 2;
		if (offset > 65535) throw this.error("Loop body too large.");
		this.emitByte((byte) ((offset >> 8) & 0xff));
		this.emitByte((byte) (offset & 0xFF));
	}

	private void patchJump(int offset) {
		int jump = this.currentCodeList.size() - offset - 2;
		if (jump > 65535) {
			throw this.error("Too much code to jump over.");
		}
		this.currentCodeList.set(offset, (byte) ((jump >> 8) & 0xff));
		this.currentCodeList.set(offset + 1, (byte) (jump & 0xFF));
	}


	private void emitVariable(byte getOp, byte setOp, byte incrementOp, byte decrementOp, byte lookup, boolean canAssign) {
		if (!canAssign) {
			this.emitBytes(getOp, lookup);
			return;
		} else if (this.checkAndConsumeIfMatches(EQUAL)) {
			this.expression();
			this.emitBytes(setOp, lookup);
			return;
		} else if (this.checkAndConsumeIfMatches
				(PLUS_EQUAL, MINUS_EQUAL, STAR_EQUAL, SLASH_EQUAL, CARET_EQUAL, PERCENT_EQUAL)) {
			Token.TokenType previousType = this.previous.type;
			this.emitBytes(getOp, lookup);
			this.expression();
			byte emit = getAssignmentOperatorByte(previousType);
			this.emitBytes(emit, setOp, lookup);
			return;
		}
		if (this.checkAndConsumeIfMatches(PLUS_PLUS)) {
			this.emitBytes(incrementOp, lookup);
			return;
		} else if (this.checkAndConsumeIfMatches(MINUS_MINUS)) {
			this.emitBytes(decrementOp, lookup);
			return;
		}
		this.emitBytes(getOp, lookup);
	}

	private void emitGetVariable(byte getOpCode, boolean canAssign) {
		if (!canAssign) {
			this.emitBytes(getOpCode, (byte) 0);
			return;
		}

		if (this.checkAndConsumeIfMatches(EQUAL)) {
			this.expression();
			this.emitByte(OP_LIST_MAP_SET);
			return;
		}

		if (this.checkAndConsumeIfMatches
				(PLUS_EQUAL, MINUS_EQUAL, STAR_EQUAL, SLASH_EQUAL, CARET_EQUAL, PERCENT_EQUAL)) {
			Token.TokenType previousType = this.previous.type;
			this.emitBytes(getOpCode, (byte) 1);
			this.expression();
			byte emit = getAssignmentOperatorByte(previousType);
			this.emitBytes(emit, OP_LIST_MAP_SET);
			return;
		}

		if (this.checkAndConsumeIfMatches(PLUS_PLUS)) {
			this.emitBytes(OP_INCREMENT_LIST_MAP);
			return;
		} else if (this.checkAndConsumeIfMatches(MINUS_MINUS)) {
			this.emitBytes(OP_DECREMENT_LIST_MAP);
			return;
		}
		this.emitBytes(getOpCode, (byte) 0);
	}

	private static byte getAssignmentOperatorByte(Token.TokenType previousTokenType) {
		return switch (previousTokenType) {
			case PLUS_EQUAL -> OP_ADD;
			case MINUS_EQUAL -> OP_SUBTRACT;
			case STAR_EQUAL -> OP_MULTIPLY;
			case SLASH_EQUAL -> OP_DIVIDE;
			case CARET_EQUAL -> OP_POWER;
			case PERCENT_EQUAL -> OP_MODULO;
			default -> throw new IllegalArgumentException("Invalid previous Token Type.");
		};
	}

	private void emitNativeFunction(Token name, byte lookup, boolean canAssign) {
		if (canAssign && this.checkAndConsumeIfMatches(EQUAL)) {
			this.expression();
			this.globalVariableLookup.put(name.lexeme, (byte) this.globalVariableLookup.size());
			this.emitBytes(OP_DEFINE_GLOBAL, (byte) (this.globalVariableLookup.size() - 1));
			this.emitPop = false;
		} else {
			this.emitBytes(OP_GET_NATIVE, lookup);
		}
	}

	void grouping(boolean canAssign) {
		this.expression();
		this.consumeOrThrow(RIGHT_PAREN, "Expected ')' after expression.");
	}

	void list(boolean canAssign) {
		List<Object> list = new ArrayList<>();
		this.emitConstant(list);
		if (!this.isNextToken(RIGHT_BRACKET)) {
			do {
				this.expression();
				this.emitByte(OP_LIST_ADD);
			} while (this.checkAndConsumeIfMatches(COMMA));
		}
		this.consumeOrThrow(RIGHT_BRACKET, "Expected ']' after list expression.");
	}


	void number(boolean canAssign) {
		double value = Double.parseDouble(this.previous.lexeme);
		this.emitConstant(value);
	}

	void literal(boolean canAssign) {
		switch (this.previous.type) {
			case FALSE -> this.emitByte(OP_FALSE);
			case TRUE -> this.emitByte(OP_TRUE);
			case STRING_VALUE -> this.emitConstant(this.previous.lexeme);
			case NULL -> this.emitByte(OP_NULL);
			default -> throw new IllegalArgumentException("Invalid literal operator type");
		}
	}

	void unary(boolean canAssign) {
		Token.TokenType operatorType = this.previous.type;
		this.parsePrecedence(Precedence.UNARY);
		switch (operatorType) {
			case MINUS -> this.emitByte(OP_NEGATE);
			case BANG -> this.emitByte(OP_NOT);
			default -> throw new IllegalArgumentException("Invalid unary operator type");
		}
	}

	void binary(boolean canAssign) {
		Token.TokenType operatorType = this.previous.type;
		ParseRule rule = operatorType.getParseRule();
		this.parsePrecedence(rule.precedence + 1);

		switch (operatorType) {
			case PLUS -> this.emitByte(OP_ADD);
			case MINUS -> this.emitByte(OP_SUBTRACT);
			case STAR -> this.emitByte(OP_MULTIPLY);
			case SLASH -> this.emitByte(OP_DIVIDE);
			case PERCENT -> this.emitBytes(OP_MODULO);
			case CARET -> this.emitByte(OP_POWER);
			case GREATER -> this.emitByte(OP_GREATER);
			case GREATER_EQUAL -> this.emitByte(OP_GREATER_EQUAL);
			case LESS -> this.emitByte(OP_LESS);
			case LESS_EQUAL -> this.emitByte(OP_LESS_EQUAL);
			case EQUAL_EQUAL -> this.emitByte(OP_EQUAL);
			case BANG_EQUAL -> this.emitByte(OP_NOT_EQUAL);
			default -> throw new IllegalArgumentException("Invalid binary operator type");
		}
	}

	void call(boolean canAssign) {
		byte argumentCount = 0;

		if (!this.isNextToken(RIGHT_PAREN)) {
			do {
				this.expression();
				if (argumentCount >= 255) {
					throw this.error("Cannot have more than 255 arguments.");
				}
				argumentCount++;
			} while (this.checkAndConsumeIfMatches(COMMA));
		}
		this.consumeOrThrow(RIGHT_PAREN, "Expected ')' after arguments.");
		this.emitBytes(OP_CALL, argumentCount);
	}

	void index(boolean canAssign) {
		this.expression();
		this.consumeOrThrow(RIGHT_BRACKET, "Expected ']' after expression");
		this.emitGetVariable(OP_LIST_MAP_GET, canAssign);
	}

	void and(boolean canAssign) {
		int endJump = this.emitJump(OP_JUMP_IF_FALSE);
		this.emitByte(OP_POP);
		this.parsePrecedence(Precedence.AND);
		this.patchJump(endJump);
	}

	void or(boolean canAssign) {
		int elseJump = this.emitJump(OP_JUMP_IF_FALSE);
		int endJump = this.emitJump(OP_JUMP);

		this.patchJump(elseJump);
		this.emitByte(OP_POP);
		this.parsePrecedence(Precedence.OR);
		this.patchJump(endJump);
	}


	private void parsePrecedence(int precedence) {
		this.advance();
		ParseFunction prefixRule = this.previous.type.getParseRule().prefix;
		if (prefixRule == null) {
			throw this.error("Expected expression.");
		}
		boolean canAssign = precedence <= Precedence.ASSIGNMENT;
		prefixRule.apply(this, canAssign);
		while (precedence <= this.current.type.getParseRule().precedence) {
			this.advance();
			ParseFunction infixRule = this.previous.type.getParseRule().infix;
			infixRule.apply(this, canAssign);
		}

		if (canAssign && this.checkAndConsumeIfMatches(EQUAL)) {
			throw this.error("Invalid assignment target.");
		}
	}

	private byte parseVariable(String message) {
		this.consumeOrThrow(IDENTIFIER, message);
		this.declareVariable();
		if (this.scopeDepth > 0) return 0;
		return (byte) this.globalVariableLookup.size();
	}

	private void markInitialized() {
		if (this.scopeDepth == 0) return;
		this.locals.get(this.locals.size() - 1).depth = this.scopeDepth;
	}


	private void emitByte(byte b) {
		this.currentCodeList.add(b);
		this.currentLineList.add(this.previous.line);
	}

	private void emitBytes(byte... b) {
		for (byte currentByte : b) {
			this.emitByte(currentByte);
		}
	}

	private void emitEnd() {
		this.emitByte(OP_END);
	}

	private int emitConstant(Object value) {
		int constant = this.chunk.addConstant(value);
		if (constant > Short.MAX_VALUE) {
			throw this.error("Too many constants in one chunk.");
		}
		// emit constant as short
		this.emitBytes(OP_CONSTANT, (byte) ((constant >> 8) & 0xFF), (byte) (constant & 0xFF));
		return constant;
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

	private void consumeOrInsertSemicolon(String message) {
		if (this.current.type == SEMICOLON) {
			this.advance();
			return;
		} else if (this.previous.line < this.current.line) {
			return;
		} else if (this.isNextToken(RIGHT_BRACE)) {
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


	private CompileError error(String message) {
		return this.errorAt(this.previous, message);
	}

	private CompileError errorAtCurrent(String message) {
		return this.errorAt(this.current, message);
	}

	private CompileError errorAt(Token token, String message) {
		String finalMessage = "Error";
		if (token.type == EOF) {
			finalMessage += " at end";
		} else /*if (token.type != ERROR)*/ {
			finalMessage += " at " + token.lexeme;
		}
		finalMessage += ": '" + message + "'";
		this.roboScriptInstance.reportCompileError(token.line, finalMessage);
		return new CompileError();
	}

	private void synchronize() {
		while (this.current.type != EOF) {
			if (this.previous.type == SEMICOLON) return;
			switch (this.current.type) {
				case CLASS, FUNCTION, VAR, FOR, IF, WHILE, RETURN -> {
					return;
				}
			}
			this.advance();
		}
	}

	private void beginScope() {
		this.scopeDepth++;
	}

	private void endScope() {
		this.scopeDepth--;
		while (this.locals.size() > 0 && this.locals.get(this.locals.size() - 1).depth > this.scopeDepth) {
			this.emitByte(OP_POP);
			this.locals.remove(this.locals.size() - 1);
		}
	}

	private void endFunctionScope() {
		this.scopeDepth--;
		while (this.locals.size() > 0 && this.locals.get(this.locals.size() - 1).depth > this.scopeDepth) {
			this.locals.remove(this.locals.size() - 1);
		}
	}

	private void createFinalChunk() {
		this.chunk.setCode(this.currentCodeList);
		this.chunk.setLines(this.currentLineList);

		for (int i : this.functions) {
			CompilerFunction function = (CompilerFunction) this.chunk.getConstant(i);
			RoboScriptFunction runtimeFunction = new RoboScriptFunction(this.chunk.getCodeSize(),
					function.argumentCount);
			this.chunk.setConstant(i, runtimeFunction);
			this.chunk.addCode(function.code);
			this.chunk.addLines(function.lines);
		}
	}

	private static class CompilerFunction {
		List<Byte> code;
		List<Integer> lines;
		int argumentCount;

		CompilerFunction(List<Byte> code, List<Integer> lines, int argumentCount) {
			this.code = code;
			this.lines = lines;
			this.argumentCount = argumentCount;
		}
	}

	protected static class CompileError extends RuntimeException {
	}

	protected static class ParseRule {
		ParseFunction prefix;
		ParseFunction infix;
		byte precedence;


		ParseRule(ParseFunction prefix, ParseFunction infix, byte precedence) {
			this.prefix = prefix;
			this.infix = infix;
			this.precedence = precedence;
		}
	}

	private static class Local {
		Token name;
		int depth;

		Local(Token name, int depth) {
			this.name = name;
			this.depth = depth;
		}
	}

	protected interface Precedence {
		byte NONE = 0;
		byte ASSIGNMENT = 1; // =
		byte OR = 2; // or
		byte AND = 3; // and
		byte EQUALITY = 4; // == !=
		byte COMPARISON = 5; // < > <= >=
		byte TERM = 6; // + -
		byte FACTOR = 7; // * /
		byte UNARY = 8; // ! -
		byte POWER = 9; // ^
		byte CALL = 10; // . ()
		byte PRIMARY = 11;
	}


}
