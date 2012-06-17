package xuml.tools.jaxb.compiler;

public class ClassAttribute {
	private final String cls;
	private final String attribute;

	public ClassAttribute(String cls, String attribute) {
		super();
		this.cls = cls;
		this.attribute = attribute;
	}

	public String getCls() {
		return cls;
	}

	public String getAttribute() {
		return attribute;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attribute == null) ? 0 : attribute.hashCode());
		result = prime * result + ((cls == null) ? 0 : cls.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassAttribute other = (ClassAttribute) obj;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!attribute.equals(other.attribute))
			return false;
		if (cls == null) {
			if (other.cls != null)
				return false;
		} else if (!cls.equals(other.cls))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClassAttribute [cls=");
		builder.append(cls);
		builder.append(", attribute=");
		builder.append(attribute);
		builder.append("]");
		return builder.toString();
	}

}
