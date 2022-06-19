package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertNotNull;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import unary_zero_one.A;
import unary_zero_one.A.AId;
import unary_zero_one.Context;

public class UnaryAssociationToZeroOneTest {

    private static EntityManagerFactory emf;

    @BeforeClass
    public static void setup() {
        emf = PersistenceHelper.createEmf("unary-zero-one");
        Context.setEntityManagerFactory(emf, 10);
    }

    @AfterClass
    public static void shutdown() {
        emf.close();
    }

    @Test
    public void testCanCreateAWithoutParent() {

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        A.create(new A.AId("hello", "there")).persist(em);
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testCreateAWithParentAndIsPersistedProperly() {
        {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            A a = A.create(new AId("boo", "baa"));
            A parent = A.create(new AId("boo2", "baa2"));
            a.setHasParent_R1(parent);
            em.persist(a);
            em.persist(parent);
            em.getTransaction().commit();
            em.close();
        }
        {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();
            A a = em.find(A.class, new A.AId("boo", "baa"));
            assertNotNull(a.getHasParent_R1());
            em.getTransaction().commit();
            em.close();
        }
    }
}
