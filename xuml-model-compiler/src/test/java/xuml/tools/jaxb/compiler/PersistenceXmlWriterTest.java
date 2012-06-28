package xuml.tools.jaxb.compiler;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

public class PersistenceXmlWriterTest {

	@Test
	public void test() {

		String s = new PersistenceXmlWriter().generate(Lists
				.newArrayList("bingorama"));
		System.out.println(s);
		Assert.assertTrue(s.contains("bingorama"));

	}
}
