package xuml.tools.model.compiler.info;

public class MyParameter {
    private final String fieldName;
    private final String type;

    public String getFieldName() {
        return fieldName;
    }

    public String getType() {
        return type;
    }

    public MyParameter(String fieldName, String type) {
        super();
        this.fieldName = fieldName;
        this.type = type;
    }
}