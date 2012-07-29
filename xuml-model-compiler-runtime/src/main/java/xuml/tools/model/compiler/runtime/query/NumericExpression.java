package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public abstract class NumericExpression<T extends Entity<T>> {

	public NumericExpression<T> plus(NumericExpression<T> e) {
		return new BinaryNumericExpression<T>(this, BinaryNumericOperator.PLUS,
				e);
	}

	public NumericExpression<T> plus(Number e) {
		return new BinaryNumericExpression<T>(this, BinaryNumericOperator.PLUS,
				new NumericConstant<T>(e));
	}

	public NumericExpression<T> minus(NumericExpression<T> e) {
		return new BinaryNumericExpression<T>(this,
				BinaryNumericOperator.MINUS, e);
	}

	public NumericExpression<T> minus(Number e) {
		return new BinaryNumericExpression<T>(this,
				BinaryNumericOperator.MINUS, new NumericConstant<T>(e));
	}

	public NumericExpression<T> times(NumericExpression<T> e) {
		return new BinaryNumericExpression<T>(this,
				BinaryNumericOperator.TIMES, e);
	}

	public NumericExpression<T> times(Number e) {
		return new BinaryNumericExpression<T>(this,
				BinaryNumericOperator.TIMES, new NumericConstant<T>(e));
	}

	public NumericExpression<T> divide(NumericExpression<T> e) {
		return new BinaryNumericExpression<T>(this,
				BinaryNumericOperator.DIVIDE, e);
	}

	public NumericExpression<T> divide(Number e) {
		return new BinaryNumericExpression<T>(this,
				BinaryNumericOperator.DIVIDE, new NumericConstant<T>(e));
	}

	public BooleanExpression<T> eq(NumericExpression<T> e) {
		return new NumericComparison<T>(this, NumericComparisonOperator.EQ, e);
	}

	public BooleanExpression<T> eq(Number e) {
		return new NumericComparison<T>(this, NumericComparisonOperator.EQ,
				new NumericConstant<T>(e));
	}

	public BooleanExpression<T> neq(NumericExpression<T> e) {
		return new NumericComparison<T>(this, NumericComparisonOperator.NEQ, e);
	}

	public BooleanExpression<T> neq(Number e) {
		return new NumericComparison<T>(this, NumericComparisonOperator.NEQ,
				new NumericConstant<T>(e));
	}

	public BooleanExpression<T> gt(NumericExpression<T> e) {
		return new NumericComparison<T>(this, NumericComparisonOperator.GT, e);
	}

	public BooleanExpression<T> gt(Number e) {
		return new NumericComparison<T>(this, NumericComparisonOperator.GT,
				new NumericConstant<T>(e));
	}

	public BooleanExpression<T> gte(NumericExpression<T> e) {
		return new NumericComparison<T>(this, NumericComparisonOperator.GTE, e);
	}

	public BooleanExpression<T> gte(Number e) {
		return new NumericComparison<T>(this, NumericComparisonOperator.GTE,
				new NumericConstant<T>(e));
	}

	public BooleanExpression<T> lt(NumericExpression<T> e) {
		return new NumericComparison<T>(this, NumericComparisonOperator.LT, e);
	}

	public BooleanExpression<T> lt(Number e) {
		return new NumericComparison<T>(this, NumericComparisonOperator.LT,
				new NumericConstant<T>(e));
	}

	public BooleanExpression<T> lte(NumericExpression<T> e) {
		return new NumericComparison<T>(this, NumericComparisonOperator.LTE, e);
	}

	public BooleanExpression<T> lte(Number e) {
		return new NumericComparison<T>(this, NumericComparisonOperator.LTE,
				new NumericConstant<T>(e));
	}
}
