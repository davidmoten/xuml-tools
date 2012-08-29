package xuml.tools.model.compiler.info;

public class MySubclassRole {
	private final String superclassJavaFullClassName;
	private final String discriminatorValue;

	public MySubclassRole(String superclassJavaFullClassName,
			String discriminatorValue) {
		super();
		this.superclassJavaFullClassName = superclassJavaFullClassName;
		this.discriminatorValue = discriminatorValue;
	}

	public String getSuperclassJavaFullClassName() {
		return superclassJavaFullClassName;
	}

	public String getDiscriminatorValue() {
		return discriminatorValue;
	}
}