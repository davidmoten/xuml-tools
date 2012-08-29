package xuml.tools.model.compiler.info;

import xuml.tools.model.compiler.ClassInfoBase;

public class MyPrimaryIdAttributeMember extends
		MyIndependentAttribute {

	public MyPrimaryIdAttributeMember(String attributeName,
			String fieldName, String columnName, MyTypeDefinition type,
			boolean nullable, String description,
			MyAttributeExtensions extensions) {
		super(attributeName, fieldName, columnName, type, nullable,
				description, extensions);
	}

}