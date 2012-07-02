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
	private EntityManagerFactory emf;

	private static Signaller instance;

	public static Signaller getInstance() {
		if (null == instance) {
			instance = new Signaller();
		}
		return instance;
	}

	private Signaller() {
	}

	/**
	 * Sets the entity manager factory for all database interaction managed by
	 * the signaller.
	 * 
	 * @param emf
	 */
	public void setEntityManagerFactory(EntityManagerFactory emf) {
		this.emf = emf;
		root.tell(emf);
	}

	public <T, R> void signal(Entity<T> entity, Event<T> event) {
		long id = persistSignal(entity.getId(), event);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
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

	private void signal(EntityManager em, QueuedSignal sig) {
		Event event = (Event) toObject(sig.eventContent);
		Object id = toObject(sig.idContent);
		Class<?> eventClass;
		try {
			eventClass = Class.forName(sig.eventClassName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		Entity entity = (Entity) em.find(eventClass, id);
		if (entity != null) {
			signal(new Signal(entity, event, sig.id));
		}
	}

	private <T> long persistSignal(Object id, Event<T> event) {
		byte[] idBytes = toBytes(id);
		byte[] eventBytes = toBytes(event);
		QueuedSignal signal = new QueuedSignal(idBytes, event.getClass()
				.getName(), eventBytes);
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
