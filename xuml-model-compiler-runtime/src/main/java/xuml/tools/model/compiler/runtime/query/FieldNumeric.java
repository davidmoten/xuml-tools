package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class FieldNumeric<T extends Entity<T>> extends
		Field<T, ExpressionTypeNumeric> {

	public FieldNumeric(String fieldName) {
		super(fieldName, ExpressionTypeNumeric.class);
	}

	public BooleanExpression<T> eq(NumericExpression<T> e) {
		return new NumericComparison<T>(e, NumericComparisonOperator.EQ, e);
	}

	public BooleanExpression<T> neq(NumericExpression<T> e) {
		return new NumericComparison<T>(e, NumericComparisonOperator.EQ, e);
	}

	public BooleanExpression<T> gt(NumericExpression<T> e) {
		return new NumericComparison<T>(e, NumericComparisonOperator.GT, e);
	}

	public BooleanExpression<T> lt(NumericExpression<T> e) {
		return new NumericComparison<T>(e, NumericComparisonOperator.LT, e);
	}
}