package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class BinaryBooleanExpression<T extends Entity<T>> extends
		BooleanExpression<T> {

	private final BooleanExpression<T> e1;
	private final BooleanExpression<T> e2;
	private final BinaryBooleanOperator op;

	public BinaryBooleanExpression(BooleanExpression<T> e1,
			BinaryBooleanOperator op, BooleanExpression<T> e2) {
		this.e1 = e1;
		this.op = op;
		this.e2 = e2;

	}

	public BooleanExpression<T> getExpression1() {
		return e1;
	}

	public BooleanExpression<T> getExpression2() {
		return e2;
	}

	public BinaryBooleanOperator getOperator() {
		return op;
	}
}
