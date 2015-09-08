package xuml.tools.model.compiler.runtime.message;

import xuml.tools.model.compiler.runtime.Entity;

public class CloseEntityActor {
    private final Entity<?> entity;

    public CloseEntityActor(Entity<?> entity) {
        this.entity = entity;
    }

    public Entity<?> getEntity() {
        return entity;
    }
}
