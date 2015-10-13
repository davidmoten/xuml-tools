package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import one_to_zero_one.A;
import one_to_zero_one.A.AId;
import one_to_zero_one.B;
import one_to_zero_one.B.BId;
import one_to_zero_one.Context;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BinaryAssociationOneToZeroOneTest {

    @BeforeClass
    public static void setup() {
        EntityManagerFactory emf = PersistenceHelper.createEmf("one-to-zero-one");
        Context.setEntityManagerFactory(emf, 10);
    }

    @AfterClass
    public static void shutdown() {
        Context.close();
    }

    @Test
    public void testCreateAWithoutB() {

        EntityManager em = Context.createEntityManager();
        em.getTransaction().begin();
        A.create(new A.AId("hello", "there")).persist(em);
        em.getTransaction().commit();
        em.close();
    }

    @Test(expected = PersistenceException.class)
    public void testCannotCreateBWithoutA() {

        EntityManager em = Context.createEntityManager();
        em.getTransaction().begin();
        try {
            B.create(new BId("some", "thing")).persist(em);
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
    }

    @Test
    public void testCreateAWithBAndIsPersistedProperly() {
        {
            EntityManager em = Context.createEntityManager();
            em.getTransaction().begin();
            A a2 = A.create(new AId("boo", "baa"));
            B b = B.create(new BId("some2", "thing2"));
            a2.relateAcrossR1(b);
            em.persist(b);
            em.getTransaction().commit();
            em.close();
        }
        {
            EntityManager em = Context.createEntityManager();
            em.getTransaction().begin();
            A a2 = em.find(A.class, new A.AId("boo", "baa"));
            assertNotNull(a2.getB_R1());
            em.getTransaction().commit();
            em.close();
        }
    }

}
