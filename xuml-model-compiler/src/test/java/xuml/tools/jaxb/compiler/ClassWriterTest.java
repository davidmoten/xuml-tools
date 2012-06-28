package xuml.tools.jaxb.compiler;

import org.junit.Test;

public class ClassWriterTest {

	@Test
	public void test() {
		System.out.println(new ClassWriter(new ClassInfoSample()).generate());
	}
}
