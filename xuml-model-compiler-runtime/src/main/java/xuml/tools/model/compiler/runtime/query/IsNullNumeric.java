package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class IsNullNumeric<T extends Entity<T>> extends NumericExpression<T> {

	private final NumericExpression<T> e;

	public IsNullNumeric(NumericExpression<T> e) {
		this.e = e;
	}

	public NumericExpression<T> getExpression() {
		return e;
	}
}
