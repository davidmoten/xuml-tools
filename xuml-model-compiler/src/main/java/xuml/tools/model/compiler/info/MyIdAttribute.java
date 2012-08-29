package xuml.tools.model.compiler.info;

public class MyIdAttribute {
	private final String fieldName;
	private final String columnName;
	private final String referenceClass;
	private final String referenceColumnName;
	private final MyTypeDefinition type;
	private final String attributeName;
	private final MyAttributeExtensions extensions;

	public String getAttributeName() {
		return attributeName;
	}

	public MyIdAttribute(String attributeName, String fieldName,
			String columnName, String referenceClass,
			String referenceColumnName, MyTypeDefinition type,
			MyAttributeExtensions extensions) {
		this.attributeName = attributeName;
		this.fieldName = fieldName;
		this.columnName = columnName;
		this.referenceClass = referenceClass;
		this.referenceColumnName = referenceColumnName;
		this.type = type;
		this.extensions = extensions;
	}

	public MyIdAttribute(String attributeName, String fieldName,
			String columnName, MyTypeDefinition type,
			MyAttributeExtensions extensions) {
		this(attributeName, fieldName, columnName, null, null, type, extensions);
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getReferenceClass() {
		return referenceClass;
	}

	public String getReferenceColumnName() {
		return referenceColumnName;
	}

	public MyTypeDefinition getType() {
		return type;
	}

	public MyAttributeExtensions getExtensions() {
		return extensions;
	}

}