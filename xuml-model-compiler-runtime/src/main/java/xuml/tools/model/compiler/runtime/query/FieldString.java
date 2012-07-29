package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class FieldString<T extends Entity<T>> extends
		Field<T, ExpressionTypeString> {

	public FieldString(String fieldName) {
		super(fieldName, ExpressionTypeString.class);
	}

	public BooleanExpression<T> eq(StringExpression<T> e) {
		return new StringComparison<T>(e, StringComparisonOperator.EQ, e);
	}

	public BooleanExpression<T> neq(StringExpression<T> e) {
		return new StringComparison<T>(e, StringComparisonOperator.NEQ, e);
	}

	public BooleanExpression<T> like(StringExpression<T> e) {
		return new StringComparison<T>(e, StringComparisonOperator.LIKE, e);
	}
}
