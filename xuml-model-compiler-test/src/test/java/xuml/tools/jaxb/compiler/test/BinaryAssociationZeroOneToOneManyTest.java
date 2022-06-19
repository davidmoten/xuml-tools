package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertEquals;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xuml.tools.model.compiler.runtime.RelationshipNotEstablishedException;
import zero_one_to_one_many.A;
import zero_one_to_one_many.A.AId;
import zero_one_to_one_many.B;
import zero_one_to_one_many.B.BId;
import zero_one_to_one_many.Context;

public class BinaryAssociationZeroOneToOneManyTest {

    @BeforeClass
    public static void setup() {
        EntityManagerFactory emf = PersistenceHelper.createEmf("zero-one-to-one-many");
        Context.setEntityManagerFactory(emf, 10);
    }

    @AfterClass
    public static void shutdown() {
        Context.close();
    }

    @Test(expected = RelationshipNotEstablishedException.class)
    public void testCreateAWithoutB() {

        EntityManager em = Context.createEntityManager();
        try {
            em.getTransaction().begin();
            A.create(new A.AId("hello", "there")).persist(em);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testCanCreateBWithoutA() {

        EntityManager em = Context.createEntityManager();
        em.getTransaction().begin();
        B.create(new BId("some", "thing")).persist(em);
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testCreateAWithMultipleBAndIsPersistedProperly() {
        {
            EntityManager em = Context.createEntityManager();
            em.getTransaction().begin();
            A a = A.create(new AId("boo", "baa"));
            B b = B.create(new BId("some2", "thing2"));
            B b2 = B.create(new BId("some3", "thing3"));
            a.getB_R1().add(b);
            a.getB_R1().add(b2);
            b2.relateAcrossR1(a);
            b.relateAcrossR1(a);
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
