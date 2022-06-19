package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertNotNull;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import zero_one_to_zero_one.A;
import zero_one_to_zero_one.A.AId;
import zero_one_to_zero_one.B;
import zero_one_to_zero_one.B.BId;
import zero_one_to_zero_one.Context;

public class BinaryAssociationZeroOneToZeroOneTest {

    @BeforeClass
    public static void setup() {
        EntityManagerFactory emf = PersistenceHelper.createEmf("zero-one-to-zero-one");
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

    @Test
    public void testCanCreateBWithoutA() {

        EntityManager em = Context.createEntityManager();
        em.getTransaction().begin();
        try {
            B.create(new BId("some", "thing")).persist(em);
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
    }

    @Test(expected = PersistenceException.class)
    public void testCannotCreateAAndBRelatedWithoutPersistingOneFirst() {
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
    }

    @Test
    public void testCreateAWithBAndIsPersistedProperly() {
        {
            EntityManager em = Context.createEntityManager();
            em.getTransaction().begin();
            A a2 = A.create(new AId("boo", "baa"));
            B b = B.create(new BId("some2", "thing2"));
            a2.persist(em);
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
