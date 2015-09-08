package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import unary_one.A;
import unary_one.A.AId;
import unary_one.Context;

public class UnaryAssociationToOneTest {

    @BeforeClass
    public static void setup() {
        EntityManagerFactory emf = PersistenceHelper.createEmf("unary-one");
        Context.setEntityManagerFactory(emf);
    }

    @AfterClass
    public static void shutdown() {
        Context.close();
    }

    @Test(expected = PersistenceException.class)
    public void testCreateAWithoutParent() {

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
    public void testCreateAWithParentAndIsPersistedProperly() {
        {
            EntityManager em = Context.createEntityManager();
            em.getTransaction().begin();
            A a = A.create(new AId("boo", "baa"));
            A parent = A.create(new AId("boo2", "baa2"));
            a.setHasParent_R1(parent);
            a.setHasParentInverse_R1(parent);
            parent.setHasParent_R1(parent);
            parent.setHasParentInverse_R1(parent);
            em.persist(a);
            em.getTransaction().commit();
            em.close();
        }
        {
            EntityManager em = Context.createEntityManager();
            em.getTransaction().begin();
            A a = em.find(A.class, new A.AId("boo", "baa"));
            assertNotNull(a.getHasParent_R1());
            em.getTransaction().commit();
            em.close();
        }
    }
}
