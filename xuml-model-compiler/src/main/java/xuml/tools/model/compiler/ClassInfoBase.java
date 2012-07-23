package xuml.tools.model.compiler;

import static com.google.common.collect.Lists.newArrayList;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import xuml.tools.miuml.metamodel.jaxb.EventSignature;
import xuml.tools.miuml.metamodel.jaxb.StateSignature;

public abstract class ClassInfoBase {

	abstract String getPackage();

	abstract String getClassDescription();

	abstract List<List<String>> getUniqueConstraintColumnNames();

	abstract String getSchema();

	abstract String getTable();

	abstract String getJavaClassSimpleName();

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

	abstract List<MyIdAttribute> getPrimaryIdAttributeMembers();

	abstract List<MyIndependentAttribute> getNonIdIndependentAttributeMembers();

	abstract List<MyEvent> getEvents();

	abstract List<String> getStateNames();

	abstract List<MyTransition> getTransitions();

	abstract String getStateAsJavaIdentifier(String state);

	abstract boolean isSuperclass();

	abstract boolean isSubclass();

	abstract MySubclassRole getSubclassRole();

	abstract List<MyReferenceMember> getReferenceMembers();

	// TODO chuck this
	abstract Set<String> getAtLeastOneFieldChecks();

	abstract String getImports(String relativeToClass);

	abstract String getIdColumnName();

	abstract String getContextPackageName();

	abstract Type getType(String name);

	final public String getContextFullClassName() {
		return getContextPackageName() + ".Context";
	}

	final public String getBehaviourFactoryFieldName() {
		return Util.toJavaIdentifier(getBehaviourFactorySimpleName());
	}

	final public String getClassFullName() {

		return getPackage() + "." + getJavaClassSimpleName();
	}

	public static class MyPrimaryIdAttributeMember extends
			MyIndependentAttribute {

		public MyPrimaryIdAttributeMember(String attributeName,
				String fieldName, String columnName, MyTypeDefinition type,
				boolean nullable, String description,
				MyAttributeExtensions extensions) {
			super(attributeName, fieldName, columnName, type, nullable,
					description, extensions);
		}

	}

	public static class MyIdAttribute {
		private final String fieldName;
		private final String columnName;
		private final String referenceClass;
		private final String referenceColumnName;
		private final MyTypeDefinition type;
		private final String attributeName;
		private final MyAttributeExtensions extensions;

		public String getAttributeName() {
			return attributeName;
		}

		public MyIdAttribute(String attributeName, String fieldName,
				String columnName, String referenceClass,
				String referenceColumnName, MyTypeDefinition type,
				MyAttributeExtensions extensions) {
			this.attributeName = attributeName;
			this.fieldName = fieldName;
			this.columnName = columnName;
			this.referenceClass = referenceClass;
			this.referenceColumnName = referenceColumnName;
			this.type = type;
			this.extensions = extensions;
		}

		public MyIdAttribute(String attributeName, String fieldName,
				String columnName, MyTypeDefinition type,
				MyAttributeExtensions extensions) {
			this(attributeName, fieldName, columnName, null, null, type,
					extensions);
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

		public MyTypeDefinition getType() {
			return type;
		}

		public MyAttributeExtensions getExtensions() {
			return extensions;
		}

	}

	public static class MyIndependentAttribute {
		private final String fieldName;
		private final String columnName;
		private final MyTypeDefinition type;
		private final boolean nullable;
		private final String description;
		private final MyAttributeExtensions extensions;
		private final String attributeName;

		public MyIndependentAttribute(String attributeName, String fieldName,
				String columnName, MyTypeDefinition type, boolean nullable,
				String description, MyAttributeExtensions extensions) {
			super();
			this.attributeName = attributeName;
			this.fieldName = fieldName;
			this.columnName = columnName;
			this.type = type;
			this.nullable = nullable;
			this.description = description;
			this.extensions = extensions;
		}

		public String getAttributeName() {
			return attributeName;
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

		public MyTypeDefinition getType() {
			return type;
		}

		public MyAttributeExtensions getExtensions() {
			return extensions;
		}

		public boolean isNullable() {
			return nullable && !extensions.isGenerated();
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
		private final String stateName;
		private final String stateSignatureInterfaceSimpleName;
		private final boolean creates;

		public List<MyParameter> getParameters() {
			return parameters;
		}

		public MyEvent(String name, String simpleClassName,
				List<MyParameter> parameters, String stateName,
				String stateSignatureInterfaceSimpleName, boolean creates) {
			this.name = name;
			this.simpleClassName = simpleClassName;
			this.stateName = stateName;
			this.stateSignatureInterfaceSimpleName = stateSignatureInterfaceSimpleName;
			this.creates = creates;
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

		/**
		 * If the parameter list was obtained from the {@link StateSignature}
		 * rather than the {@link EventSignature} then this returns the state
		 * name.
		 * 
		 * @return
		 */
		public String getStateName() {
			return stateName;
		}

		public String getStateSignatureInterfaceSimpleName() {
			return stateSignatureInterfaceSimpleName;
		}

		public boolean getCreates() {
			return creates;
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

	public static class MyJoinColumn {
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

	public static enum MyType {
		BOOLEAN, INTEGER, REAL, DATE, TIMESTAMP, STRING, ARBITRARY_ID, ENUMERATION
	}

	public final static class MyTypeDefinition {
		private final String name;
		private final MyType myType;
		private final Type type;
		private final String units;
		private final BigInteger precision;
		private final BigDecimal lowerLimit;
		private final BigDecimal upperLimit;
		private final String defaultValue;
		private final List<String> enumeration;
		private final BigInteger minLength;
		private final BigInteger maxLength;
		private final String prefix;
		private final String suffix;
		private final String validationPattern;

		public MyTypeDefinition(String name, MyType myType, Type type,
				String units, BigInteger precision, BigDecimal lowerLimit,
				BigDecimal upperLimit, String defaultValue,
				List<String> enumeration, BigInteger minLength,
				BigInteger maxLength, String prefix, String suffix,
				String validationPattern) {
			this.name = name;
			this.myType = myType;
			this.type = type;
			this.units = units;
			this.precision = precision;
			this.lowerLimit = lowerLimit;
			this.upperLimit = upperLimit;
			this.defaultValue = defaultValue;
			this.enumeration = enumeration;
			this.minLength = minLength;
			this.maxLength = maxLength;
			this.prefix = prefix;
			this.suffix = suffix;
			this.validationPattern = validationPattern;
		}

		public String getName() {
			return name;
		}

		public MyType getMyType() {
			return myType;
		}

		public Type getType() {
			return type;
		}

		public String getUnits() {
			return units;
		}

		public BigInteger getPrecision() {
			return precision;
		}

		public BigDecimal getLowerLimit() {
			return lowerLimit;
		}

		public BigDecimal getUpperLimit() {
			return upperLimit;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public List<String> getEnumeration() {
			return enumeration;
		}

		public BigInteger getMinLength() {
			return minLength;
		}

		public BigInteger getMaxLength() {
			return maxLength;
		}

		public String getPrefix() {
			return prefix;
		}

		public String getSuffix() {
			return suffix;
		}

		public String getValidationPattern() {
			return validationPattern;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("MyTypeDefinition [name=");
			builder.append(name);
			builder.append(", type=");
			builder.append(type);
			builder.append(", units=");
			builder.append(units);
			builder.append(", precision=");
			builder.append(precision);
			builder.append(", lowerLimit=");
			builder.append(lowerLimit);
			builder.append(", upperLimit=");
			builder.append(upperLimit);
			builder.append(", defaultValue=");
			builder.append(defaultValue);
			builder.append(", enumeration=");
			builder.append(enumeration);
			builder.append(", minLength=");
			builder.append(minLength);
			builder.append(", maxLength=");
			builder.append(maxLength);
			builder.append(", prefix=");
			builder.append(prefix);
			builder.append(", suffix=");
			builder.append(suffix);
			builder.append(", validationPattern=");
			builder.append(validationPattern);
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
		private final List<MyJoinColumn> joinColumns;
		private final boolean inPrimaryId;
		/**
		 * Gets used for mappedBy field in a OneToMany annotation for example.
		 */
		private final String thisFieldName;
		private final MyManyToMany manyToMany;

		public MyReferenceMember(String simpleClassName, String fullClassName,
				Mult thisMult, Mult thatMult, String thisVerbClause,
				String thatVerbClause, String fieldName,
				List<MyJoinColumn> joinColumns, String thisFieldName,
				MyManyToMany manyToMany, boolean inPrimaryId) {
			this.simpleClassName = simpleClassName;
			this.fullClassName = fullClassName;
			this.thisMult = thisMult;
			this.thatMult = thatMult;
			this.thisVerbClause = thisVerbClause;
			this.thatVerbClause = thatVerbClause;
			this.fieldName = fieldName;
			this.joinColumns = joinColumns;
			this.thisFieldName = thisFieldName;
			this.manyToMany = manyToMany;
			this.inPrimaryId = inPrimaryId;
		}

		public boolean isInPrimaryId() {
			return inPrimaryId;
		}

		public String getFieldName() {
			return fieldName;
		}

		public String getThisFieldName() {
			return thisFieldName;
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
			builder.append(thisFieldName);
			builder.append(", manyToMany=");
			builder.append(manyToMany);
			builder.append("]");
			return builder.toString();
		}

	}

	public static class MyManyToMany {
		private final String joinTable;
		private final String joinTableSchema;
		private final List<MyJoinColumn> joinColumns;
		private final List<MyJoinColumn> inverseJoinColumns;

		public MyManyToMany(String joinTable, String joinTableSchema,
				List<MyJoinColumn> joinColumns,
				List<MyJoinColumn> inverseJoinColumns) {
			super();
			this.joinTable = joinTable;
			this.joinTableSchema = joinTableSchema;
			this.joinColumns = joinColumns;
			this.inverseJoinColumns = inverseJoinColumns;
		}

		public String getJoinTable() {
			return joinTable;
		}

		public String getJoinTableSchema() {
			return joinTableSchema;
		}

		public List<MyJoinColumn> getJoinColumns() {
			return joinColumns;
		}

		public List<MyJoinColumn> getInverseJoinColumns() {
			return inverseJoinColumns;
		}

	}

	public static class MyTransition {
		private final String eventName;
		private final String eventSimpleClassName;
		private final String fromState;
		private final String toState;
		private final String eventId;

		public MyTransition(String eventName, String eventSimpleClassName,
				String eventId, String fromState, String toState) {
			this.eventName = eventName;
			this.eventSimpleClassName = eventSimpleClassName;
			this.eventId = eventId;
			this.fromState = fromState;
			this.toState = toState;
		}

		public String getEventName() {
			return eventName;
		}

		public String getEventSimpleClassName() {
			return eventSimpleClassName;
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

		public boolean isCreationTransition() {
			return fromState == null;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("MyTransition [eventName=");
			builder.append(eventName);
			builder.append(", fromState=");
			builder.append(fromState);
			builder.append(", toState=");
			builder.append(toState);
			builder.append(", eventId=");
			builder.append(eventId);
			builder.append("]");
			return builder.toString();
		}
	}

	public String getEmbeddedIdSimpleClassName() {
		return getJavaClassSimpleName() + "Id";
	}

	public String getEmbeddedIdAttributeName() {
		return "id";
	}

	public boolean hasBehaviour() {
		return getEvents().size() > 0;
	}

	public static class MySpecializations {
		private final BigInteger rnum;
		private final Set<String> fieldNames;

		public MySpecializations(BigInteger rnum, Set<String> fieldNames) {
			super();
			this.rnum = rnum;
			this.fieldNames = fieldNames;
		}

		public BigInteger getRnum() {
			return rnum;
		}

		public Set<String> getFieldNames() {
			return fieldNames;
		}

	}

	public static class MyAttributeExtensions {
		private final boolean generated;
		private final String documentationMimeType;
		private final String documentationContent;

		public MyAttributeExtensions(boolean generated,
				String documentationMimeType, String documentationContent) {
			super();
			this.generated = generated;
			this.documentationMimeType = documentationMimeType;
			this.documentationContent = documentationContent;
		}

		public boolean isGenerated() {
			return generated;
		}

		public String getDocumentationMimeType() {
			return documentationMimeType;
		}

		public String getDocumentationContent() {
			return documentationContent;
		}

	}

	public static class MyFind {

		private final List<MyIndependentAttribute> attributes;

		public MyFind(List<MyIndependentAttribute> attributes) {
			this.attributes = attributes;
		}

		public List<MyIndependentAttribute> getAttributes() {
			return attributes;
		}

	}
}