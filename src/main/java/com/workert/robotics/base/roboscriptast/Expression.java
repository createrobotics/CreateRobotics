package com.workert.robotics.base.roboscriptast;

import java.util.List;

abstract class Expression {
	interface Visitor<R> {
		R visitAssignExpr(Assign expr);

		R visitInstanceExpr(Instance expr);

		R visitBinaryExpr(Binary expr);

		R visitCallExpr(Call expr);

		R visitGetExpr(Get expr);

		R visitSetExpr(Set expr);

		R visitSuperExpr(Super expr);

		R visitThisExpr(This expr);

		R visitGroupingExpr(Grouping expr);

		R visitLiteralExpr(Literal expr);

		R visitLogicalExpr(Logical expr);

		R visitUnaryExpr(Unary expr);

		R visitVariableExpr(Variable expr);

		R visitArrayExpr(Array expr);

		R visitIndexGetExpr(IndexGet expr);

		R visitIndexSetExpr(IndexSet expr);
	}


	static class Assign extends Expression {
		Assign(Token name, Expression value) {
			this.name = name;
			this.value = value;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitAssignExpr(this);
		}

		final Token name;
		final Expression value;
	}


	static class Binary extends Expression {
		Binary(Expression left, Token operator, Expression right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBinaryExpr(this);
		}

		final Expression left;
		final Token operator;
		final Expression right;
	}

	static class Instance extends Expression {
		Instance(Expression left, Token right) {
			this.left = left;
			this.right = right;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitInstanceExpr(this);
		}

		final Expression left;
		final Token right;
	}


	static class Call extends Expression {
		Call(Expression callee, Token paren, List<Expression> arguments) {
			this.callee = callee;
			this.paren = paren;
			this.arguments = arguments;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitCallExpr(this);
		}

		final Expression callee;
		final Token paren;
		final List<Expression> arguments;
	}

	static class IndexGet extends Expression {
		IndexGet(Expression array, Token bracket, Expression index) {
			this.array = array;
			this.bracket = bracket;
			this.index = index;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitIndexGetExpr(this);
		}

		final Expression array;
		final Token bracket;
		final Expression index;
	}

	static class IndexSet extends Expression {
		IndexSet(Expression array, Token bracket, Expression index, Expression value) {
			this.array = array;
			this.bracket = bracket;
			this.index = index;
			this.value = value;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitIndexSetExpr(this);
		}

		final Expression array;
		final Token bracket;
		final Expression index;
		final Expression value;
	}

	static class Array extends Expression {
		Array(Token closeBracket, List<Expression> elements) {
			this.closeBracket = closeBracket;
			this.elements = elements;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitArrayExpr(this);
		}

		final Token closeBracket;
		final List<Expression> elements;
	}


	static class Get extends Expression {
		Get(Expression object, Token name) {
			this.object = object;
			this.name = name;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitGetExpr(this);
		}

		final Expression object;
		final Token name;
	}


	static class Set extends Expression {
		Set(Expression object, Token name, Expression value) {
			this.object = object;
			this.name = name;
			this.value = value;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitSetExpr(this);
		}

		final Expression object;
		final Token name;
		final Expression value;
	}


	static class Super extends Expression {
		Super(Token keyword, Token method, List<Expression> arguments) {
			this.keyword = keyword;
			this.method = method;
			this.arguments = arguments;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitSuperExpr(this);
		}

		final Token keyword;
		final Token method;
		final List<Expression> arguments;
	}


	static class This extends Expression {
		This(Token keyword) {
			this.keyword = keyword;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitThisExpr(this);
		}

		final Token keyword;
	}


	static class Grouping extends Expression {
		Grouping(Expression expression) {
			this.expression = expression;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitGroupingExpr(this);
		}

		final Expression expression;
	}


	static class Literal extends Expression {
		Literal(Object value) {
			this.value = value;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitLiteralExpr(this);
		}

		final Object value;
	}


	static class Logical extends Expression {
		Logical(Expression left, Token operator, Expression right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitLogicalExpr(this);
		}

		final Expression left;
		final Token operator;
		final Expression right;
	}


	static class Unary extends Expression {
		Unary(Token operator, Expression right) {
			this.operator = operator;
			this.right = right;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitUnaryExpr(this);
		}

		final Token operator;
		final Expression right;
	}


	static class Variable extends Expression {
		Variable(Token name) {
			this.name = name;
		}

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitVariableExpr(this);
		}

		final Token name;
	}


	abstract <R> R accept(Visitor<R> visitor);
}