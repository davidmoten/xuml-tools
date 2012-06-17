package miuml.jaxb;

import org.junit.Test;

public class MarshallerTest {

	@Test
	public void testUnmarshal() {
		new Marshaller().unmarshal(MarshallerTest.class
				.getResourceAsStream("/bookstore.xml"));
	}
}
