package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class BinaryNumericExpression<T extends Entity<T>> extends NumericExpression<T> {

    private final NumericExpression<T> e1;
    private final NumericExpression<T> e2;
    private final BinaryNumericOperator op;

    public BinaryNumericExpression(NumericExpression<T> e1, BinaryNumericOperator op,
            NumericExpression<T> e2) {
        this.e1 = e1;
        this.op = op;
        this.e2 = e2;
    }

    public NumericExpression<T> getExpression1() {
        return e1;
    }

    public NumericExpression<T> getExpression2() {
        return e2;
    }

    public BinaryNumericOperator getOperator() {
        return op;
    }

}
