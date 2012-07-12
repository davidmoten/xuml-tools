package xuml.tools.model.compiler;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.HashMultimap.create;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import miuml.jaxb.Association;
import miuml.jaxb.AsymmetricPerspective;
import miuml.jaxb.AtomicType;
import miuml.jaxb.Attribute;
import miuml.jaxb.BinaryAssociation;
import miuml.jaxb.BooleanType;
import miuml.jaxb.Class;
import miuml.jaxb.CreationEvent;
import miuml.jaxb.EnumeratedType;
import miuml.jaxb.Event;
import miuml.jaxb.Generalization;
import miuml.jaxb.IdentifierAttribute;
import miuml.jaxb.IndependentAttribute;
import miuml.jaxb.IntegerType;
import miuml.jaxb.Named;
import miuml.jaxb.NativeAttribute;
import miuml.jaxb.Perspective;
import miuml.jaxb.RealType;
import miuml.jaxb.Reference;
import miuml.jaxb.ReferentialAttribute;
import miuml.jaxb.Relationship;
import miuml.jaxb.SpecializationReference;
import miuml.jaxb.State;
import miuml.jaxb.StateModelParameter;
import miuml.jaxb.StateModelSignature;
import miuml.jaxb.SymbolicType;
import miuml.jaxb.SymmetricPerspective;
import miuml.jaxb.Transition;
import miuml.jaxb.UnaryAssociation;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ClassInfo extends ClassInfoBase {

	private final Class cls;
	private final String packageName;
	private final String classDescription;
	private final String schema;
	private final TypeRegister typeRegister = new TypeRegister();
	private final Lookups lookups;
	private static NameManager nameManager = NameManager.getInstance();

	public ClassInfo(Class cls, String packageName, String classDescription,
			String schema, Lookups lookups) {
		this.cls = cls;
		this.packageName = packageName;
		// TODO is this property needed?
		this.classDescription = classDescription;
		this.schema = schema;
		this.lookups = lookups;
	}

	@Override
	String getPackage() {
		return packageName;
	}

	@Override
	String getClassDescription() {
		return classDescription;
	}

	@Override
	List<List<String>> getUniqueConstraintColumnNames() {
		HashMultimap<BigInteger, String> map = getIdentifierAttributeNames();
		List<List<String>> list = newArrayList();
		for (BigInteger i : map.keySet()) {
			if (!i.equals(BigInteger.ONE)) {
				List<String> cols = newArrayList();
				for (String attribute : map.get(i))
					cols.add(nameManager.toColumnName(cls.getName(), attribute));
				list.add(cols);
			}
		}
		return list;
	}

	private HashMultimap<BigInteger, String> getIdentifierAttributeNames() {
		HashMultimap<BigInteger, Attribute> map = getIdentifierAttributes();
		HashMultimap<BigInteger, String> m = create();
		for (BigInteger i : map.keySet()) {
			m.putAll(i, getNames(map.get(i)));
		}
		return m;
	}

	private static Function<Attribute, String> attributeName = new Function<Attribute, String>() {
		@Override
		public String apply(Attribute a) {
			return a.getName();
		}
	};

	private Set<String> getNames(Set<Attribute> attributes) {
		return newHashSet(transform(attributes, attributeName));
	}

	private HashMultimap<BigInteger, Attribute> getIdentifierAttributes() {
		HashMultimap<BigInteger, Attribute> map = HashMultimap.create();
		for (JAXBElement<? extends Attribute> element : cls.getAttribute()) {
			Attribute attribute = element.getValue();
			for (IdentifierAttribute id : attribute.getIdentifier()) {
				map.put(id.getNumber(), attribute);
			}
		}
		return map;
	}

	@Override
	String getSchema() {
		return schema;
	}

	@Override
	String getTable() {
		return nameManager.toTableName(schema, cls.getName());
	}

	@Override
	String getJavaClassSimpleName() {
		return Util.toClassSimpleName(cls.getName());
	}

	@Override
	List<MyIdAttribute> getPrimaryIdAttributeMembers() {
		Set<Attribute> list = getIdentifierAttributes().get(BigInteger.ONE);
		return getMyIdAttributes(list);
	}

	private List<MyIdAttribute> getMyIdAttributes(Set<Attribute> list) {
		List<MyIdAttribute> result = newArrayList();
		for (Attribute attribute : list) {
			MyIdAttribute id;
			if (attribute instanceof NativeAttribute) {
				NativeAttribute a = (NativeAttribute) attribute;
				id = createMyIdAttribute(a);
			} else {
				ReferentialAttribute a = (ReferentialAttribute) attribute;
				id = createMyIdAttribute(a);
			}
			result.add(id);
		}
		return result;
	}

	private MyIdAttribute createMyIdAttribute(ReferentialAttribute a) {
		Reference ref = a.getReference().getValue();
		Relationship rel = lookups.getRelationship(ref.getRelationship());
		String otherClassName = getOtherClassName(rel);
		return getPrimaryIdAttribute(a, ref, otherClassName);
	}

	private String getOtherClassName(Relationship rel) {
		String otherClassName;
		if (rel instanceof BinaryAssociation) {
			BinaryAssociation b = (BinaryAssociation) rel;
			if (isActiveSide(b))
				otherClassName = b.getPassivePerspective().getViewedClass();
			else
				otherClassName = b.getActivePerspective().getViewedClass();
		} else if (rel instanceof UnaryAssociation) {
			// TODO
			throw new RuntimeException("not sure how to do this one yet");
		} else if (rel instanceof Generalization) {
			Generalization g = (Generalization) rel;
			if (cls.getName().equals(g.getSuperclass()))
				throw new RuntimeException(
						"cannot use an id from a specialization as primary id member: "
								+ g.getRnum());
			else
				otherClassName = g.getSuperclass();
		} else
			throw new RuntimeException(
					"this relationship type not implemented: "
							+ rel.getClass().getName());
		return otherClassName;
	}

	private MyIdAttribute getPrimaryIdAttribute(ReferentialAttribute a,
			Reference ref, String otherClassName) {
		MyIdAttribute p = getOtherPrimaryIdAttribute(a, ref, otherClassName);
		if (p != null)
			return new MyIdAttribute(a.getName(), nameManager.toFieldName(
					cls.getName(), a.getName()), nameManager.toColumnName(
					cls.getName(), a.getName()), otherClassName,
					nameManager.toColumnName(otherClassName,
							p.getAttributeName()), p.getType());
		else
			throw new RuntimeException("attribute not found!");
	}

	private MyIdAttribute getOtherPrimaryIdAttribute(ReferentialAttribute a,
			Reference ref, String otherClassName) {
		ClassInfo otherInfo = getClassInfo(otherClassName);
		// look for attribute
		String otherAttributeName;
		if (ref.getAttribute() == null)
			otherAttributeName = a.getName();
		else
			otherAttributeName = ref.getAttribute();
		List<MyIdAttribute> members = otherInfo.getPrimaryIdAttributeMembers();
		for (MyIdAttribute p : members) {
			if (otherAttributeName.equals(p.getAttributeName())) {
				return p;
			}
		}
		// not found
		throw new RuntimeException("could not find attribute <"
				+ ref.getAttribute() + " in class " + otherClassName);

	}

	private ClassInfo getClassInfo(String otherClassName) {
		ClassInfo otherInfo = new ClassInfo(
				lookups.getClassByName(otherClassName), packageName, "unknown",
				schema, lookups);
		return otherInfo;
	}

	private boolean isActiveSide(BinaryAssociation b) {
		return b.getActivePerspective().getViewedClass().equals(cls.getName());
	}

	private MyIdAttribute createMyIdAttribute(NativeAttribute a) {
		return new MyIdAttribute(a.getName(),
				Util.toJavaIdentifier(a.getName()), Util.toColumnName(a
						.getName()), getTypeDefinition(a.getType()));
	}

	private MyIndependentAttribute createMyIndependentAttribute(
			NativeAttribute a) {

		boolean inIdentifier = false;
		for (Attribute attribute : getIdentifierAttributes().values()) {
			if (a.getName().equals(attribute.getName()))
				inIdentifier = true;
		}
		boolean isNullable = !inIdentifier;

		return new MyIndependentAttribute(Util.toJavaIdentifier(a.getName()),
				Util.toColumnName(a.getName()), getTypeDefinition(a.getType()),
				isNullable, "description");
	}

	@Override
	List<MyIndependentAttribute> getNonIdIndependentAttributeMembers() {
		List<MyIndependentAttribute> list = newArrayList();
		for (JAXBElement<? extends Attribute> element : cls.getAttribute()) {
			if (element.getValue() instanceof IndependentAttribute) {
				IndependentAttribute a = (IndependentAttribute) element
						.getValue();
				if (!isMemberOfPrimaryIdentifier(a)) {
					list.add(createMyIndependentAttribute(a));
				}
			}
		}
		return list;
	}

	private boolean isMemberOfPrimaryIdentifier(IndependentAttribute a) {
		for (IdentifierAttribute idAttribute : a.getIdentifier()) {
			if (idAttribute.getNumber().intValue() == 1) {
				return true;
			}
		}
		return false;
	}

	@Override
	List<MyEvent> getEvents() {
		if (cls.getLifecycle() == null)
			return newArrayList();
		List<MyEvent> list = newArrayList();
		CreationEvent creationEvent = getCreationEvent();
		for (JAXBElement<? extends Event> element : cls.getLifecycle()
				.getEvent()) {
			Event event = element.getValue();

			final StateModelSignature signature;
			final String stateName;

			if (event.getEventSignature() != null) {
				signature = event.getEventSignature();
				stateName = null;
			} else {
				// TODO of eventSignature is null then get signature from
				// destination state
				State destinationState = null;

				for (MyTransition transition : getTransitions()) {
					if (transition.getEventId()
							.equals(event.getID().toString())) {
						for (State state : cls.getLifecycle().getState()) {
							if (transition.getToState().equals(state.getName())) {
								destinationState = state;
							}
						}
					}
				}
				if (destinationState != null) {
					signature = destinationState.getStateSignature();
					stateName = destinationState.getName();
				} else {
					signature = null;
					stateName = null;
				}
			}

			if (signature == null)
				throw new RuntimeException(
						"event/state signature not found for " + cls.getName()
								+ ",event=" + event.getName());

			List<MyParameter> parameters = Lists.newArrayList();
			for (StateModelParameter p : signature.getStateModelParameter()) {
				parameters.add(new MyParameter(Util.toJavaIdentifier(p
						.getName()), lookups.getJavaType(p.getType())));
			}

			MyEvent myEvent = new MyEvent(event.getName(),
					Util.toClassSimpleName(event.getName()), parameters,
					stateName, getStateSignatureInterfaceName(stateName),
					event == creationEvent);
			list.add(myEvent);
		}
		return list;
	}

	private String getStateSignatureInterfaceName(final String stateName) {
		if (stateName == null)
			return null;
		else
			return "StateSignature_"
					+ Util.upperFirst(Util.toJavaIdentifier(stateName));
	}

	@Override
	List<String> getStateNames() {
		List<String> list = Lists.newArrayList();
		if (cls.getLifecycle() == null)
			return newArrayList();
		else {
			for (State state : cls.getLifecycle().getState())
				list.add(state.getName());
			return list;
		}
	}

	@Override
	List<MyTransition> getTransitions() {
		List<MyTransition> list = Lists.newArrayList();
		for (Transition transition : cls.getLifecycle().getTransition()) {
			// TODO what to do about event name? Event inheritance is involved.
			String eventName = getEventName(transition.getEventID());
			list.add(new MyTransition(eventName, Util
					.toClassSimpleName(eventName), transition.getEventID()
					.toString(), transition.getState(), transition
					.getDestination()));

		}
		CreationEvent creation = getCreationEvent();
		if (creation != null) {
			String eventName = getEventName(creation.getID());
			list.add(new MyTransition(eventName, Util
					.toClassSimpleName(eventName), creation.getID().toString(),
					null, creation.getState()));
		}
		return list;
	}

	private CreationEvent getCreationEvent() {
		for (JAXBElement<? extends Event> element : cls.getLifecycle()
				.getEvent()) {
			if (element.getValue() instanceof CreationEvent)
				return (CreationEvent) element.getValue();
		}
		return null;
	}

	private String getEventName(BigInteger eventId) {
		for (JAXBElement<? extends Event> ev : cls.getLifecycle().getEvent()) {
			if (ev.getValue().getID().equals(eventId))
				return ev.getValue().getName();
		}
		return null;
	}

	@Override
	String getStateAsJavaIdentifier(String stateName) {
		for (State state : cls.getLifecycle().getState())
			if (state.getName().equals(stateName))
				// TODO use nameManager
				return Util.toJavaConstantIdentifier(stateName);
		throw new RuntimeException("state not found: " + stateName);
	}

	@Override
	boolean isSuperclass() {
		return lookups.isSuperclass(cls.getName());
	}

	@Override
	boolean isSubclass() {
		return lookups.isSpecialization(cls.getName());
	}

	@Override
	MySubclassRole getSubclassRole() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	List<MyReferenceMember> getReferenceMembers() {

		List<MyReferenceMember> list = Lists.newArrayList();
		List<Association> associations = lookups.getAssociations(cls);
		for (Association a : associations) {
			MyReferenceMember m = createMyReferenceMember(a, cls);
			list.add(m);
		}
		for (Generalization g : lookups.getGeneralizations()) {
			for (Named specialization : g.getSpecializedClass()) {
				if (g.getSuperclass().equals(cls.getName()))
					list.add(createMyReferenceMember(g, specialization, cls,
							true));
				else if (specialization.getName().equals(cls.getName()))
					list.add(createMyReferenceMember(g, specialization, cls,
							false));
			}
		}
		return list;
	}

	private MyReferenceMember createMyReferenceMember(Generalization g,
			Named spec, Class cls, boolean isSuperclass) {
		if (isSuperclass) {
			ClassInfo infoOther = getClassInfo(spec.getName());
			String fieldName = nameManager.toFieldName(cls.getName(),
					spec.getName(), g.getRnum());
			String thisFieldName = nameManager.toFieldName(spec.getName(),
					cls.getName(), g.getRnum());
			return new MyReferenceMember(spec.getName(),
					infoOther.getClassFullName(), Mult.ONE, Mult.ZERO_ONE,
					"specializes", "generalizes", fieldName, null,
					thisFieldName, (MyManyToMany) null, false);
		} else {
			ClassInfo infoOther = getClassInfo(g.getSuperclass());
			String fieldName = nameManager.toFieldName(cls.getName(),
					g.getSuperclass(), g.getRnum());
			String thisFieldName = nameManager.toFieldName(g.getSuperclass(),
					cls.getName(), g.getRnum());
			List<JoinColumn> joins = newArrayList();
			for (MyIdAttribute member : infoOther
					.getPrimaryIdAttributeMembers()) {
				// TODO handle when matching attribute not found, use some
				// default, see schema
				String attributeName = getMatchingAttributeName(g.getRnum(),
						member.getAttributeName());
				JoinColumn jc = new JoinColumn(nameManager.toColumnName(
						g.getSuperclass(), attributeName),
						member.getColumnName());
				System.out.println(jc);
				joins.add(jc);
			}
			return new MyReferenceMember(g.getSuperclass(),
					infoOther.getClassFullName(), Mult.ZERO_ONE, Mult.ONE,
					"generalizes", "specializes", fieldName, joins,
					thisFieldName, (MyManyToMany) null, false);
		}
	}

	private MyReferenceMember createMyReferenceMember(Association a, Class cls) {
		if (a instanceof BinaryAssociation)
			return createMyReferenceMember((BinaryAssociation) a, cls);
		else
			return createMyReferenceMember((UnaryAssociation) a, cls);
	}

	private MyReferenceMember createMyReferenceMember(UnaryAssociation a,
			Class cls) {
		SymmetricPerspective p = a.getSymmetricPerspective();
		String fieldName = nameManager.toFieldName(cls.getName(),
				p.getPhrase(), a.getRnum());
		List<JoinColumn> joins = newArrayList();
		if (p.isOnePerspective())
			for (MyIdAttribute member : getPrimaryIdAttributeMembers()) {
				String attributeName = member.getAttributeName() + " R"
						+ a.getRnum();
				JoinColumn jc = new JoinColumn(nameManager.toColumnName(
						cls.getName(), attributeName), member.getColumnName());
				System.out.println(jc);
				joins.add(jc);
			}

		return new MyReferenceMember(getJavaClassSimpleName(),
				getClassFullName(), Mult.ONE, toMult(p), "inverse of"
						+ p.getPhrase(), p.getPhrase(), fieldName, joins,
				"this", null, false);
	}

	private MyReferenceMember createMyReferenceMember(BinaryAssociation a,
			Class cls) {
		AsymmetricPerspective pThis;
		AsymmetricPerspective pThat;

		if (a.getActivePerspective().getViewedClass().equals(cls.getName())) {
			pThis = a.getActivePerspective();
			pThat = a.getPassivePerspective();
		} else {
			pThis = a.getPassivePerspective();
			pThat = a.getActivePerspective();
		}
		String otherClassName = pThat.getViewedClass();
		ClassInfo infoOther = getClassInfo(otherClassName);
		List<JoinColumn> joins = newArrayList();
		if (pThat.isOnePerspective())
			for (MyIdAttribute member : infoOther
					.getPrimaryIdAttributeMembers()) {
				String attributeName = getMatchingAttributeName(a.getRnum(),
						member.getAttributeName());
				JoinColumn jc = new JoinColumn(nameManager.toColumnName(
						cls.getName(), attributeName), member.getColumnName());
				System.out.println(jc);
				joins.add(jc);
			}

		String fieldName = nameManager.toFieldName(cls.getName(),
				pThat.getViewedClass(), a.getRnum());
		// now establish the name of the field for this class as seen in the
		// other class
		String thisFieldName = nameManager.toFieldName(otherClassName,
				cls.getName(), a.getRnum());
		boolean inPrimaryId = inPrimaryId(a.getRnum());
		return new MyReferenceMember(pThat.getViewedClass(),
				infoOther.getClassFullName(), toMult(pThis), toMult(pThat),
				pThis.getPhrase(), pThat.getPhrase(), fieldName, joins,
				thisFieldName, (MyManyToMany) null, inPrimaryId);
	}

	private boolean inPrimaryId(BigInteger rnum) {
		for (JAXBElement<? extends Attribute> element : cls.getAttribute()) {
			Attribute a = element.getValue();
			if (a instanceof ReferentialAttribute) {
				ReferentialAttribute r = (ReferentialAttribute) a;
				if (r.getReference().getValue().getRelationship().equals(rnum))
					for (IdentifierAttribute ia : r.getIdentifier()) {
						if (ia.getNumber().equals(BigInteger.ONE))
							return true;
					}
			}
		}
		return false;
	}

	private String getMatchingAttributeName(BigInteger rNum,
			String otherAttributeName) {
		for (JAXBElement<? extends Attribute> element : cls.getAttribute()) {
			Attribute a = element.getValue();
			if (a instanceof ReferentialAttribute) {
				ReferentialAttribute r = (ReferentialAttribute) a;
				if (r.getReference().getValue().getRelationship().equals(rNum)
						&& r.getReference().getValue().getAttribute()
								.equals(otherAttributeName))
					return r.getName();
			}
		}
		throw new RuntimeException("could not find matching attribute "
				+ cls.getName() + " R" + rNum + " " + otherAttributeName);
	}

	private static Mult toMult(Perspective p) {
		if (p.isConditional() && p.isOnePerspective())
			return Mult.ZERO_ONE;
		else if (p.isConditional() && !p.isOnePerspective())
			return Mult.MANY;
		else if (p.isOnePerspective())
			return Mult.ONE;
		else
			return Mult.ONE_MANY;
	}

	@Override
	Set<String> getAtLeastOneFieldChecks() {
		// TODO Auto-generated method stub
		return Sets.newHashSet();
	}

	@Override
	String getImports(String relativeToClass) {
		return getTypes().getImports(relativeToClass);
	}

	@Override
	String getIdColumnName() {
		// TODO Auto-generated method stub
		return "ID";
	}

	@Override
	String getContextPackageName() {
		// TODO Auto-generated method stub
		return packageName;
	}

	@Override
	TypeRegister getTypes() {
		return typeRegister;
	}

	@Override
	Type getType(String name) {
		String javaClassName = lookups.getJavaType(name);
		return new Type(javaClassName);
	}

	public List<MySpecializations> getSpecializations() {
		List<MySpecializations> list = Lists.newArrayList();

		for (Generalization g : lookups.getGeneralizations()) {
			if (g.getSuperclass().equals(cls.getName())) {
				Set<String> fieldNames = Sets.newHashSet();
				for (Named spec : g.getSpecializedClass()) {
					// get the attribute name
					String attributeName = null;
					for (JAXBElement<? extends Attribute> element : cls
							.getAttribute()) {
						if (element.getValue() instanceof ReferentialAttribute) {
							ReferentialAttribute r = (ReferentialAttribute) element
									.getValue();
							Reference ref = r.getReference().getValue();
							if (ref instanceof SpecializationReference) {
								if (ref.getRelationship().equals(g.getRnum()))
									attributeName = r.getName();
							}
						}
					}
					if (attributeName == null)
						throw new RuntimeException(
								"could not find attribute name for generalization "
										+ g.getRnum() + ", specialization "
										+ spec.getName());
					String fieldName = nameManager.toFieldName(cls.getName(),
							spec.getName(), g.getRnum());
					fieldNames.add(fieldName);
				}
				list.add(new MySpecializations(g.getRnum(), fieldNames));
			}
		}
		return list;
	}

	public MyTypeDefinition getTypeDefinition(String name) {
		AtomicType t = lookups.getAtomicType(name);
		if (t instanceof SymbolicType)
			return getTypeDefinition((SymbolicType) t);
		else if (t instanceof BooleanType)
			return getTypeDefinition((BooleanType) t);
		else if (t instanceof EnumeratedType)
			return getTypeDefinition((EnumeratedType) t);
		else if (t instanceof IntegerType)
			return getTypeDefinition((IntegerType) t);
		else if (t instanceof RealType)
			return getTypeDefinition((RealType) t);
		else
			throw new RuntimeException("unexpected");
	}

	private MyTypeDefinition getTypeDefinition(RealType t) {
		return new MyTypeDefinition(t.getName(), MyType.REAL, new Type(
				Double.class), t.getUnits(), t.getPrecision(),
				t.getLowerLimit(), t.getUpperLimit(), t.getDefaultValue() + "",
				null, null, null, null, null, null);
	}

	private MyTypeDefinition getTypeDefinition(IntegerType t) {
		MyType myType;
		Type type;

		if ("date".equals(t.getName())) {
			myType = MyType.DATE;
			type = new Type(Date.class);
		} else if ("timestamp".equals(t.getName())) {
			myType = MyType.TIMESTAMP;
			type = new Type(Date.class);
		} else {
			myType = MyType.INTEGER;
			type = new Type(Integer.class);
		}

		return new MyTypeDefinition(t.getName(), myType, type, t.getUnits(),
				null, toBigDecimal(t.getLowerLimit()),
				toBigDecimal(t.getUpperLimit()), toString(t.getDefaultValue()),
				null, null, null, null, null, null);
	}

	private static BigDecimal toBigDecimal(BigInteger n) {
		if (n == null)
			return null;
		else
			return new BigDecimal(n);
	}

	private static String toString(BigInteger n) {
		if (n == null)
			return null;
		else
			return n.toString();
	}

	private MyTypeDefinition getTypeDefinition(EnumeratedType t) {
		return null;
	}

	private MyTypeDefinition getTypeDefinition(BooleanType t) {
		return new MyTypeDefinition(t.getName(), MyType.BOOLEAN, new Type(
				Boolean.class), null, null, null, null,
				((Boolean) t.isDefaultValue()).toString(), null, null, null,
				null, null, null);
	}

	private MyTypeDefinition getTypeDefinition(SymbolicType t) {
		return new MyTypeDefinition(t.getName(), MyType.STRING, new Type(
				String.class), null, null, null, null, t.getDefaultValue()
				.toString(), null, t.getMinLength(), t.getMaxLength(),
				t.getPrefix(), t.getSuffix(), t.getValidationPattern());
	}
}
