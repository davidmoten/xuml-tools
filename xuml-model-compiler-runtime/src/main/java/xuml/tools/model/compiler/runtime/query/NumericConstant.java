package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class NumericConstant<T extends Entity<T>> extends NumericExpression<T> {

	private final Number value;

	public NumericConstant(Number value) {
		this.value = value;
	}

	public Number getValue() {
		return value;
	}

}
