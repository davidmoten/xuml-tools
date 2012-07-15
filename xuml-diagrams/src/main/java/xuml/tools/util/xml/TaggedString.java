package xuml.tools.util.xml;

import java.io.ByteArrayOutputStream;

public class TaggedString extends TaggedOutputStream {

	public TaggedString() {
		super(new ByteArrayOutputStream(), true);
	}

	@Override
	public String toString() {
		return this.getOutputStream().toString();
	}

}