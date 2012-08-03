package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;
import akka.util.Duration;

public class BinaryDateExpression<T extends Entity<T>> extends
		DateExpression<T> {

	private final DateExpression<T> e1;
	private final DateExpression<T> e2;
	private final BinaryDateOperator op;

	public BinaryDateExpression(DateExpression<T> e1, BinaryDateOperator op,
			DateExpression<T> e2) {
		this.e1 = e1;
		this.op = op;
		this.e2 = e2;
	}

	public BinaryDateExpression(DateExpression<T> e1, BinaryDateOperator op,
			Duration duration) {
		this(e1, op, new DateConstant<T>(duration));
	}

	public DateExpression<T> getExpression1() {
		return e1;
	}

	public DateExpression<T> getExpression2() {
		return e2;
	}

	public BinaryDateOperator getOperator() {
		return op;
	}

}
