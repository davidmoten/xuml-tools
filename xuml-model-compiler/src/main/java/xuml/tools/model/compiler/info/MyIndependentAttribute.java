package xuml.tools.model.compiler.info;

public class MyIndependentAttribute {
	private final String fieldName;
	private final String columnName;
	private final MyTypeDefinition type;
	private final boolean nullable;
	private final String description;
	private final MyAttributeExtensions extensions;
	private final String attributeName;

	public MyIndependentAttribute(String attributeName, String fieldName,
			String columnName, MyTypeDefinition type, boolean nullable,
			String description, MyAttributeExtensions extensions) {
		super();
		this.attributeName = attributeName;
		this.fieldName = fieldName;
		this.columnName = columnName;
		this.type = type;
		this.nullable = nullable;
		this.description = description;
		this.extensions = extensions;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public String getDescription() {
		return description;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getColumnName() {
		return columnName;
	}

	public MyTypeDefinition getType() {
		return type;
	}

	public MyAttributeExtensions getExtensions() {
		return extensions;
	}

	public boolean isNullable() {
		return nullable && !extensions.isGenerated();
	}

}