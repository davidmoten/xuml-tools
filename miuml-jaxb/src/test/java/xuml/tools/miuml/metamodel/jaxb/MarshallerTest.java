package xuml.tools.miuml.metamodel.jaxb;

import org.junit.Test;

import xuml.tools.miuml.metamodel.jaxb.Marshaller;

/**
 * Tests the {@link Marshaller}.
 * 
 * @author dave
 * 
 */
public class MarshallerTest {

	/**
	 * Tests unmarshall of /samples.xml on the classpath.
	 */
	@Test
	public void testUnmarshalOfSamplesXml() {
		Marshaller m = new Marshaller();
		m.unmarshal(MarshallerTest.class.getResourceAsStream("/samples.xml"));
	}
}
