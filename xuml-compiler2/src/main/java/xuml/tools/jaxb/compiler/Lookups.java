package xuml.tools.jaxb.compiler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import miuml.jaxb.Association;
import miuml.jaxb.Attribute;
import miuml.jaxb.BinaryAssociation;
import miuml.jaxb.Class;
import miuml.jaxb.Generalization;
import miuml.jaxb.ModeledDomain;
import miuml.jaxb.Named;
import miuml.jaxb.ReferentialAttribute;
import miuml.jaxb.Relationship;
import miuml.jaxb.Subsystem;
import miuml.jaxb.SubsystemElement;
import miuml.jaxb.ToOneReference;
import miuml.jaxb.UnaryAssociation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

class Lookups {
	private final Map<String, Class> classesByName = Maps.newHashMap();
	private final Map<BigInteger, Relationship> relationshipsByNumber = Maps
			.newHashMap();

	private final Map<BigInteger, Map<ClassAttribute, ClassAttribute>> relationshipAttributeMappings = Maps
			.newHashMap();

	private final Map<String, Attribute> attributesByName = Maps.newHashMap();

	public Lookups(ModeledDomain domain) {
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

		for (Class cls : classesByName.values()) {
			for (JAXBElement<? extends Attribute> element : cls.getAttribute()) {
				if (element.getValue() instanceof ReferentialAttribute) {
					ReferentialAttribute r = (ReferentialAttribute) element
							.getValue();
					BigInteger rNum = r.getReference().getValue()
							.getRelationship();
					if (relationshipAttributeMappings.get(rNum) == null)
						relationshipAttributeMappings.put(rNum,
								new HashMap<ClassAttribute, ClassAttribute>());
					if (r.getReference().getValue() instanceof ToOneReference) {
						ToOneReference r2 = (ToOneReference) r.getReference()
								.getValue();
						// relationshipAttributeMappings.get(rNum).put(new
						// ClassAttribute(cls.getName(),r.getName()), new
						// ClassAttribute( r2.getAttribute()))
					}
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

	public List<Generalization> getGeneralizations(Class cls) {
		ArrayList<Generalization> list = Lists.newArrayList();
		for (Relationship r : relationshipsByNumber.values()) {
			if (r instanceof Generalization)
				list.add((Generalization) r);
		}
		return list;
	}

}
