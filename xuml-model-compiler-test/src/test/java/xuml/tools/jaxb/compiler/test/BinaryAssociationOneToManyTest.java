package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import one_to_many.A;
import one_to_many.A.AId;
import one_to_many.B;
import one_to_many.B.BId;
import one_to_many.Context;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class BinaryAssociationOneToManyTest {

    @BeforeClass
    public static void setup() {
        EntityManagerFactory emf = PersistenceHelper.createEmf("one-to-many");
        Context.setEntityManagerFactory(emf);
    }

    @AfterClass
    public static void shutdown() {
        Context.close();
    }

    @Test
    public void testCreateAWithoutB() {

        EntityManager em = Context.createEntityManager();
        em.getTransaction().begin();
        // use Id Builder
        A.create(A.AId.builder().aOne("hello").aTwo("there").build()).persist(em);
        // A.create(new A.AId("hello", "there")).persist(em);
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
    public void testCreateAWithMultipleBAndIsPersistedProperly() {
        {
            EntityManager em = Context.createEntityManager();
            em.getTransaction().begin();
            A a = A.create(new AId("boo", "baa"));
            B b = B.create(new BId("some2", "thing2"));
            B b2 = B.create(new BId("some3", "thing3"));
            // demo method chaining of relateAcross
            a.relateAcrossR1(b).relateAcrossR1(b2);
            a.persist(em);
            b.persist(em);
            b2.persist(em);
            em.getTransaction().commit();
            em.close();
        }
        {
            EntityManager em = Context.createEntityManager();
            em.getTransaction().begin();
            A a2 = em.find(A.class, new A.AId("boo", "baa"));
            assertEquals(2, a2.getB_R1().size());
            em.getTransaction().commit();
            em.close();
        }
    }

}
