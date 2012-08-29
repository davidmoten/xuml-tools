package xuml.tools.model.compiler.info;

public class MyJoinColumn {
	private final String thisColumnName;
	private final String otherColumnName;

	public MyJoinColumn(String thisColumnName, String otherColumnName) {
		this.thisColumnName = thisColumnName;
		this.otherColumnName = otherColumnName;
	}

	public String getThisColumnName() {
		return thisColumnName;
	}

	public String getOtherColumnName() {
		return otherColumnName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("JoinColumn [thisColumnName=");
		builder.append(thisColumnName);
		builder.append(", otherColumnName=");
		builder.append(otherColumnName);
		builder.append("]");
		return builder.toString();
	}

}