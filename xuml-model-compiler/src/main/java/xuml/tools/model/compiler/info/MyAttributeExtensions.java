package xuml.tools.model.compiler.info;

public class MyAttributeExtensions {
    private final boolean generated;
    private final String documentationMimeType;
    private final String documentationContent;
    private final boolean optional;

    public MyAttributeExtensions(boolean generated, String documentationMimeType,
            String documentationContent, boolean optional) {
        this.generated = generated;
        this.documentationMimeType = documentationMimeType;
        this.documentationContent = documentationContent;
        this.optional = optional;
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

    public boolean isOptional() {
        return optional;
    }

}