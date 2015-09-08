package xuml.tools.model.compiler.info;

public class MyPrimaryIdAttributeMember extends MyIndependentAttribute {

    public MyPrimaryIdAttributeMember(String attributeName, String fieldName, String columnName,
            MyTypeDefinition type, boolean nullable, String description,
            MyAttributeExtensions extensions) {
        super(attributeName, fieldName, columnName, type, nullable, description, extensions);
    }

}