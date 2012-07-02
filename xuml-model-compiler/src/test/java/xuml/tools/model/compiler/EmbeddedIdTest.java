package xuml.tools.model.compiler;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Assert;

import org.junit.Test;

import xuml.tools.model.compiler.runtime.SignalQueue;
import xuml.tools.util.database.DerbyUtil;

public class EmbeddedIdTest {

	@Test
	public void test() {
		DerbyUtil.disableDerbyLog();
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("embeddedIdTest");
		EntityManager em = emf.createEntityManager();
		SignalQueue signal = new SignalQueue(
				String.class.getName(), "id stuff".getBytes(), "event stuff",
				"event.class.name".getBytes());
		em.getTransaction().begin();
		em.persist(signal);
		em.getTransaction().commit();
		em.close();
		System.out.println("id=" + signal.id);
		Assert.assertEquals(1, (long) signal.id);
	}
}
