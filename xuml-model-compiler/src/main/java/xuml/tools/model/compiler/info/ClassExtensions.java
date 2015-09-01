package xuml.tools.model.compiler.info;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public class ClassExtensions {

    private final Optional<String> documentationContent;
    private final Optional<String> documentationMimeType;

    public ClassExtensions(Optional<String> documentationContent,
            Optional<String> documentationMimeType) {
        Preconditions.checkNotNull(documentationContent);
        Preconditions.checkNotNull(documentationMimeType);
        this.documentationContent = documentationContent;
        this.documentationMimeType = documentationMimeType;
    }

    public Optional<String> getDocumentationContent() {
        return documentationContent;
    }

    public Optional<String> getDocumentationMimeType() {
        return documentationMimeType;
    }

}
