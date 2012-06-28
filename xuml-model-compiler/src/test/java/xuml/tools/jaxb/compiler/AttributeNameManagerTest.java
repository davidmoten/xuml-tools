package xuml.tools.jaxb.compiler;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AttributeNameManagerTest {

	@Test
	public void test() {
		String column = NameManager.getInstance().toColumnName("Test",
				"B A two");
		assertEquals("b_a_two", column);
	}

}
