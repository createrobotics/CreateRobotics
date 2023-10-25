package com.workert.robotics.base.roboscript;
import java.util.List;

abstract class Statement {


	static class Class {

	}

	static class Expression extends Statement {
		Expression(com.workert.robotics.base.roboscript.Expression expression) {
			this.expression = expression;
		}

		final com.workert.robotics.base.roboscript.Expression expression;
	}

	static class Var extends Statement {
		Var(VarDeclaration declaration, Expression initializer) {
			this.declaration = declaration;
			this.initializer = initializer;
		}

		final VarDeclaration declaration;
		final Expression initializer;
	}

	static class VarDeclaration extends Statement {
		VarDeclaration(Token name, Token type) {
			this.name = name;
			this.type = type;
		}

		final Token name;
		final Token type;
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
	}

	static class If extends Statement {
		If(Expression condition, Statement thenBranch, Statement elseBranch) {
			this.condition = condition;
			this.thenBranch = thenBranch;
			this.elseBranch = elseBranch;
		}

		final Expression condition;
		final Statement thenBranch;
		final Statement elseBranch;
	}

	static class While extends Statement {
		While(Expression condition, Statement body) {
			this.condition = condition;
			this.body = body;
		}

		final Expression condition;
		final Statement body;
	}

	static class For extends Statement {
		For(Var declaration, Expression iteratable) {
			this.declaration = declaration;
			this.iterable = iteratable;
		}

		final Var declaration;
		final Expression iterable;
	}

	static class Return extends Statement {
		Return(Token keyword, Token value) {
			this.keyword = keyword;
			this.value = value;
		}

		final Token keyword;
		final Token value;
	}

	static class Break extends Statement {
		Break(Token keyword) {
			this.keyword = keyword;
		}

		final Token keyword;
	}
}
