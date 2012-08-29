package xuml.tools.model.compiler.info;

import java.util.List;


public class MyReferenceMember {
	private final String simpleClassName;
	private final String fullClassName;
	private final Mult thisMult;
	private final Mult thatMult;
	private final String thisVerbClause;
	private final String thatVerbClause;
	private final String fieldName;
	private final List<MyJoinColumn> joinColumns;
	private final boolean inPrimaryId;
	/**
	 * Gets used for mappedBy field in a OneToMany annotation for example.
	 */
	private final String mappedBy;
	private final MyManyToMany manyToMany;
	private final String rnum;

	public MyReferenceMember(String simpleClassName, String fullClassName,
			Mult thisMult, Mult thatMult, String thisVerbClause,
			String thatVerbClause, String fieldName,
			List<MyJoinColumn> joinColumns, String mappedBy,
			MyManyToMany manyToMany, boolean inPrimaryId, String rnum) {
		this.simpleClassName = simpleClassName;
		this.fullClassName = fullClassName;
		this.thisMult = thisMult;
		this.thatMult = thatMult;
		this.thisVerbClause = thisVerbClause;
		this.thatVerbClause = thatVerbClause;
		this.fieldName = fieldName;
		this.joinColumns = joinColumns;
		this.mappedBy = mappedBy;
		this.manyToMany = manyToMany;
		this.inPrimaryId = inPrimaryId;
		this.rnum = rnum;
	}

	public boolean isInPrimaryId() {
		return inPrimaryId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getMappedBy() {
		return mappedBy;
	}

	public List<MyJoinColumn> getJoinColumns() {
		return joinColumns;
	}

	public String getSimpleClassName() {
		return simpleClassName;
	}

	public String getFullClassName() {
		return fullClassName;
	}

	public Mult getThisMult() {
		return thisMult;
	}

	public Mult getThatMult() {
		return thatMult;
	}

	public String getThisVerbClause() {
		return thisVerbClause;
	}

	public String getThatVerbClause() {
		return thatVerbClause;
	}

	public MyManyToMany getManyToMany() {
		return manyToMany;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MyReferenceMember [simpleClassName=");
		builder.append(simpleClassName);
		builder.append(", fullClassName=");
		builder.append(fullClassName);
		builder.append(", thisMult=");
		builder.append(thisMult);
		builder.append(", thatMult=");
		builder.append(thatMult);
		builder.append(", thisVerbClause=");
		builder.append(thisVerbClause);
		builder.append(", thatVerbClause=");
		builder.append(thatVerbClause);
		builder.append(", fieldName=");
		builder.append(fieldName);
		builder.append(", joinColumns=");
		builder.append(joinColumns);
		builder.append(", inPrimaryId=");
		builder.append(inPrimaryId);
		builder.append(", thisFieldName=");
		builder.append(mappedBy);
		builder.append(", manyToMany=");
		builder.append(manyToMany);
		builder.append("]");
		return builder.toString();
	}

	public String getRnum() {
		return rnum;
	}

}