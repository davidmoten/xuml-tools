package xuml.tools.jaxb.compiler.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.RollbackException;

import one_many_to_many_association.A;
import one_many_to_many_association.B;
import one_many_to_many_association.C;
import one_many_to_many_association.Context;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xuml.tools.model.compiler.runtime.RelationshipNotEstablishedException;

public class BinaryAssociationOneManyToManyAssociationClassTest {

    @BeforeClass
    public static void setup() {
        EntityManagerFactory emf = PersistenceHelper.createEmf("one-many-to-many-association");
        Context.setEntityManagerFactory(emf, 10);
    }

    @AfterClass
    public static void shutdown() {
        Context.close();
    }

    // TODO many more tests can be added here

    @Test(expected = RelationshipNotEstablishedException.class)
    public void testCannotCreateBWithoutInstanceOfAViaC() {

        EntityManager em = Context.createEntityManager();
        try {
            em.getTransaction().begin();
            B.create("b").persist(em);
        } finally {
            em.close();
        }
    }

    @Test
    public void testCanCreateInstanceOfAWithoutB() {

        EntityManager em = Context.createEntityManager();
        try {
            em.getTransaction().begin();
            A.create("a").persist(em);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test
    public void testCanCreateInstanceOfBWithA() {

        EntityManager em = Context.createEntityManager();
        try {
            em.getTransaction().begin();
            A a = A.create("a1").persist(em);
            B b = B.create("b1");
            C c = C.create("c1");
            c.setA_R1(a);
            c.setB_R1(b);
            c.setDescription("hello");
            a.getC_R1().add(c);
            b.getC_R1().add(c);
            em.persist(b);
            em.persist(c);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test(expected = RollbackException.class)
    public void testCannotCreateTwoLinksBetweenAAndB() {

        EntityManager em = Context.createEntityManager();
        try {
            em.getTransaction().begin();
            A a = A.create("a2").persist(em);
            B b = B.create("b2");
            C c = C.create("c2");
            c.setA_R1(a);
            c.setB_R1(b);
            c.setDescription("hello");
            a.getC_R1().add(c);
            b.getC_R1().add(c);
            em.persist(b);
            em.persist(c);
            C c3 = C.create("c3");
            c3.setA_R1(a);
            c3.setB_R1(b);
            c3.setDescription("hello");
            a.getC_R1().add(c3);
            b.getC_R1().add(c3);
            c3.persist(em);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
