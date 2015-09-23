package xuml.tools.model.compiler.runtime.message;

public class CloseEntityActor {
    private final String entityUniqueId;

    public CloseEntityActor(String entityUniqueId) {
        this.entityUniqueId = entityUniqueId;
    }

    public String getEntityUniqueId() {
        return entityUniqueId;
    }

}