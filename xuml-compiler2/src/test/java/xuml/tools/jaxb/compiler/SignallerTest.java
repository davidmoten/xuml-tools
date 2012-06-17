package xuml.tools.jaxb.compiler;

import org.junit.Test;

public class SignallerTest {

	private static class MyTest implements Entity<MyTest, Long> {

		@Override
		public Long getId() {
			return null;
		}

		@Override
		public void event(Event<MyTest> event) {
			// do nothing
		}
	}

	private static class MyEvent implements Event<MyTest> {
	}

	@Test
	public void testSignaller() {

		Signaller<MyTest, Long> s = new Signaller<MyTest, Long>(null,
				MyTest.class);
		s.signal(1L, new MyEvent());
	}
}
