package xuml.tools.model.compiler.runtime;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;

public class Info {

    private Entity<?> currentEntity;

    public Entity<?> getCurrentEntity() {
        return currentEntity;
    }

    public void setCurrentEntity(Entity<?> entity) {
        this.currentEntity = entity;
    }

    private AtomicInteger counter = new AtomicInteger(0);

    public AtomicInteger getCounter() {
        return counter;
    }

    public void setCounter(AtomicInteger counter) {
        this.counter = counter;
    }

    private UUID id;
    private EntityManager em;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setCurrentEntityManager(EntityManager em) {
        this.em = em;
    }

    public EntityManager getCurrentEntityManager() {
        return em;
    }

}
