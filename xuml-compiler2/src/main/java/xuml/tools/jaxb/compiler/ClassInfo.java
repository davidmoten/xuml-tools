package xuml.tools.jaxb.compiler;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.Set;

public abstract class ClassInfo {

	abstract String getPackage();

	abstract String getClassDescription();

	abstract List<List<String>> getUniqueConstraintColumnNames();

	abstract String getSchema();

	abstract String getTable();

	abstract String getJavaClassSimpleName();

	abstract List<String> getOperations();

	abstract TypeRegister getTypes();

	final public String getBehaviourPackage() {
		return getPackage() + ".behaviour";
	}

	final public String getBehaviourFactoryFullClassName() {
		return getBehaviourPackage() + "." + getBehaviourFactorySimpleName();
	}

	final public String getBehaviourFullClassName() {
		return getBehaviourPackage() + "." + getJavaClassSimpleName()
				+ "Behaviour";
	}

	final public String getBehaviourFactorySimpleName() {
		return getJavaClassSimpleName() + "BehaviourFactory";
	}

	final public String addType(java.lang.Class<?> cls) {
		return getTypes().addType(cls);
	}

	final public void addTypes(java.lang.Class<?>... classes) {
		getTypes().addTypes(classes);
	}

	final public String addType(String fullClassName) {
		return getTypes().addType(fullClassName);
	}

	final public String addType(Type type) {
		return getTypes().addType(type);
	}

	abstract List<MyPrimaryIdAttribute> getPrimaryIdAttributeMembers();

	abstract List<MyIndependentAttribute> getNonIdIndependentAttributeMembers();

	abstract List<MyEvent> getEvents();

	abstract List<String> getStateNames();

	abstract List<MyTransition> getTransitions();

	abstract String getStateIdentifier(String state);

	abstract boolean isSuperclass();

	abstract boolean isSubclass();

	abstract MySubclassRole getSubclassRole();

	abstract List<MyReferenceMember> getReferenceMembers();

	abstract Set<String> getAtLeastOneFieldChecks();

	abstract String getImports();

	abstract String getIdColumnName();

	abstract String getContextPackageName();

	abstract Type getType(String name);

	final public String getContextFullClassName() {
		return getContextPackageName() + ".Context";
	}

	final public String getBehaviourFactoryFullName() {
		return getBehaviourPackage() + "." + getBehaviourFactorySimpleName();
	}

	final public String getBehaviourFactoryFieldName() {
		return Util.toJavaIdentifier(getBehaviourFactorySimpleName());
	}

	final public String getClassFullName() {

		return getPackage() + "." + getJavaClassSimpleName();
	}

	public static class MyPrimaryIdAttributeMember extends
			MyIndependentAttribute {

		public MyPrimaryIdAttributeMember(String fieldName, String columnName,
				Type type, boolean nullable, String description) {
			super(fieldName, columnName, type, nullable, description);
		}

	}

	public static class MyPrimaryIdAttribute {
		private final String fieldName;
		private final String columnName;
		private final String referenceClass;
		private final String referenceColumnName;
		private final Type type;
		private final String attributeName;

		public String getAttributeName() {
			return attributeName;
		}

		public MyPrimaryIdAttribute(String attributeName, String fieldName,
				String columnName, String referenceClass,
				String referenceColumnName, Type type) {
			this.attributeName = attributeName;
			this.fieldName = fieldName;
			this.columnName = columnName;
			this.referenceClass = referenceClass;
			this.referenceColumnName = referenceColumnName;
			this.type = type;
		}

		public MyPrimaryIdAttribute(String attributeName, String fieldName,
				String columnName, Type type) {
			this(attributeName, fieldName, columnName, null, null, type);
		}

		public String getFieldName() {
			return fieldName;
		}

		public String getColumnName() {
			return columnName;
		}

		public String getReferenceClass() {
			return referenceClass;
		}

		public String getReferenceColumnName() {
			return referenceColumnName;
		}

		public Type getType() {
			return type;
		}

	}

	public static class MyIndependentAttribute {
		private final String fieldName;
		private final String columnName;
		private final Type type;
		private final boolean nullable;
		private final String description;

		public boolean isNullable() {
			return nullable;
		}

		public MyIndependentAttribute(String fieldName, String columnName,
				Type type, boolean nullable, String description) {
			super();
			this.fieldName = fieldName;
			this.columnName = columnName;
			this.type = type;
			this.nullable = nullable;
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public String getFieldName() {
			return fieldName;
		}

		public String getColumnName() {
			return columnName;
		}

		public Type getType() {
			return type;
		}
	}

	public static class MyParameter {
		private final String fieldName;
		private final String type;

		public String getFieldName() {
			return fieldName;
		}

		public String getType() {
			return type;
		}

		public MyParameter(String fieldName, String type) {
			super();
			this.fieldName = fieldName;
			this.type = type;
		}
	}

	public static class MyEvent {
		private final String name;
		private String simpleClassName;
		private final List<MyParameter> parameters;

		public List<MyParameter> getParameters() {
			return parameters;
		}

		public MyEvent(String name, String simpleClassName,
				List<MyParameter> parameters) {
			this.name = name;
			this.simpleClassName = simpleClassName;
			if (parameters == null)
				this.parameters = newArrayList();
			else
				this.parameters = parameters;
		}

		public String getName() {
			return name;
		}

		public String getSimpleClassName() {
			return simpleClassName;
		}

		public void setSimpleClassName(String simpleClassName) {
			this.simpleClassName = simpleClassName;
		}
	}

	public static class MySubclassRole {
		private final String superclassJavaFullClassName;
		private final String discriminatorValue;

		public MySubclassRole(String superclassJavaFullClassName,
				String discriminatorValue) {
			super();
			this.superclassJavaFullClassName = superclassJavaFullClassName;
			this.discriminatorValue = discriminatorValue;
		}

		public String getSuperclassJavaFullClassName() {
			return superclassJavaFullClassName;
		}

		public String getDiscriminatorValue() {
			return discriminatorValue;
		}
	}

	public static enum Mult {
		ONE, ZERO_ONE, MANY, ONE_MANY;
	}

	public static class JoinColumn {
		private final String thisColumnName;
		private final String otherColumnName;

		public JoinColumn(String thisColumnName, String otherColumnName) {
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

	public static class MyReferenceMember {
		private final String simpleClassName;
		private final String fullClassName;
		private final Mult thisMult;
		private final Mult thatMult;
		private final String thisVerbClause;
		private final String thatVerbClause;
		private final String fieldName;
		private final List<JoinColumn> joinColumns;
		private final String thisName;
		private final String otherName;
		private final MyManyToMany manyToMany;

		public MyReferenceMember(String simpleClassName, String fullClassName,
				Mult thisMult, Mult thatMult, String thisVerbClause,
				String thatVerbClause, String fieldName,
				List<JoinColumn> joinColumns, String thisName,
				String otherName, MyManyToMany manyToMany) {
			this.simpleClassName = simpleClassName;
			this.fullClassName = fullClassName;
			this.thisMult = thisMult;
			this.thatMult = thatMult;
			this.thisVerbClause = thisVerbClause;
			this.thatVerbClause = thatVerbClause;
			this.fieldName = fieldName;
			this.joinColumns = joinColumns;
			this.thisName = thisName;
			this.otherName = otherName;
			this.manyToMany = manyToMany;
		}

		public String getFieldName() {
			return fieldName;
		}

		public String getThisName() {
			return thisName;
		}

		public String getOtherName() {
			return otherName;
		}

		public List<JoinColumn> getJoinColumns() {
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

	}

	public static class MyManyToMany {
		private final String joinTable;
		private final String joinTableSchema;
		private final String thisColumnName;
		private final String thatColumnName;

		public String getThatColumnName() {
			return thatColumnName;
		}

		public MyManyToMany(String joinTable, String joinTableSchema,
				String thisColumnName, String thatColumnName) {
			super();
			this.joinTable = joinTable;
			this.joinTableSchema = joinTableSchema;
			this.thisColumnName = thisColumnName;
			this.thatColumnName = thatColumnName;
		}

		public String getJoinTable() {
			return joinTable;
		}

		public String getJoinTableSchema() {
			return joinTableSchema;
		}

		public String getThisColumnName() {
			return thisColumnName;
		}
	}

	public static class MyTransition {
		private final String eventName;
		private final String fromState;
		private final String toState;
		private final String eventId;

		public String getEventName() {
			return eventName;
		}

		public MyTransition(String eventName, String eventId, String fromState,
				String toState) {
			this.eventName = eventName;
			this.eventId = eventId;
			this.fromState = fromState;
			this.toState = toState;
		}

		public String getEventId() {
			return eventId;
		}

		public String getFromState() {
			return fromState;
		}

		public String getToState() {
			return toState;
		}
	}

	public String getEmbeddedIdSimpleClassName() {
		return getJavaClassSimpleName() + "Id";
	}

	public String getEmbeddedIdAttributeName() {
		return "id";
	}
}