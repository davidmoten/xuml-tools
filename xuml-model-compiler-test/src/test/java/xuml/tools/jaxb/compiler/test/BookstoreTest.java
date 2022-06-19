package xuml.tools.jaxb.compiler.test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import bookstore.Context;
import bookstore.Order;
import bookstore.Order.Behaviour;

public class BookstoreTest {

    @BeforeClass
    public static void setup() {

        // create the entity manager factory
        EntityManagerFactory emf = PersistenceHelper.createEmf("bookstore");

        // pass the EntityManagerFactory to the generated xuml Context
        Context.setEntityManagerFactory(emf, 10);

    }

    @AfterClass
    public static void cleanup() {

        // shutdown the actor system
        Context.stop();

        // close the entity manager factory if desired
        Context.close();
    }

    @Test
    public void testCreateEntityManager() {
        EntityManager em = Context.createEntityManager();
        em.close();
    }

    public Order.BehaviourFactory createOrderBehaviourFactory() {
        return new Order.BehaviourFactory() {

            @Override
            public Behaviour create(Order self) {
                // TOOD create a Behaviour implementation when have some tests
                // for it
                return null;
            }

        };
    }
}
