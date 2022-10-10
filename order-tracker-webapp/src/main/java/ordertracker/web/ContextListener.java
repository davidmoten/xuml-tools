package ordertracker.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ordertracker.App;
import xuml.tools.util.database.DerbyUtil;

public class ContextListener implements ServletContextListener {

	private static Logger log = LoggerFactory.getLogger(ContextListener.class);
	
    static {
        System.setProperty("org.jboss.logging.provider", "slf4j");
    }

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			DerbyUtil.disableDerbyLog();
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
