package ${package};

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import xuml.tools.util.database.DerbyUtil;
import test.A;
import test.B;
import test.Context;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import xuml.tools.util.database.DerbyUtil;

public class AppTest {
	
	
	@BeforeClass
	public static void setup() {
		DerbyUtil.disableDerbyLog();

		// create the entity manager factory
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("testPersistenceUnit");

		// pass the EntityManagerFactory to the generated xuml Context
		Context.setEntityManagerFactory(emf);

		// set the behaviour factory for the class A
		// A.setBehaviourFactory(createBehaviourFactory());
	}
	
	@Test
	public void dummy() {
		
	}

	@AfterClass
	public static void cleanup() {

		// shutdown the actor system
		Context.stop();

		// close the entity manager factory if desired
		Context.close();
	}

	
}