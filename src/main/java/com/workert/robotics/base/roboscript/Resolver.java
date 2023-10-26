package com.workert.robotics.base.roboscript;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Resolver implements Statement.Visitor<Void> {
	private final Map<String, Statement.VarDeclaration> globalVariables = new HashMap<>();
	private final List<Local> locals = new ArrayList<>();
	private int scopeDepth = 0;


	void resolve(List<Statement> statements) {
		for (Statement statement : statements) {
			this.resolve(statement);
		}
	}

	void resolveBlock(List<Statement> statements) {
		this.beginScope();
		this.resolve(statements);
		this.endScope();
	}

	private void resolve(Statement statement) {
		statement.accept(this);
	}

	private void resolve(Expression expression) {
		// expression.accept(this);
	}

	@Override
	public Void visitExpressionStatement(Statement.Expression statement) {
		return null;
	}

	@Override
	public Void visitVarStatement(Statement.Var statement) {
		this.resolve(statement.declaration);
		this.declareVariable(statement.declaration);
		if (!statement.declaration.nullable) {
			switch (statement.declaration.type.type) {
				case RANGE, IDENTIFIER, ANY -> {
					if (statement.initializer == null) {
						// throw an error here about these types needing to be initialized if not nullable
					}
				}
			}
		}
		if (statement.initializer != null) {
			this.resolve(statement.initializer);
			if (!this.areCompatibleTypes(statement.declaration, statement.initializer)) {
				// throw error about incompatibility
			}
		}
		this.defineVariable(statement.declaration);
		return null;
	}

	@Override
	public Void visitVarDeclarationStatement(Statement.VarDeclaration statement) {
		if (statement.nullable && statement.type != null) {
			if (statement.type.type == Token.TokenType.BOOL || statement.type.type == Token.TokenType.NUMBER) {
				// throw error saying numbers and bools cannot be nullable
			}
		}

		return null;
	}

	@Override
	public Void visitFunctionDeclaration(Statement.Function statement) {
		return null;
	}

	@Override
	public Void visitIfStatement(Statement.If statement) {
		this.resolve(statement.condition);
		this.resolveBlock(statement.thenBranch);
		if (statement.elseBranch != null) this.resolveBlock(statement.elseBranch);
		return null;
	}

	@Override
	public Void visitWhileStatement(Statement.While statement) {
		this.resolve(statement.condition);
		this.resolveBlock(statement.body);
		return null;
	}

	@Override
	public Void visitLoopStatement(Statement.Loop statement) {
		this.resolveBlock(statement.body);
		return null;
	}

	@Override
	public Void visitForStatement(Statement.For statement) {
		this.resolve(statement.iterable);
		this.beginScope();
		this.declareVariable(statement.declaration);
		this.defineVariable(statement.declaration);
		this.resolve(statement.body);
		this.endScope();
		return null;
	}

	@Override
	public Void visitReturnStatement(Statement.Return statement) {
		return null;
	}

	@Override
	public Void visitBreakStatement(Statement.Break breakStatement) {
		return null;
	}


	private void declareVariable(Statement.VarDeclaration declaration) {
		if (!this.isGlobal()) return;
		for (int i = this.locals.size() - 1; i >= 0; i--) {
			Local local = this.locals.get(i);
			if (local.depth != -1 && local.depth < this.scopeDepth) break;
			if (declaration.name.lexeme.equals(local.local.name.lexeme)) {
				// throw an error because you cant have the same whatever
			}
		}
		this.addLocal(declaration);
	}

	private void defineVariable(Statement.VarDeclaration declaration) {
		if (!this.isGlobal()) {
			this.markInitialized();
			return;
		}
		if (this.globalVariables.containsKey(declaration.name.lexeme)) {
			// throw error because you cant have the same thing
		}
		this.globalVariables.put(declaration.name.lexeme, declaration);
	}

	private void addLocal(Statement.VarDeclaration declaration) {
		if (this.locals.size() >= 127) {
			// throw error
		}
		this.locals.add(new Local(declaration, -1));
	}

	private void markInitialized() {
		if (this.scopeDepth == 0) return;
		this.locals.get(this.locals.size() - 1).depth = this.scopeDepth;
	}

	private Statement.VarDeclaration findVariable(String name) {
		for (int i = (this.locals.size() - 1); i >= 0; i--) {
			Local local = this.locals.get(i);
			if (local.local.name.lexeme.equals(name)) {
				return local.local;
			}
		}
		if (this.globalVariables.containsKey(name)) {
			return this.globalVariables.get(name);
		}
		return null;
	}

	private boolean areCompatibleTypes(Statement.VarDeclaration declaration, Expression expression) {
		if (declaration.type == null) return true;


		if (!declaration.nullable) {
			if (expression instanceof Expression.NullLiteral) return false;

			if (expression instanceof Expression.Variable v) {
				Statement.VarDeclaration variable = this.findVariable(v.name.lexeme);
				// variable is nullable but the variable being assigned is not
				if (variable != null && variable.nullable) return false;
			}

		}

		if (declaration.type.type != Token.TokenType.IDENTIFIER) {
			return switch (declaration.type.type) {
				case STRING -> {
					if (expression instanceof Expression.StringLiteral) yield true;
					if (expression instanceof Expression.Variable v) {
						Statement.VarDeclaration variable = this.findVariable(v.name.lexeme);
						if (variable != null && variable.type != null && variable.type.type == Token.TokenType.STRING) {
							yield true;
						}
					}
					yield false;
				}
				case LIST -> {
					if (expression instanceof Expression.ListLiteral) yield true;
					if (expression instanceof Expression.Variable v) {
						Statement.VarDeclaration variable = this.findVariable(v.name.lexeme);
						if (variable != null && variable.type != null && variable.type.type == Token.TokenType.LIST) {
							yield true;
						}
					}
					yield false;
				}
				case RANGE -> {
					if (expression instanceof Expression.RangeLiteral) yield true;
					if (expression instanceof Expression.Variable v) {
						Statement.VarDeclaration variable = this.findVariable(v.name.lexeme);
						if (variable != null && variable.type != null && variable.type.type == Token.TokenType.RANGE) {
							yield true;
						}
					}
					yield false;
				}
				case NUMBER -> {
					if (expression instanceof Expression.DoubleLiteral) yield true;
					if (expression instanceof Expression.Variable v) {
						Statement.VarDeclaration variable = this.findVariable(v.name.lexeme);
						if (variable != null && variable.type != null && variable.type.type == Token.TokenType.NUMBER) {
							if (declaration.nullable) {
								throw new IllegalArgumentException("Number var declaration should never be nullable");
							}
							yield true;
						}
					}
					yield false;
				}
				case BOOL -> {
					if (expression instanceof Expression.BoolLiteral) yield true;
					if (expression instanceof Expression.Variable v) {
						Statement.VarDeclaration variable = this.findVariable(v.name.lexeme);
						if (variable != null && variable.type != null && variable.type.type == Token.TokenType.BOOL) {
							if (declaration.nullable) {
								throw new IllegalArgumentException("Bool var declaration should never be nullable");
							}
							yield true;
						}
					}
					yield false;
				}

				default -> false;
			};
		}

		return false;
	}

	private void beginScope() {
		this.scopeDepth++;
	}

	private void endScope() {
		this.scopeDepth--;
		while (!this.locals.isEmpty() && this.locals.get(this.locals.size() - 1).depth > this.scopeDepth) {
			this.locals.remove(this.locals.size() - 1);
		}
	}

	private boolean isGlobal() {
		return this.scopeDepth == 0;
	}


	private static class Local {
		Statement.VarDeclaration local;
		int depth;

		Local(Statement.VarDeclaration local, int depth) {
			this.local = local;
			this.depth = depth;
		}
	}


}
