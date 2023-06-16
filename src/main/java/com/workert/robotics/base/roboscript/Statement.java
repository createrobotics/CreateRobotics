package com.workert.robotics.base.roboscript;

import java.util.List;

public abstract class Statement {
	interface Visitor<R> {
		R visitBlockStmt(Block stmt);

		R visitClassStmt(Class stmt);

		R visitExpressionStmt(Expression stmt);

		R visitFunctionStmt(Function stmt);

		R visitIfStmt(If stmt);

		R visitReturnStmt(Return stmt);

		R visitBreakStmt(Break stmt);

		R visitVarStmt(Var stmt);

		R visitWhileStmt(While stmt);

		R visitForeachStmt(Foreach stmt);
	}


	static class Block extends Statement {
		Block(List<Statement> statements) {
			this.statements = statements;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBlockStmt(this);
		}

		final List<Statement> statements;
	}


	static class Class extends Statement {
		Class(Token name, com.workert.robotics.base.roboscript.Expression.Variable superclass, List<Statement> body, Statement.Function initializer) {
			this.name = name;
			this.superclass = superclass;
			this.body = body;
			this.initializer = initializer;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitClassStmt(this);
		}

		final Token name;
		final com.workert.robotics.base.roboscript.Expression.Variable superclass;
		final List<Statement> body;
		final Statement.Function initializer;
	}


	static class Expression extends Statement {
		Expression(com.workert.robotics.base.roboscript.Expression expression) {
			this.expression = expression;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitExpressionStmt(this);
		}

		final com.workert.robotics.base.roboscript.Expression expression;
	}


	static class Function extends Statement {
		Function(Token name, List<Token> params, List<Statement> body) {
			this.name = name;
			this.params = params;
			this.body = body;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitFunctionStmt(this);
		}

		final Token name;
		final List<Token> params;
		final List<Statement> body;
	}


	static class If extends Statement {
		If(com.workert.robotics.base.roboscript.Expression condition, Statement thenBranch, Statement elseBranch) {
			this.condition = condition;
			this.thenBranch = thenBranch;
			this.elseBranch = elseBranch;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitIfStmt(this);
		}

		final com.workert.robotics.base.roboscript.Expression condition;
		final Statement thenBranch;
		final Statement elseBranch;
	}


	static class Return extends Statement {
		Return(Token keyword, com.workert.robotics.base.roboscript.Expression value) {
			this.keyword = keyword;
			this.value = value;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitReturnStmt(this);
		}

		final Token keyword;
		final com.workert.robotics.base.roboscript.Expression value;
	}


	static class Break extends Statement {
		Break(Token keyword) {
			this.keyword = keyword;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBreakStmt(this);
		}

		final Token keyword;
	}


	static class Var extends Statement {
		Var(Token name, com.workert.robotics.base.roboscript.Expression initializer, boolean staticc) {
			this.name = name;
			this.initializer = initializer;
			this.staticc = staticc;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitVarStmt(this);
		}

		final Token name;
		final com.workert.robotics.base.roboscript.Expression initializer;
		final boolean staticc;
	}


	static class While extends Statement {
		While(com.workert.robotics.base.roboscript.Expression condition, Statement body) {
			this.condition = condition;
			this.body = body;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitWhileStmt(this);
		}

		final com.workert.robotics.base.roboscript.Expression condition;
		final Statement body;
	}

	static class Foreach extends Statement {
		Foreach(Token variable, Token colon, com.workert.robotics.base.roboscript.Expression right, Statement body) {
			this.variable = variable;
			this.colon = colon;
			this.right = right;
			this.body = body;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitForeachStmt(this);
		}

		final Token variable;
		final Token colon;
		final com.workert.robotics.base.roboscript.Expression right;
		final Statement body;
	}


	abstract <R> R accept(Visitor<R> visitor);
}