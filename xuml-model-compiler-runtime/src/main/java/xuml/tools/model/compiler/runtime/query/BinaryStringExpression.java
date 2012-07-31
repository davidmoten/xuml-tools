package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class BinaryStringExpression<T extends Entity<T>> extends
		StringExpression<T> {

	private final StringExpression<T> e1;
	private final StringExpression<T> e2;
	private final BinaryStringOperator op;

	public BinaryStringExpression(StringExpression<T> e1,
			BinaryStringOperator op, StringExpression<T> e2) {
		this.e1 = e1;
		this.op = op;
		this.e2 = e2;
	}

	public StringExpression<T> getExpression1() {
		return e1;
	}

	public StringExpression<T> getExpression2() {
		return e2;
	}

	public BinaryStringOperator getOperator() {
		return op;
	}
}
