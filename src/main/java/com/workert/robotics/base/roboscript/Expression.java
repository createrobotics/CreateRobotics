package com.workert.robotics.base.roboscript;
import java.util.List;

abstract class Expression {
	interface Visitor<R> {
		R visitAssignExpression(Assign expression);

		R visitGroupingExpression(Grouping expression);

		R visitBinaryExpression(Binary expression);

		R visitLogicalExpression(Logical expression);

		R visitUnaryExpression(Unary expression);

		R visitCallExpression(Call expression);

		R visitListGetExpression(ListGet expression);

		R visitListSetExpression(ListSet expression);

		R visitClassGetExpression(ClassGet expression);

		R visitClassSetExpression(ClassSet expression);

		R visitVariableExpression(Variable expression);

		R visitDoubleLiteralExpression(DoubleLiteral expression);

		R visitStringLiteralExpression(StringLiteral expression);

		R visitBoolLiteralExpression(BoolLiteral expression);

		R visitListLiteralExpression(ListLiteral expression);

		R visitRangeLiteralExpression(RangeLiteral expression);

		R visitMapLiteralExpression(MapLiteral expression);

		R visitNullLiteralExpression(NullLiteral expression);
	}

	static class Assign extends Expression {
		Assign(Expression.Variable name, Expression value) {
			this.name = name;
			this.value = value;
		}

		final Expression.Variable name;
		final Expression value;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitAssignExpression(this);
		}
	}

	static class Grouping extends Expression {
		Grouping(Expression expression) {
			this.expression = expression;
		}

		final Expression expression;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitGroupingExpression(this);
		}
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

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBinaryExpression(this);
		}
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

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitLogicalExpression(this);
		}
	}

	static class Unary extends Expression {
		Unary(Token operator, Expression right) {
			this.operator = operator;
			this.right = right;
		}

		final Token operator;
		final Expression right;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitUnaryExpression(this);
		}
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

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitCallExpression(this);
		}
	}

	static class ListGet extends Expression {

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitListGetExpression(this);
		}
	}

	static class ListSet extends Expression {

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitListSetExpression(this);
		}
	}

	static class ClassGet extends Expression {

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitClassGetExpression(this);
		}
	}

	static class ClassSet extends Expression {

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitClassSetExpression(this);
		}
	}

	static class Variable extends Expression {
		Variable(Token name) {
			this.name = name;
		}

		final Token name;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitVariableExpression(this);
		}
	}

	static class DoubleLiteral extends Expression {
		DoubleLiteral(double value) {
			this.value = value;
		}

		final double value;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitDoubleLiteralExpression(this);
		}
	}

	static class StringLiteral extends Expression {
		StringLiteral(String value) {
			this.value = value;
		}

		final String value;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitStringLiteralExpression(this);
		}
	}

	static class BoolLiteral extends Expression {
		BoolLiteral(boolean value) {
			this.value = value;
		}

		final boolean value;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitBoolLiteralExpression(this);
		}
	}

	static class ListLiteral extends Expression {
		ListLiteral(List<Expression> elements) {
			this.elements = elements;
		}

		final List<Expression> elements;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitListLiteralExpression(this);
		}
	}

	static class RangeLiteral extends Expression {
		RangeLiteral(Expression startValue, Expression upperRange) {
			this.startValue = startValue;
			this.upperRange = upperRange;
		}

		final Expression startValue;
		final Expression upperRange;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitRangeLiteralExpression(this);
		}
	}


	static class MapLiteral extends Expression {
		MapLiteral(List<Expression> keys, List<Expression> values) {
			this.keys = keys;
			this.values = values;
		}

		final List<Expression> keys;
		final List<Expression> values;

		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitMapLiteralExpression(this);
		}
	}

	static class NullLiteral extends Expression {
		@Override
		<R> R accept(Visitor<R> visitor) {
			return visitor.visitNullLiteralExpression(this);
		}
	}

	abstract <R> R accept(Visitor<R> visitor);
}
