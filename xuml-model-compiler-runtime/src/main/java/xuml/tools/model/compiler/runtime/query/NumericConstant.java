package xuml.tools.model.compiler.runtime.query;

import java.math.BigDecimal;

import xuml.tools.model.compiler.runtime.Entity;

public class NumericConstant<T extends Entity<T>> implements
		NumericExpression<T> {

	private final BigDecimal value;

	public NumericConstant(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getValue() {
		return value;
	}

}
