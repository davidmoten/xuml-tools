package xuml.tools.migrator;

import model.Attribute;
import model.AttributeReferential;
import model.Class;
import model.Event;
import model.Package;
import model.Parameter;
import model.Primitive;
import xuml.metamodel.jaxb.IndependentAttribute;
import xuml.metamodel.jaxb.IndependentAttributeType;
import xuml.metamodel.jaxb.ObjectFactory;
import xuml.metamodel.jaxb.ReferentialAttribute;
import xuml.metamodel.jaxb.System;
import xuml.metamodel.jaxb.ToOneReference;

/**
 * 
 * Migrates an xuml-compiler System to an xuml-compiler2 System.
 * 
 * @author dxm
 * 
 */
public class SystemMigrator {

	private final model.System system;
	private final ObjectFactory factory;
	private final String domain;

	public SystemMigrator(model.System system, String domain) {
		this.system = system;
		this.domain = domain;
		factory = new ObjectFactory();
	}

	public System convert() {
		System s = factory.createSystem();

		for (Package pkg : system.getPackage()) {
			processPackage(system, pkg);

		}
		return null;
	}

	private void processPackage(model.System system, Package pkg) {
		for (Package p : pkg.getSubPackage()) {
			processPackage(system, p);
		}
		for (Class c : pkg.getClass_()) {
			xuml.metamodel.jaxb.Class cls = factory.createClass();
			cls.setName(c.getName());
			cls.setDomain(domain);
			for (Event e : c.getStateMachine().getEvent()) {
				xuml.metamodel.jaxb.Event event = factory.createEvent();
				event.setName(e.getName());
				for (Parameter p : e.getParameter()) {
					xuml.metamodel.jaxb.Parameter param = factory
							.createParameter();
					param.setName(event.getName());
					event.getParameter().add(param);
				}
			}
			for (Attribute a : c.getAttribute()) {
				IndependentAttribute attribute = factory
						.createIndependentAttribute();
				attribute.setName(a.getName());
				attribute.setType(convert(a.getType().getPrimitive()));
			}
			for (AttributeReferential a : c.getAttributeReferential()) {
				ReferentialAttribute attribute = factory
						.createReferentialAttribute();
				attribute.setName(a.getName());
				attribute.setOtherName(a.getAssociationEnd().getName());
				ToOneReference ref = factory.createToOneReference();
			}
		}

	}

	private IndependentAttributeType convert(Primitive t) {
		if (t.equals(Primitive.ARBITRARY_ID))
			return IndependentAttributeType.ARBITRARY_ID;
		else if (t.equals(Primitive.BOOLEAN))
			return IndependentAttributeType.BOOLEAN;
		else if (t.equals(Primitive.DATE))
			return IndependentAttributeType.DATE;
		else if (t.equals(Primitive.DECIMAL))
			return IndependentAttributeType.DECIMAL;
		else if (t.equals(Primitive.INTEGER))
			return IndependentAttributeType.INTEGER;
		else if (t.equals(Primitive.LONG))
			return IndependentAttributeType.INTEGER;
		else if (t.equals(Primitive.STRING))
			return IndependentAttributeType.STRING;
		else if (t.equals(Primitive.TIME))
			return IndependentAttributeType.TIME;
		else if (t.equals(Primitive.TIMESTAMP))
			return IndependentAttributeType.TIMESTAMP;
		else
			throw new RuntimeException("unexpected");
	}
}
