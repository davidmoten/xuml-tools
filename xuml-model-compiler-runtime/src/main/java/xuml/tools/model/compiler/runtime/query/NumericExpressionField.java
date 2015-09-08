package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class NumericExpressionField<T extends Entity<T>> extends NumericExpression<T> {

    private final Field field;

    public NumericExpressionField(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

}
