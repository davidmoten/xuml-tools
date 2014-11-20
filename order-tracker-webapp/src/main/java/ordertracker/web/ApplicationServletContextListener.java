package ordertracker.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.github.davidmoten.App;

public class ApplicationServletContextListener implements
		ServletContextListener {
	
	
	private static Logger log = Logger
			.getLogger(ApplicationServletContextListener.class);

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			App.startup();
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		try {
			App.shutdown();
		} catch (RuntimeException e) {
			log.error(e.getMessage(), e);
		}
	}

}
