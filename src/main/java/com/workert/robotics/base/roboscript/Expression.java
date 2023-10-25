package com.workert.robotics.base.roboscript;
import java.util.List;

abstract class Expression {
	static class Assign extends Expression {
		Assign(Expression.Variable name, Expression value) {
			this.name = name;
			this.value = value;
		}

		final Expression.Variable name;
		final Expression value;
	}

	static class Grouping extends Expression {
		Grouping(Expression expression) {
			this.expression = expression;
		}

		final Expression expression;
	}

	static class Binary extends Expression {

		Binary(Expression left, Token operator, Expression right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		final Expression left;
		final Expression right;
		final Token operator;
	}

	static class Logical extends Expression {

		Logical(Expression left, Token operator, Expression right) {
			this.left = left;
			this.right = right;
			this.operator = operator;
		}

		final Expression left;
		final Expression right;
		final Token operator;
	}

	static class Unary extends Expression {
		Unary(Token operator, Expression right) {
			this.operator = operator;
			this.right = right;
		}

		final Token operator;
		final Expression right;
	}

	static class Call extends Expression {
		Call(Expression callee, Token paren, List<Expression> arguments) {
			this.callee = callee;
			this.paren = paren;
			this.arguments = arguments;
		}

		final Expression callee;
		final Token paren;
		final List<Expression> arguments;
	}

	static class ListGet extends Expression {

	}

	static class ListSet extends Expression {

	}

	static class ClassGet extends Expression {

	}

	static class ClassSet extends Expression {

	}

	static class Variable extends Expression {
		Variable(Token name) {
			this.name = name;
		}

		final Token name;
	}

	static class DoubleLiteral extends Expression {
		DoubleLiteral(double value) {
			this.value = value;
		}

		final double value;
	}

	static class StringLiteral extends Expression {
		StringLiteral(String value) {
			this.value = value;
		}

		final String value;
	}

	static class BoolLiteral extends Expression {
		BoolLiteral(boolean value) {
			this.value = value;
		}

		final boolean value;
	}

	static class ListLiteral extends Expression {
		ListLiteral(List<Expression> elements) {
			this.elements = elements;
		}

		final List<Expression> elements;
	}

	static class MapLiteral extends Expression {
		MapLiteral(List<Expression> keys, List<Expression> values) {
			this.keys = keys;
			this.values = values;
		}

		final List<Expression> keys;
		final List<Expression> values;
	}

	static class NullLiteral extends Expression {
	}
}
