package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import one_to_one.A;
import one_to_one.A.AId;
import one_to_one.B;
import one_to_one.B.BId;
import one_to_one.Context;
import xuml.tools.model.compiler.runtime.RelationshipNotEstablishedException;

public class BinaryAssociationOneToOneTest {

    @BeforeClass
    public static void setup() {
        EntityManagerFactory emf = PersistenceHelper.createEmf("one-to-one");
        Context.setEntityManagerFactory(emf, 10);
    }

    @AfterClass
    public static void shutdown() {
        Context.close();
    }

    @Test(expected = RelationshipNotEstablishedException.class)
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
            A a = A.create(new AId("boo", "baa"));
            B b = B.create(new BId("some2", "thing2"));
            a.relateAcrossR1(b);
            em.persist(b);
            em.getTransaction().commit();
            em.close();
        }
        {
            EntityManager em = Context.createEntityManager();
            em.getTransaction().begin();
            A a = em.find(A.class, new A.AId("boo", "baa"));
            assertNotNull(a.getB_R1());
            em.getTransaction().commit();
            em.close();
        }
    }
}
