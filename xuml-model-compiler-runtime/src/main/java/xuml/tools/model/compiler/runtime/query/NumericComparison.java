package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class NumericComparison<T extends Entity<T>> extends BooleanExpression<T> {
    private final NumericExpression<T> n1;
    private final NumericComparisonOperator op;
    private final NumericExpression<T> n2;

    public NumericComparison(NumericExpression<T> n1, NumericComparisonOperator op,
            NumericExpression<T> n2) {
        super();
        this.n1 = n1;
        this.op = op;
        this.n2 = n2;
    }

    public NumericExpression<T> getExpression1() {
        return n1;
    }

    public NumericComparisonOperator getOperator() {
        return op;
    }

    public NumericExpression<T> getExpression2() {
        return n2;
    }
}
