package xuml.tools.jaxb.compiler;

public class ClassInfoFromJaxb {

	// private static final String UNEXPECTED = "unexpected";
	// private final Class cls;
	// private final String packageName;
	// private final Lookups lookups;
	// private final TypeRegister types = new TypeRegister();
	// private final String contextPackageName;
	// private final Map<String, String> domainPackageNames;
	// private final Domain domain;
	//
	// public ClassInfoFromJaxb(Domain domain, Class cls,
	// Map<String, String> domainPackageNames, Lookups lookups,
	// String contextPackageName) {
	// this.domain = domain;
	// this.domainPackageNames = domainPackageNames;
	// this.contextPackageName = contextPackageName;
	// this.cls = cls;
	// this.lookups = lookups;
	// this.packageName = domainPackageNames.get(domain.getName());
	// }
	//
	// @Override
	// public String getContextPackageName() {
	// return contextPackageName;
	// }
	//
	// @Override
	// public String getPackage() {
	// return packageName;
	// }
	//
	// @Override
	// public String getClassDescription() {
	// return cls.getName() + " description";
	// }
	//
	// @Override
	// public List<List<String>> getUniqueConstraintColumnNames() {
	// List<List<String>> list = Lists.newArrayList();
	// Multimap<BigInteger, Attribute> ids = getIdentifiers();
	// for (BigInteger idNumber : ids.keySet()) {
	// Set<String> columnNames = Sets.newHashSet();
	// for (Attribute attribute : ids.get(idNumber)) {
	// columnNames.add(getColumnName(attribute));
	// }
	// list.add(new ArrayList<String>(columnNames));
	// }
	// return list;
	// }
	//
	// private String getColumnName(Attribute attribute) {
	// return getColumnName(attribute, cls, lookups);
	// }
	//
	// private static String getColumnName(Attribute attribute, Class cls,
	// Lookups lookups, Domain domain) {
	// if (attribute instanceof IndependentAttribute) {
	// IndependentAttribute a = (IndependentAttribute) attribute;
	// // TODO user override
	// return Util.toColumnName(a.getName());
	// } else if (attribute instanceof DerivedAttribute) {
	// DerivedAttribute a = (DerivedAttribute) attribute;
	// return Util.toColumnName(a.getName());
	// } else if (attribute instanceof ReferentialAttribute) {
	// ReferentialAttribute a = (ReferentialAttribute) attribute;
	// BigInteger relNumber = a.getReference().getValue()
	// .getRelationship();
	// Relationship rel = lookups.getRelationship(domain.getName(),
	// relNumber);
	// if (rel instanceof Association) {
	// Association ass = (Association) rel;
	// Class other = getOtherClass(lookups, cls, ass);
	// return getIdColumnName(other);
	// } else
	// return null;
	// } else
	// throw new RuntimeException("unexpected");
	// }
	//
	// @Override
	// public String getIdColumnName() {
	// return Util.toTableIdName(cls.getName());
	// }
	//
	// public static String getIdColumnName(Class cls) {
	// return Util.toTableIdName(cls.getName());
	// }
	//
	// private static Class getOtherClass(Lookups lookups, Class cls,
	// Association a) {
	// if (cls.getDomain().equals(a.getDomain())) {
	// if (cls.getName().equals(a.getClass1().getName()))
	// return lookups.getClass(a.getDomain(), a.getClass2().getName());
	// else
	// return lookups.getClass(a.getDomain(), a.getClass1().getName());
	// } else
	// throw new RuntimeException("unexpected");
	// }
	//
	// private AssociationEnd getOtherEnd(Class cls, Association a) {
	// if (cls.getDomain().equals(a.getDomain())) {
	// if (cls.getName().equals(a.getClass1().getName()))
	// return a.getClass2();
	// else
	// return a.getClass1();
	// } else
	// throw new RuntimeException("unexpected");
	// }
	//
	// private Multimap<BigInteger, Attribute> getIdentifiers() {
	// Multimap<BigInteger, Attribute> ids = HashMultimap.create();
	// for (JAXBElement<? extends Attribute> element : cls.getAttributeBase()) {
	// Attribute a = element.getValue();
	// for (Identifier id : a.getIdentifier()) {
	// ids.put(id.getNumber(), a);
	// }
	// }
	// return ids;
	// }
	//
	// @Override
	// public String getSchema() {
	// return Util.toTableName(cls.getDomain());
	// }
	//
	// @Override
	// public String getTable() {
	// return Util.toTableName(cls.getName());
	// }
	//
	// @Override
	// public String getJavaClassSimpleName() {
	// return Util.upperFirst(Util.toJavaIdentifier(cls.getName()));
	// }
	//
	// @Override
	// public List<String> getOperations() {
	// return Lists.newArrayList();
	// }
	//
	// @Override
	// public MyIndependentAttribute getPrimaryId() {
	// Collection<Attribute> attributes = getIdentifiers().get(BigInteger.ONE);
	// if (attributes.size() == 1) {
	// Attribute attribute = attributes.iterator().next();
	// String fieldName = Util.toJavaIdentifier(attribute.getName());
	// return new MyIndependentAttribute("id",
	// Util.toColumnName(fieldName), getType(cls, attribute),
	// false, null);
	// } else
	// // don't use embedded id but rather create an arbitrary id
	// return new MyIndependentAttribute("id", Util.toTableIdName(cls
	// .getName()), new Type(
	// toJavaType(IndependentAttributeType.ARBITRARY_ID)), false,
	// "");
	// }
	//
	// @Override
	// public List<MyIndependentAttribute> getNonIdIndependentAttributeMembers()
	// {
	//
	// List<MyIndependentAttribute> list = Lists.newArrayList();
	//
	// for (JAXBElement<? extends Attribute> element : cls.getAttributeBase()) {
	// Attribute attribute = element.getValue();
	// if (attribute instanceof IndependentAttribute) {
	// IndependentAttribute a = (IndependentAttribute) attribute;
	// boolean inPrimaryIdentifier = false;
	// for (Identifier id : attribute.getIdentifier())
	// if (id.getNumber().equals(BigInteger.ONE))
	// inPrimaryIdentifier = true;
	// // if primary id has more than one field then arbitrary id is
	// // used as the id
	// if (getIdentifiers().get(BigInteger.ONE).size() > 1)
	// inPrimaryIdentifier = false;
	// if (!inPrimaryIdentifier) {
	// list.add(new MyIndependentAttribute(
	// toJavaIdentifier(attribute.getName()),
	// toColumnName(attribute.getName()), getType(cls,
	// attribute), !a.isMandatory(),
	// "attribute description here"));
	// }
	// }
	// }
	//
	// return list;
	// }
	//
	// @Override
	// public List<MyEvent> getEvents() {
	// List<MyEvent> list = Lists.newArrayList();
	// for (Event event : cls.getEvent()) {
	// List<MyParameter> params = Lists.newArrayList();
	// for (Parameter p : event.getParameter()) {
	// params.add(new MyParameter(toJavaIdentifier(p.getName()), p
	// .getType()));
	// }
	// list.add(new MyEvent(event.getName(),
	// upperFirst(toJavaIdentifier(event.getName())), params));
	// }
	// return list;
	// }
	//
	// @Override
	// public List<String> getStateNames() {
	// List<String> list = Lists.newArrayList();
	// for (State state : cls.getState()) {
	// list.add(state.getName());
	// }
	// return list;
	// }
	//
	// @Override
	// public List<MyTransition> getTransitions() {
	// List<MyTransition> list = Lists.newArrayList();
	// for (Transition t : cls.getTransition()) {
	// list.add(new MyTransition(t.getEvent(), t.getFrom(), t.getTo()));
	// }
	// return list;
	// }
	//
	// @Override
	// public String getStateIdentifier(String state) {
	// return toJavaConstantIdentifier(state);
	// }
	//
	// @Override
	// public boolean isSuperclass() {
	//
	// for (JAXBElement<? extends Relationship> element : lookups.getSystem()
	// .getRelationshipBase()) {
	// Relationship rel = element.getValue();
	// if (rel instanceof Generalization) {
	// Generalization g = (Generalization) rel;
	// if (g.getDomain().equals(cls.getDomain())
	// && g.getSuperclass().equals(cls.getName()))
	// return true;
	// }
	// }
	// return false;
	// }
	//
	// @Override
	// public boolean isSubclass() {
	// for (JAXBElement<? extends Relationship> element : lookups.getSystem()
	// .getRelationshipBase()) {
	// Relationship rel = element.getValue();
	// if (rel instanceof Generalization) {
	// Generalization g = (Generalization) rel;
	// if (g.getDomain().equals(cls.getDomain())
	// && g.getSubclass().equals(cls.getName()))
	// return true;
	// }
	// }
	// return false;
	// }
	//
	// @Override
	// public MySubclassRole getSubclassRole() {
	// for (JAXBElement<? extends Relationship> element : lookups.getSystem()
	// .getRelationshipBase()) {
	// Relationship rel = element.getValue();
	// if (rel instanceof Generalization) {
	// Generalization g = (Generalization) rel;
	// if (cls.getDomain().equals(g.getDomain())
	// && cls.getName().equals(g.getSubclass())) {
	// return new MySubclassRole(g.getSuperclass(),
	// "discriminator");
	// }
	// }
	// }
	// return null;
	// }
	//
	// @Override
	// public List<MyReferenceMember> getReferenceMembers() {
	// List<MyReferenceMember> list = Lists.newArrayList();
	// for (JAXBElement<? extends Relationship> element : lookups.getSystem()
	// .getRelationshipBase()) {
	// Relationship rel = element.getValue();
	// if (rel instanceof Generalization) {
	// list.addAll(getReferenceMembersFromGeneralizations(rel));
	// } else if (rel instanceof Association) {
	// list.addAll(getReferenceMembersFromAssociation(rel));
	// }
	// }
	// return list;
	// }
	//
	// private List<MyReferenceMember> getReferenceMembersFromAssociation(
	// Relationship rel) {
	// List<MyReferenceMember> list = Lists.newArrayList();
	// Association ass = (Association) rel;
	// if (cls.getDomain().equals(ass.getDomain())
	// && cls.getName().equals(ass.getClass1().getName())
	// || cls.getName().equals(ass.getClass2().getName())) {
	// AssociationEnd thisEnd;
	// AssociationEnd otherEnd;
	// if (cls.getName().equals(ass.getClass1().getName())) {
	// thisEnd = ass.getClass1();
	// otherEnd = ass.getClass2();
	// } else {
	// thisEnd = ass.getClass2();
	// otherEnd = ass.getClass1();
	// }
	// Class other = lookups.getClass(ass.getDomain(), otherEnd.getName());
	// ClassInfo otherInfo = createClassInfo(other);
	// String fieldName = Util.lowerFirst(otherInfo
	// .getJavaClassSimpleName()) + "ViaR" + ass.getNumber();
	// String otherFieldName = Util.lowerFirst(otherInfo
	// .getJavaClassSimpleName()) + "ViaR" + ass.getNumber();
	// String otherColumnName = Util.toColumnName(otherInfo
	// .getJavaClassSimpleName() + "ViaR" + ass.getNumber());
	//
	// Class assoc = lookups.getAssociationClassForAssociation(rel
	// .getNumber());
	// MyManyToMany manyToMany;
	// if (assoc != null) {
	// ClassInfo ai = createClassInfo(assoc);
	// manyToMany = new MyManyToMany(
	// Util.toTableName(assoc.getName()), ai.getSchema(),
	// Util.toColumnName(fieldName), otherColumnName);
	// } else
	// manyToMany = null;
	// MyReferenceMember ref = new MyReferenceMember(
	// otherInfo.getJavaClassSimpleName(),
	// otherInfo.getClassFullName(),
	// convert(thisEnd.getMultiplicity()),
	// convert(otherEnd.getMultiplicity()),
	// thisEnd.getVerbClause(), otherEnd.getVerbClause(),
	// fieldName, otherColumnName, fieldName, otherFieldName,
	// manyToMany);
	// list.add(ref);
	// }
	// return list;
	// }
	//
	// private Mult convert(Multiplicity m) {
	// if (m.equals(Multiplicity.ZERO_ONE))
	// return Mult.ZERO_ONE;
	// else if (m.equals(Multiplicity.ONE))
	// return Mult.ONE;
	// else if (m.equals(Multiplicity.ONE_MANY))
	// return Mult.ONE_MANY;
	// else if (m.equals(Multiplicity.MANY))
	// return Mult.MANY;
	// else
	// throw new RuntimeException("unexpected");
	// }
	//
	// private List<MyReferenceMember> getReferenceMembersFromGeneralizations(
	// Relationship rel) {
	// List<MyReferenceMember> list = Lists.newArrayList();
	// Generalization g = (Generalization) rel;
	// if (cls.getDomain().equals(g.getDomain())
	// && cls.getName().equals(g.getSuperclass())) {
	// Class subclass = lookups.getClass(g.getDomain(), g.getSubclass());
	// ClassInfo si = createClassInfo(subclass);
	// String fieldName = Util.lowerFirst(si.getJavaClassSimpleName())
	// + "ViaR" + g.getNumber();
	// MyReferenceMember ref = new MyReferenceMember(
	// si.getJavaClassSimpleName(), si.getClassFullName(),
	// Mult.ONE, Mult.ZERO_ONE, "has generalization",
	// "has specialization", fieldName, null, fieldName, null,
	// null);
	// list.add(ref);
	// }
	// if (cls.getDomain().equals(g.getDomain())
	// && cls.getName().equals(g.getSubclass())) {
	// Class superclass = lookups.getClass(g.getDomain(),
	// g.getSuperclass());
	// ClassInfo si = createClassInfo(superclass);
	// String fieldName = Util.lowerFirst(si.getJavaClassSimpleName())
	// + "R" + g.getNumber();
	// MyReferenceMember ref = new MyReferenceMember(
	// si.getJavaClassSimpleName(), si.getClassFullName(),
	// Mult.ZERO_ONE, Mult.ONE, "has specialization",
	// "has generalization", fieldName,
	// Util.toColumnName(fieldName), fieldName, null, null);
	// list.add(ref);
	// }
	// return list;
	// }
	//
	// private ClassInfo createClassInfo(Class cls) {
	// return new ClassInfoFromJaxb(cls, domainPackageNames, lookups,
	// contextPackageName);
	// }
	//
	// @Override
	// public Set<String> getAtLeastOneFieldChecks() {
	// // TODO implement getAtLeastOneFieldChecks
	// return Sets.newHashSet();
	// }
	//
	// @Override
	// public String getImports() {
	// return types.getImports();
	// }
	//
	// private Type getType(Class cls, Attribute a) {
	// MyAttributeType t = getMyAttributeType(cls, a);
	// if (t.multiple)
	// return new Type(Set.class.getName(), new Type(toJavaType(t.type)));
	// return new Type(toJavaType(t.type));
	// }
	//
	// private static class MyAttributeType {
	// IndependentAttributeType type;
	// boolean multiple;
	//
	// public MyAttributeType(IndependentAttributeType type, boolean multiple) {
	// super();
	// this.type = type;
	// this.multiple = multiple;
	// }
	// }
	//
	// private void log(String message, Object... objects) {
	// java.lang.System.out.format(message + "\n", objects);
	// }
	//
	// /**
	// * Recursively travels relationship paths and returns the type of a
	// referred
	// * attribute.
	// *
	// * @param cls
	// * @param a
	// * @return
	// */
	// private MyAttributeType getMyAttributeType(Class cls, Attribute a) {
	// log("getting type of %s.%s", cls.getName(), a.getName());
	//
	// if (a instanceof IndependentAttribute) {
	// return new MyAttributeType(((IndependentAttribute) a).getType(),
	// false);
	// } else if (a instanceof DerivedAttribute) {
	// return new MyAttributeType(((DerivedAttribute) a).getType(), false);
	// } else if (a instanceof ReferentialAttribute) {
	// ReferentialAttribute r = (ReferentialAttribute) a;
	// Reference ref = r.getReferenceBase().getValue();
	// if (ref instanceof ToOneReference) {
	// ToOneReference t = (ToOneReference) ref;
	// MyAttributeType result = getToOneReferenceType(cls, a, r, t);
	// return result;
	// } else if (ref instanceof SuperclassReference) {
	// SuperclassReference t = (SuperclassReference) ref;
	// MyAttributeType otherType = getGeneralizationType(cls, a, r, t);
	// return otherType;
	// } else if (ref instanceof AssociativeReference) {
	// AssociativeReference t = (AssociativeReference) ref;
	// MyAttributeType otherType = getAssociativeType(cls, a, r, t);
	// return otherType;
	// } else {
	// throw new RuntimeException(UNEXPECTED);
	// }
	// } else
	// throw new RuntimeException("unexpected attribute type " + a);
	// }
	//
	// private MyAttributeType getAssociativeType(Class cls, Attribute a,
	// ReferentialAttribute r, AssociativeReference t) {
	// Association ass = lookups.getAssociation(cls.getDomain(),
	// t.getRelationship());
	// Class other;
	// if (t.getSide().intValue() == 1)
	// other = lookups
	// .getClass(ass.getDomain(), ass.getClass1().getName());
	// else
	// other = lookups
	// .getClass(ass.getDomain(), ass.getClass2().getName());
	// String otherName;
	// if (r.getOtherName() == null)
	// otherName = r.getName();
	// else
	// otherName = r.getOtherName();
	// log("looking up attribute %s %s %s", other.getDomain(),
	// other.getName(), otherName);
	// Attribute otherAttribute = lookups.getAttribute(other.getDomain(),
	// other.getName(), otherName);
	// MyAttributeType otherType = getMyAttributeType(other, otherAttribute);
	// return otherType;
	// }
	//
	// private MyAttributeType getGeneralizationType(Class cls, Attribute a,
	// ReferentialAttribute r, SuperclassReference t) {
	// Generalization g = lookups.getGeneralization(cls.getDomain(),
	// t.getRelationship());
	// if (g == null)
	// throw new RuntimeException("did not find association");
	// Class other = getOtherClass(g);
	// String otherName;
	// if (r.getOtherName() == null)
	// otherName = r.getName();
	// else
	// otherName = r.getOtherName();
	// log("looking up attribute %s %s %s", other.getDomain(),
	// other.getName(), otherName);
	// Attribute otherAttribute = lookups.getAttribute(other.getDomain(),
	// other.getName(), otherName);
	// MyAttributeType otherType = getMyAttributeType(other, otherAttribute);
	// return otherType;
	// }
	//
	// private Class getOtherClass(Generalization g) {
	// return lookups.getClass(g.getDomain(), g.getSuperclass());
	// }
	//
	// private MyAttributeType getToOneReferenceType(Class cls, Attribute a,
	// ReferentialAttribute r, ToOneReference t) {
	// Association ass = lookups.getAssociation(cls.getDomain(),
	// t.getRelationship());
	// if (ass == null)
	// throw new RuntimeException("did not find association");
	// Class other = getOtherClass(cls, ass);
	// AssociationEnd otherEnd = getOtherEnd(cls, ass);
	// String otherName = r.getOtherName();
	// if (otherName == null)
	// otherName = a.getName();
	// log("looking up attribute %s %s %s", other.getDomain(),
	// other.getName(), otherName);
	// Attribute otherAttribute = lookups.getAttribute(other.getDomain(),
	// other.getName(), otherName);
	// MyAttributeType otherType = getMyAttributeType(other, otherAttribute);
	// MyAttributeType result;
	// if (otherEnd.getMultiplicity().equals(Multiplicity.MANY)
	// || otherEnd.getMultiplicity().equals(Multiplicity.ONE_MANY)) {
	// result = new MyAttributeType(otherType.type, true);
	// } else
	// result = otherType;
	// return result;
	// }
	//
	// private Class getOtherClass(Class cls, Association a) {
	// if (cls.getDomain().equals(a.getDomain())) {
	// if (cls.getName().equals(a.getClass1().getName()))
	// return lookups.getClass(a.getDomain(), a.getClass2().getName());
	// else
	// return lookups.getClass(a.getDomain(), a.getClass1().getName());
	// } else
	// throw new RuntimeException(UNEXPECTED);
	// }
	//
	// private static String toJavaType(IndependentAttributeType type) {
	// if (type.equals(IndependentAttributeType.ARBITRARY_ID)) {
	// return "Long";
	// } else if (type.equals(IndependentAttributeType.BOOLEAN)) {
	// return "Boolean";
	// } else if (type.equals(IndependentAttributeType.DATE)) {
	// return "String";
	// } else if (type.equals(IndependentAttributeType.DECIMAL)) {
	// return "BigDecimal";
	// } else if (type.equals(IndependentAttributeType.INTEGER)) {
	// return "BigInteger";
	// } else if (type.equals(IndependentAttributeType.STRING)) {
	// return "String";
	// } else if (type.equals(IndependentAttributeType.TIMESTAMP)) {
	// return "java.util.Date";
	// } else
	// throw new RuntimeException("no java type implemented for " + type);
	// }

}
