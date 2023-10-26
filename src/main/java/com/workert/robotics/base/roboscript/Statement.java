package com.workert.robotics.base.roboscript;
import java.util.List;

abstract class Statement {

	interface Visitor<R> {
		R visitExpressionStatement(Expression statement);

		R visitVarStatement(Var statement);

		R visitVarDeclarationStatement(VarDeclaration statement);

		R visitFunctionDeclaration(Function statement);

		R visitIfStatement(If statement);

		R visitWhileStatement(While statement);

		R visitLoopStatement(Loop statement);

		R visitForStatement(For statement);

		R visitReturnStatement(Return statement);

		R visitBreakStatement(Break breakStatement);
	}


	static class Class {

	}

	static class Expression extends Statement {
		Expression(com.workert.robotics.base.roboscript.Expression expression) {
			this.expression = expression;
		}

		final com.workert.robotics.base.roboscript.Expression expression;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitExpressionStatement(this);
		}
	}

	static class Var extends Statement {
		Var(VarDeclaration declaration, com.workert.robotics.base.roboscript.Expression initializer) {
			this.declaration = declaration;
			this.initializer = initializer;
		}

		final VarDeclaration declaration;
		final com.workert.robotics.base.roboscript.Expression initializer;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitVarStatement(this);
		}
	}

	static class VarDeclaration extends Statement {
		VarDeclaration(Token name, Token type, boolean nullSafe) {
			this.name = name;
			this.type = type;
			this.nullable = nullSafe;
		}

		final Token name;
		final Token type;

		final boolean nullable;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitVarDeclarationStatement(this);
		}
	}

	static class Function extends Statement {
		Function(Token name, List<Statement> body, List<VarDeclaration> params) {
			this.name = name;
			this.body = body;
			this.params = params;
		}

		final Token name;
		final List<Statement> body;
		final List<VarDeclaration> params;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitFunctionDeclaration(this);
		}
	}

	static class If extends Statement {
		If(com.workert.robotics.base.roboscript.Expression condition, List<Statement> thenBranch, List<Statement> elseBranch) {
			this.condition = condition;
			this.thenBranch = thenBranch;
			this.elseBranch = elseBranch;
		}

		final com.workert.robotics.base.roboscript.Expression condition;
		final List<Statement> thenBranch;
		final List<Statement> elseBranch;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitIfStatement(this);
		}
	}

	static class While extends Statement {
		While(com.workert.robotics.base.roboscript.Expression condition, List<Statement> body) {
			this.condition = condition;
			this.body = body;
		}

		final com.workert.robotics.base.roboscript.Expression condition;
		final List<Statement> body;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitWhileStatement(this);
		}
	}

	static class Loop extends Statement {
		Loop(List<Statement> body) {
			this.body = body;
		}

		final List<Statement> body;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitLoopStatement(this);
		}
	}

	static class For extends Statement {
		For(VarDeclaration declaration, com.workert.robotics.base.roboscript.Expression iteratable, List<Statement> body) {
			this.declaration = declaration;
			this.iterable = iteratable;
			this.body = body;
		}

		final VarDeclaration declaration;
		final com.workert.robotics.base.roboscript.Expression iterable;
		final List<Statement> body;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitForStatement(this);
		}
	}

	static class Return extends Statement {
		Return(Token keyword, com.workert.robotics.base.roboscript.Expression value) {
			this.keyword = keyword;
			this.value = value;
		}

		final Token keyword;
		final com.workert.robotics.base.roboscript.Expression value;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitReturnStatement(this);
		}
	}

	static class Break extends Statement {
		Break(Token keyword) {
			this.keyword = keyword;
		}

		final Token keyword;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBreakStatement(this);
		}
	}

	abstract <R> R accept(Visitor<R> visitor);
}
