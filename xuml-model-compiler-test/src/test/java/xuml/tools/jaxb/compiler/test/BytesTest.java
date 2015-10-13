package xuml.tools.jaxb.compiler.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import bytes.A;
import bytes.Context;

public class BytesTest {

    @BeforeClass
    public static void setup() {
        EntityManagerFactory emf = PersistenceHelper.createEmf("bytes");
        Context.setEntityManagerFactory(emf, 10);
    }

    @AfterClass
    public static void shutdown() {
        Context.stop();
    }

    @Test
    public void test() {
        EntityManager em = Context.createEntityManager();
        A a = A.create("1");
        a.setATwo(new byte[] { 1, 2, 3, 4, 5 });
        em.persist(a);
        em.close();
    }
}
