package xuml.tools.model.compiler.info;

import java.util.List;

import xuml.tools.model.compiler.ClassInfo.OtherId;

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
    private final MyJoinTable joinTable;
    private final String rnum;
    private final List<OtherId> otherIds;

    public MyReferenceMember(String simpleClassName, String fullClassName, Mult thisMult,
            Mult thatMult, String thisVerbClause, String thatVerbClause, String fieldName,
            List<MyJoinColumn> joinColumns, String mappedBy, MyJoinTable joinTable,
            boolean inPrimaryId, String rnum, List<OtherId> otherIds) {
        this.simpleClassName = simpleClassName;
        this.fullClassName = fullClassName;
        this.thisMult = thisMult;
        this.thatMult = thatMult;
        this.thisVerbClause = thisVerbClause;
        this.thatVerbClause = thatVerbClause;
        this.fieldName = fieldName;
        this.joinColumns = joinColumns;
        this.mappedBy = mappedBy;
        this.joinTable = joinTable;
        this.inPrimaryId = inPrimaryId;
        this.rnum = rnum;
        this.otherIds = otherIds;
    }

    public boolean isInPrimaryId() {
        return inPrimaryId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public List<OtherId> getOtherIds() {
        return otherIds;
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

    public MyJoinTable getJoinTable() {
        return joinTable;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MyReferenceMember [simpleClassName=");
        builder.append(simpleClassName);
        builder.append(", fullClassName=");
        builder.append(fullClassName);
        builder.append(", rnum=");
        builder.append(rnum);
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
        builder.append(", otherIds=");
        builder.append(otherIds);
        builder.append(", joinColumns=");
        builder.append(joinColumns);
        builder.append(", inPrimaryId=");
        builder.append(inPrimaryId);
        builder.append(", thisFieldName=");
        builder.append(mappedBy);
        builder.append(", joinTable=");
        builder.append(joinTable);
        builder.append("]");
        return builder.toString();
    }

    public String getRnum() {
        return rnum;
    }

}