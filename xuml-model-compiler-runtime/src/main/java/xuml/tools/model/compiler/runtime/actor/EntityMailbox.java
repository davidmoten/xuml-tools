package xuml.tools.model.compiler.runtime.actor;

import javax.persistence.EntityManagerFactory;

import xuml.tools.model.compiler.runtime.message.EntityCommit;
import xuml.tools.model.compiler.runtime.message.Signal;
import akka.actor.ActorSystem;
import akka.dispatch.PriorityGenerator;
import akka.dispatch.UnboundedPriorityMailbox;

import com.typesafe.config.Config;

/**
 * <p>
 * Prioritizes mail for an {@link EntityActor}.
 * </p>
 * <ul>
 * <li>High priority: Signal to self,EntityManagerFactory</li>
 * <li>Medium priority: Commit</li>
 * <li>Low priority: Signal to other</li>
 * <li>Lowest priority: other messages (like StopEntityActor)</li>
 * </ul>
 * 
 * @author dxm
 * 
 */
public class EntityMailbox extends UnboundedPriorityMailbox {
	public EntityMailbox(ActorSystem.Settings settings, Config config) {
		// needed for reflective instantiation
		super(new PriorityGenerator() {
			@Override
			public int gen(Object message) {
				if (message instanceof EntityManagerFactory)
					return 0;
				else if (message instanceof Signal) {
					return 0;// highest priority
				} else if (message instanceof EntityCommit)
					return 10;// medium priority
				else
					return 30;// lowest priority
			}
		});
	}
}