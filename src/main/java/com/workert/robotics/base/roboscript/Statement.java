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
		Var(VarDeclaration declaration, com.workert.robotics.base.roboscript.Expression initializer) {
			this.declaration = declaration;
			this.initializer = initializer;
		}

		final VarDeclaration declaration;
		final com.workert.robotics.base.roboscript.Expression initializer;
	}

	static class VarDeclaration extends Statement {
		VarDeclaration(Token name, Token type, boolean nullSafe) {
			this.name = name;
			this.type = type;
			this.nullSafe = nullSafe;
		}

		final Token name;
		final Token type;

		final boolean nullSafe;
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
		If(com.workert.robotics.base.roboscript.Expression condition, List<Statement> thenBranch, List<Statement> elseBranch) {
			this.condition = condition;
			this.thenBranch = thenBranch;
			this.elseBranch = elseBranch;
		}

		final com.workert.robotics.base.roboscript.Expression condition;
		final List<Statement> thenBranch;
		final List<Statement> elseBranch;
	}

	static class While extends Statement {
		While(com.workert.robotics.base.roboscript.Expression condition, List<Statement> body) {
			this.condition = condition;
			this.body = body;
		}

		final com.workert.robotics.base.roboscript.Expression condition;
		final List<Statement> body;
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
	}

	static class Return extends Statement {
		Return(Token keyword, com.workert.robotics.base.roboscript.Expression value) {
			this.keyword = keyword;
			this.value = value;
		}

		final Token keyword;
		final com.workert.robotics.base.roboscript.Expression value;
	}

	static class Break extends Statement {
		Break(Token keyword) {
			this.keyword = keyword;
		}

		final Token keyword;
	}
}
