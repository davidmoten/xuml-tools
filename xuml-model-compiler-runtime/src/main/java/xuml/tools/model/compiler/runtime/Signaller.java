package xuml.tools.model.compiler.runtime;

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

	public EntityManagerFactory getEntityManagerFactory() {
		return emf;
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
			em.getTransaction().begin();
			t.event(event);
			em.persist(t);
			em.getTransaction().commit();
			em.close();
			return t;
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public <T extends Entity<T>> void signal(Entity<T> entity, Event<T> event) {
		@SuppressWarnings("unchecked")
		long id = persistSignal(entity.getId(), (Class<T>) entity.getClass(),
				event);
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
	public int sendSignalsInQueue() {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		List<QueuedSignal> signals = em.createQuery(
				"select s from " + QueuedSignal.class.getSimpleName()
						+ " s order by id").getResultList();
		em.getTransaction().commit();
		em.close();
		// close transaction before signalling because EntityActor will attempt
		// to delete the QueuedSignal within its transaction processing the
		// event
		for (QueuedSignal sig : signals) {
			signal(sig);
		}
		return signals.size();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void signal(QueuedSignal sig) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		System.out.println("sending " + sig);
		Event event = (Event) Util.toObject(sig.eventContent);
		Object id = Util.toObject(sig.idContent);
		Class<?> entityClass;
		entityClass = getClassForName(sig.entityClassName);
		Entity entity = (Entity) em.find(entityClass, id);
		em.getTransaction().commit();
		em.close();
		if (entity != null) {
			signal(new Signal(entity, event, sig.id));
		} else
			System.out.println("ENTITY NOT FOUND for entityClassName="
					+ sig.entityClassName + ",id=" + id);
	}

	private Class<?> getClassForName(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public <T extends Entity<T>> long persistSignal(Object id, Class<T> cls,
			Event<T> event) {
		byte[] idBytes = Util.toBytes(id);
		byte[] eventBytes = Util.toBytes(event);
		QueuedSignal signal = new QueuedSignal(idBytes, cls.getName(), event
				.getClass().getName(), eventBytes);
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(signal);
		em.getTransaction().commit();
		em.close();
		return signal.id;
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

	public void close() {
		emf.close();
	}
}
