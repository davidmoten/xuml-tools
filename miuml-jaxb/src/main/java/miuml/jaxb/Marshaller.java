package miuml.jaxb;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

public class Marshaller {

	private Unmarshaller unmarshaller;

	public Marshaller() {

		try {
			JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
			unmarshaller = context.createUnmarshaller();
			SchemaFactory sf = SchemaFactory
					.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(getClass().getResource(
					"/miuml-metamodel.xsd"));
			unmarshaller.setSchema(schema);
			unmarshaller.setEventHandler(new ValidationEventHandler() {
				@Override
				public boolean handleEvent(ValidationEvent event) {
					throw new RuntimeException(event.getMessage(), event
							.getLinkedException());
				}
			});
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized Domains unmarshal(InputStream is) {
		try {
			return unmarshaller.unmarshal(new StreamSource(is), Domains.class)
					.getValue();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

}