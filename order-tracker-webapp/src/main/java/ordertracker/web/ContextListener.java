package ordertracker.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import ordertracker.App;

import org.apache.log4j.Logger;

import xuml.tools.util.database.DerbyUtil;

public class ContextListener implements ServletContextListener {

	private static Logger log = Logger.getLogger(ContextListener.class);

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
