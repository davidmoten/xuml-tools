package xuml.tools.jaxb.compiler;

import xuml.tools.jaxb.compiler.message.Commit;
import akka.actor.ActorSystem;
import akka.dispatch.PriorityGenerator;
import akka.dispatch.UnboundedPriorityMailbox;

import com.typesafe.config.Config;

public class EntityMailbox extends UnboundedPriorityMailbox {
	public EntityMailbox(ActorSystem.Settings settings, Config config) {
		// needed for reflective instantiation
		super(new PriorityGenerator() {
			@Override
			public int gen(Object message) {
				if (message instanceof SignalToSelf)
					return 0; // high priority
				else if (message instanceof Commit)
					return 10;// medium priority
				else if (message instanceof SignalToOther)
					return 20;// low priority
				else
					return 30;// lowest priority
			}
		});
	}
}