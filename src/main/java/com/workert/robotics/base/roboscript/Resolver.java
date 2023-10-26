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

	private void resolve(Statement statement) {
		statement.accept(this);
	}

	@Override
	public Void visitExpressionStatement(Statement.Expression statement) {
		return null;
	}

	@Override
	public Void visitVarStatement(Statement.Var statement) {
		if (this.isGlobal()) {
			this.globalVariables.put(statement.declaration.name.lexeme, statement.declaration);
		}
		return null;
	}

	@Override
	public Void visitVarDeclarationStatement(Statement.VarDeclaration statement) {
		return null;
	}

	@Override
	public Void visitFunctionDeclaration(Statement.Function statement) {
		return null;
	}

	@Override
	public Void visitIfStatement(Statement.If statement) {
		return null;
	}

	@Override
	public Void visitWhileStatement(Statement.While statement) {
		return null;
	}

	@Override
	public Void visitLoopStatement(Statement.Loop statement) {
		return null;
	}

	@Override
	public Void visitForStatement(Statement.For statement) {
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
				local.local = declaration;
				local.depth = -1;
				return;
			}
		}
		this.addLocal(declaration);
	}

	private void defineVariable(Statement.VarDeclaration declaration) {
		if (!this.isGlobal()) {
			this.markInitialized();
			return;
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
		if (!declaration.nullable) {
			if (expression instanceof Expression.NullLiteral) return false;

			if (expression instanceof Expression.Variable v) {
				Statement.VarDeclaration variable = this.findVariable(v.name.lexeme);
				// variable is nullable but the variable being assigned is not
				if (variable != null && variable.nullable) return false;
			}

		}

		if (declaration.type != null && declaration.type.type != Token.TokenType.IDENTIFIER) {
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
