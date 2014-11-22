package ordertracker;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

public class App {

	private static Logger log = Logger.getLogger(App.class);

	public static void startup() {

		log.info("starting up");

		// create the entity manager factory
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("testPersistenceUnit");

		// pass the EntityManagerFactory to the generated xuml Context
		Context.setEntityManagerFactory(emf);

		// set the behaviour factory for Order
		Order.setBehaviourFactory(OrderBehaviour.createFactory());

		// send any signals not processed from last shutdown
		Context.sendSignalsInQueue();

		log.info("started up");
	}

	public static void shutdown() {
		// shutdown the actor system
		Context.stop();

		// close the entity manager factory if desired
		Context.close();

	}

}