package xuml.tools.model.compiler.runtime.message;

import com.google.common.base.Preconditions;

public final class ActorConfig {

    private final int entityActoryPoolSize;

    public ActorConfig(int entityActoryPoolSize) {
        Preconditions.checkArgument(entityActoryPoolSize > 0, "pool size must be > 0");
        this.entityActoryPoolSize = entityActoryPoolSize;
    }

    public int getEntityActoryPoolSize() {
        return entityActoryPoolSize;
    }
}
