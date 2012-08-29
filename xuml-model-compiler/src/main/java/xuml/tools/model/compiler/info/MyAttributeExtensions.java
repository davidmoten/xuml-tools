package xuml.tools.model.compiler.info;

public class MyAttributeExtensions {
	private final boolean generated;
	private final String documentationMimeType;
	private final String documentationContent;

	public MyAttributeExtensions(boolean generated,
			String documentationMimeType, String documentationContent) {
		super();
		this.generated = generated;
		this.documentationMimeType = documentationMimeType;
		this.documentationContent = documentationContent;
	}

	public boolean isGenerated() {
		return generated;
	}

	public String getDocumentationMimeType() {
		return documentationMimeType;
	}

	public String getDocumentationContent() {
		return documentationContent;
	}

}