package xuml.tools.jaxb.compiler.test;

import jakarta.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import specialization.A;
import specialization.B;
import specialization.C;
import specialization.Context;
import xuml.tools.model.compiler.runtime.RelationshipNotEstablishedException;
import xuml.tools.model.compiler.runtime.TooManySpecializationsException;

public class SpecializationTest {

    @BeforeClass
    public static void setup() {
        Context.setEntityManagerFactory(PersistenceHelper.createEmf("specialization"), 10);
    }

    @AfterClass
    public static void close() {
        Context.stop();
        Context.close();
    }

    @Test
    public void testCanCreate() {
        EntityManager em = Context.createEntityManager();
        try {
            em.getTransaction().begin();
            A a = A.create("something");
            B b = B.create("hello");
            a.setTwo(2);
            a.setB_R1(b);
            b.setA_R1(a);
            b.setNumber(3);
            a.persist(em);
            b.persist(em);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test(expected = RelationshipNotEstablishedException.class)
    public void testCannotCreateAWithoutOneSpecialization() {
        EntityManager em = Context.createEntityManager();
        try {
            em.getTransaction().begin();
            A a = A.create("something2");
            a.setTwo(2);
            a.persist(em);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Test(expected = TooManySpecializationsException.class)
    public void testCannotCreateAWithTwoSpecializations() {
        EntityManager em = Context.createEntityManager();
        try {
            em.getTransaction().begin();
            A a = A.create("something3");
            B b = B.create("hello3");
            C c = C.create("there3");
            a.setTwo(2);
            a.setB_R1(b);
            a.setC_R1(c);
            c.setA_R1(a);
            b.setA_R1(a);
            b.setNumber(3);
            a.persist(em);
            b.persist(em);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // TODO cannot create A with both B and C
}
