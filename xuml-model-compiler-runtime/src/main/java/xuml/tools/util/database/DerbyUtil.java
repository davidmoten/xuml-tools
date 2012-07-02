package xuml.tools.util.database;

import java.io.OutputStream;

public class DerbyUtil {

	public static OutputStream nullOutputStream() {
		return new OutputStream() {
			@Override
			public void write(int b) {
			}
		};
	}

	public static void disableDerbyLog() {
		System.setProperty("derby.stream.error.method",
				DerbyUtil.class.getName() + ".nullOutputStream");
	}

}