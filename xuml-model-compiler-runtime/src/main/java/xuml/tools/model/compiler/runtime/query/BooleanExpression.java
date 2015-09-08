package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public abstract class BooleanExpression<T extends Entity<T>> {

    public BooleanExpression<T> and(BooleanExpression<T> e) {
        return new BinaryBooleanExpression<T>(this, BinaryBooleanOperator.AND, e);
    }

    public BooleanExpression<T> or(BooleanExpression<T> e) {
        return new BinaryBooleanExpression<T>(this, BinaryBooleanOperator.OR, e);
    }

    public BooleanExpression<T> not() {
        return new Not<T>(this);
    }

}