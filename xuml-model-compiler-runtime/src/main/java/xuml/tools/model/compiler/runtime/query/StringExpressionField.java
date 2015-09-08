package xuml.tools.model.compiler.runtime.query;

import xuml.tools.model.compiler.runtime.Entity;

public class StringExpressionField<T extends Entity<T>> extends StringExpression<T> {

    private final Field field;

    public StringExpressionField(Field field) {
        super();
        this.field = field;
    }

    public Field getField() {
        return field;
    }

}
