package xuml.tools.jaxb;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.io.IOUtils;

public class ApplicationServletContextListener implements
		ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		saveXmlTo("47");
		saveXmlTo("48");
		saveXmlTo("1");
		saveXmlTo("2");
		saveXmlTo("3");
	}

	public void saveXmlTo(String id) {
		try {
			ModelPersistence.instance().save(
					id,
					IOUtils.toString(ApplicationServletContextListener.class
							.getResourceAsStream("/bookstore.xml")));
			PresentationPersistence.instance().save(
					id,
					IOUtils.toString(ApplicationServletContextListener.class
							.getResourceAsStream("/bookstore.json")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}

}
