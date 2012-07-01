package xuml.tools.model.compiler;

import org.junit.Test;

import xuml.tools.model.compiler.ClassWriter;

public class ClassWriterTest {

	@Test
	public void test() {
		System.out.println(new ClassWriter(new ClassInfoSample()).generate());
	}
}
