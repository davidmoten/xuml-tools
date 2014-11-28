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

		// set the behaviour factories
		Order.setBehaviourFactory(OrderBehaviour.class);
		Depot.setBehaviourFactory(DepotBehaviour.class);
		SystemEvent.setBehaviourFactory(SystemEventBehaviour.class);

		// send any signals not processed from last shutdown
		Context.sendSignalsInQueue();

		// create the singleton event entity
		SystemEvent.create(new SystemEvent.Events.Create("1"));
		// create the example depot
		Depot.create(new Depot.Events.Create("1", "Gundagai", -35.0, 142.0));

		log.info("started up");
	}

	public static void shutdown() {
		// shutdown the actor system
		Context.stop();

		// close the entity manager factory if desired
		Context.close();

	}

}