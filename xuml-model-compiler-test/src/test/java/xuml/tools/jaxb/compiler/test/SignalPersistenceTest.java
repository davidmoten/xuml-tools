package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import one_to_zero_one.A;
import one_to_zero_one.A.AId;
import one_to_zero_one.Context;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xuml.tools.model.compiler.runtime.Util;

public class SignalPersistenceTest {

    @BeforeClass
    public static void setup() {
        EntityManagerFactory emf = PersistenceHelper.createEmf("one-to-zero-one");
        Context.setEntityManagerFactory(emf);
    }

    @AfterClass
    public static void shutdown() {
        Context.close();
    }

    @Test
    public void testSignalPersistence() throws ClassNotFoundException {

        EntityManager em = Context.createEntityManager();
        em.getTransaction().begin();
        A.create(new A.AId("hello2", "there2")).persist(em);
        em.getTransaction().commit();

        // now test can find using id
        em.getTransaction().begin();
        assertNotNull(em.find(A.class, new A.AId("hello2", "there2")));
        em.getTransaction().commit();

        // now test can find using id and Class.forName
        em.getTransaction().begin();
        assertNotNull(em.find(Class.forName(A.class.getName()), new A.AId("hello2", "there2")));
        em.getTransaction().commit();

        em.close();

        // now test can find using new em, Class.forName and reconstituted id
        em = Context.createEntityManager();
        em.getTransaction().begin();
        AId id = new A.AId("hello2", "there2");
        Object id2 = Util.toObject(Util.toBytes(id));
        assertNotNull(em.find(Class.forName(A.class.getName()), id2));
        em.getTransaction().commit();

        em.close();
    }

}
