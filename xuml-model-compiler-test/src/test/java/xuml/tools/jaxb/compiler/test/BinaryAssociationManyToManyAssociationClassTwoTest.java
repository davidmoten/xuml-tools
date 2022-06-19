package xuml.tools.jaxb.compiler.test;

import static org.junit.Assert.assertEquals;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import many_to_many_association_two.A;
import many_to_many_association_two.B;
import many_to_many_association_two.C;
import many_to_many_association_two.Context;

public class BinaryAssociationManyToManyAssociationClassTwoTest {

    @BeforeClass
    public static void setup() {
        EntityManagerFactory emf = PersistenceHelper.createEmf("many-to-many-association-two");
        Context.setEntityManagerFactory(emf, 10);
    }

    @AfterClass
    public static void shutdown() {
        Context.close();
    }

    // TODO many more tests can be added here

    @Test
    public void testCanCreateManyToMany() {

        EntityManager em = Context.createEntityManager();
        em.getTransaction().begin();
        A a1 = A.create("thing").persist(em);
        B b1 = B.create("boo").persist(em);
        B b2 = B.create("boo2").persist(em);
        C c1 = new C("c1");
        c1.setDescription("example");
        c1.setA_R1(a1);
        c1.setB_R1(b1);
        a1.getC_R1().add(c1);
        b1.getC_R1().add(c1);

        C c2 = new C("c2");
        c2.setDescription("example2");
        c2.setA_R1(a1);
        c2.setB_R1(b2);
        a1.getC_R1().add(c2);
        b2.getC_R1().add(c2);

        c1.persist(em);
        c2.persist(em);
        em.getTransaction().commit();

        em.close();

        em = Context.createEntityManager();
        assertEquals(2, a1.load(em).getB_R1().size());

        em.close();
    }
}
