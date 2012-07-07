package xuml.tools.model.compiler.runtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import xuml.tools.model.compiler.runtime.actor.RootActor;
import xuml.tools.model.compiler.runtime.message.EntityCommit;
import xuml.tools.model.compiler.runtime.message.Signal;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class Signaller {

	private final ThreadLocal<Info> info = new ThreadLocal<Info>() {
		@Override
		protected Info initialValue() {
			return new Info();
		}
	};
	private final ActorSystem actorSystem = ActorSystem.create();
	private final ActorRef root = actorSystem.actorOf(
			new Props(RootActor.class), "root");
	private final EntityManagerFactory emf;

	public Signaller(EntityManagerFactory emf) {
		this.emf = emf;
		root.tell(emf);
	}

	/**
	 * Returns a new instance of type T using the given {@link CreationEvent}.
	 * This is a synchronous creation using a newly created then closed
	 * EntityManager for persisting the entity. If you need finer grained
	 * control of commits then open your own entity manager and do the the
	 * persist yourself.
	 * 
	 * @param cls
	 * @param event
	 * @return
	 */
	public <T extends Entity<T>> T create(Class<T> cls, CreationEvent<T> event) {
		try {
			T t = cls.newInstance();
			EntityManager em = emf.createEntityManager();
			t.event(event);
			em.persist(t);
			em.close();
			return t;
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> void signal(Entity<T> entity, Event<T> event) {
		long id = persistSignal(entity.getId(),
				(Class<Entity<T>>) entity.getClass(), event);
		Signal<T> signal = new Signal<T>(entity, event, id);
		signal(signal);
	}

	private <T> void signal(Signal<T> signal) {
		if (signalInitiatedFromEvent()) {
			info.get().getCurrentEntity().helper().queueSignal(signal);
		} else {
			root.tell(signal);
		}
	}

	@SuppressWarnings({ "unchecked" })
	public void sendSignalsInQueue() {
		EntityManager em = emf.createEntityManager();
		List<QueuedSignal> signals = em.createQuery(
				"select s from " + QueuedSignal.class.getSimpleName()
						+ " s order by id").getResultList();
		for (QueuedSignal sig : signals) {
			signal(em, sig);
		}
		em.close();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void signal(EntityManager em, QueuedSignal sig) {
		Event event = (Event) toObject(sig.eventContent);
		Object id = toObject(sig.idContent);
		Class<?> entityClass;
		try {
			entityClass = Class.forName(sig.entityClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		Entity entity = (Entity) em.find(entityClass, id);
		if (entity != null) {
			signal(new Signal(entity, event, sig.id));
		}
	}

	public <T> long persistSignal(Object id, Class<Entity<T>> cls,
			Event<T> event) {
		byte[] idBytes = toBytes(id);
		byte[] eventBytes = toBytes(event);
		QueuedSignal signal = new QueuedSignal(idBytes, cls.getName(), event
				.getClass().getName(), eventBytes);
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(signal);
		em.getTransaction().commit();
		em.close();
		return signal.id;
	}

	private byte[] toBytes(Object object) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bytes);
			oos.writeObject(object);
			oos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return bytes.toByteArray();
	}

	private Object toObject(byte[] bytes) {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		Object object;
		try {
			ObjectInputStream ois = new ObjectInputStream(in);
			object = ois.readObject();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return object;
	}

	private boolean signalInitiatedFromEvent() {
		return info.get().getCurrentEntity() != null;
	}

	public <T, R> void signalCommit(Entity<T> entity) {
		root.tell(new EntityCommit<T>(entity));
	}

	public Info getInfo() {
		return info.get();
	}

	public void stop() {
		actorSystem.shutdown();
	}
}
