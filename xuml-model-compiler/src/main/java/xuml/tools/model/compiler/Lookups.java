package xuml.tools.model.compiler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import xuml.tools.miuml.metamodel.jaxb.Association;
import xuml.tools.miuml.metamodel.jaxb.AtomicType;
import xuml.tools.miuml.metamodel.jaxb.BinaryAssociation;
import xuml.tools.miuml.metamodel.jaxb.BooleanType;
import xuml.tools.miuml.metamodel.jaxb.Class;
import xuml.tools.miuml.metamodel.jaxb.ConstrainedType;
import xuml.tools.miuml.metamodel.jaxb.Domains;
import xuml.tools.miuml.metamodel.jaxb.EnumeratedType;
import xuml.tools.miuml.metamodel.jaxb.Generalization;
import xuml.tools.miuml.metamodel.jaxb.IntegerType;
import xuml.tools.miuml.metamodel.jaxb.ModeledDomain;
import xuml.tools.miuml.metamodel.jaxb.Named;
import xuml.tools.miuml.metamodel.jaxb.RealType;
import xuml.tools.miuml.metamodel.jaxb.Relationship;
import xuml.tools.miuml.metamodel.jaxb.Subsystem;
import xuml.tools.miuml.metamodel.jaxb.SubsystemElement;
import xuml.tools.miuml.metamodel.jaxb.SymbolicType;
import xuml.tools.miuml.metamodel.jaxb.UnaryAssociation;


import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

class Lookups {
	private final Map<String, Class> classesByName = Maps.newHashMap();
	private final Map<BigInteger, Relationship> relationshipsByNumber = Maps
			.newHashMap();

	private final ModeledDomain domain;
	private final Domains domains;

	public Lookups(Domains domains, ModeledDomain domain) {
		this.domains = domains;
		this.domain = domain;
		for (Subsystem subsystem : domain.getSubsystem()) {
			for (JAXBElement<? extends SubsystemElement> ssElement : subsystem
					.getSubsystemElement()) {
				SubsystemElement val = ssElement.getValue();
				if (val instanceof Relationship) {
					relationshipsByNumber.put(((Relationship) val).getRnum(),
							(Relationship) val);
				} else if (val instanceof Class) {
					classesByName.put(((Class) val).getName(), (Class) val);
				}
			}
		}
	}

	public Relationship getRelationship(BigInteger number) {
		return relationshipsByNumber.get(number);
	}

	public Class getClassByName(String name) {
		return classesByName.get(name);
	}

	public boolean isSuperclass(String className) {
		for (Relationship r : relationshipsByNumber.values()) {
			if (r instanceof Generalization) {
				Generalization g = (Generalization) r;
				if (className.equals(g.getSuperclass()))
					return true;
			}
		}
		return false;
	}

	public boolean isSpecialization(String className) {
		for (Relationship r : relationshipsByNumber.values()) {
			if (r instanceof Generalization) {
				Generalization g = (Generalization) r;
				for (Named sp : g.getSpecializedClass())
					if (className.equals(sp.getName()))
						return true;
			}
		}
		return false;
	}

	public List<Association> getAssociations(Class cls) {
		ArrayList<Association> list = Lists.newArrayList();
		for (Relationship r : relationshipsByNumber.values()) {
			if (r instanceof Association) {
				Association a = (Association) r;
				if (a instanceof BinaryAssociation) {
					BinaryAssociation b = (BinaryAssociation) a;
					if (b.getActivePerspective().getViewedClass()
							.equals(cls.getName())
							|| b.getPassivePerspective().getViewedClass()
									.equals(cls.getName()))
						list.add(a);
				} else {
					UnaryAssociation u = (UnaryAssociation) a;
					if (u.getSymmetricPerspective().getViewedClass()
							.equals(cls.getName()))
						list.add(a);
				}
			}
		}
		return list;
	}

	public List<Generalization> getGeneralizations() {
		ArrayList<Generalization> list = Lists.newArrayList();
		for (Relationship r : relationshipsByNumber.values()) {
			if (r instanceof Generalization) {
				Generalization g = (Generalization) r;
				list.add(g);
			}
		}
		return list;
	}

	public Optional<Association> associationForAssociationClass(String className) {
		for (Relationship r : relationshipsByNumber.values()) {
			if (r instanceof Association) {
				Association a = (Association) r;
				if (className.equals(a.getAssociationClass()))
					return Optional.of(a);
			}
		}
		return Optional.absent();
	}

	public String getJavaType(String typeName) {
		return getJavaType(getAtomicType(typeName), typeName);
	}

	public AtomicType getAtomicType(String typeName) {
		// check domain class types then if not found check global types
		AtomicType result = getAtomicType(domain.getConstrainedType(), typeName);
		if (result == null)
			result = getAtomicType(domains.getConstrainedType(), typeName);
		if (result == null)
			throw new RuntimeException("type not found: " + typeName);
		else
			return result;
	}

	private AtomicType getAtomicType(
			List<JAXBElement<? extends ConstrainedType>> types, String typeName) {
		AtomicType result = null;
		for (JAXBElement<? extends ConstrainedType> element : types) {
			if (element.getValue() instanceof AtomicType) {
				AtomicType t = (AtomicType) element.getValue();
				if (typeName.equals(t.getName()))
					result = t;
			} else
				throw new RuntimeException(
						"Structure types not implemented yet");
		}
		return result;
	}

	private String getJavaType(AtomicType t, String name) {
		String result;
		if (t instanceof BooleanType)
			result = Boolean.class.getName();
		else if (t instanceof EnumeratedType)
			result = String.class.getName();
		else if (t instanceof IntegerType && "date".equalsIgnoreCase(name))
			result = Date.class.getName();
		else if (t instanceof IntegerType && "timestamp".equalsIgnoreCase(name))
			result = Date.class.getName();
		else if (t instanceof IntegerType)
			result = Integer.class.getName();
		else if (t instanceof RealType)
			result = Double.class.getName();
		else if (t instanceof SymbolicType)
			result = String.class.getName();
		else
			throw new RuntimeException(t.getClass().getName()
					+ " not implemented");
		return result;
	}
}
