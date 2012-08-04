package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class IsNullDate<T extends Entity<T>> extends DateExpression<T> {

	private final DateExpression<T> e;

	public IsNullDate(DateExpression<T> e) {
		this.e = e;
	}

	public DateExpression<T> getExpression() {
		return e;
	}
}
