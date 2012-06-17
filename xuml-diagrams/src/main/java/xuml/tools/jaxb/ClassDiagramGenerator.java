package xuml.tools.jaxb;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import miuml.jaxb.Attribute;
import miuml.jaxb.BinaryAssociation;
import miuml.jaxb.DerivedAttribute;
import miuml.jaxb.Domain;
import miuml.jaxb.Domains;
import miuml.jaxb.Event;
import miuml.jaxb.Generalization;
import miuml.jaxb.IdentifierAttribute;
import miuml.jaxb.IndependentAttribute;
import miuml.jaxb.ModeledDomain;
import miuml.jaxb.Named;
import miuml.jaxb.Perspective;
import miuml.jaxb.Reference;
import miuml.jaxb.ReferentialAttribute;
import miuml.jaxb.Relationship;
import miuml.jaxb.Subsystem;
import miuml.jaxb.SubsystemElement;
import miuml.jaxb.UnaryAssociation;

import org.apache.commons.io.IOUtils;

public class ClassDiagramGenerator {

	public String generate(Domains domains) {
		return placeInTemplate(generateDivs(domains));
	}

	private String placeInTemplate(String divs) {
		try {
			String template = IOUtils.toString(ClassDiagramGenerator.class
					.getResourceAsStream("/class-diagram-template.html"));
			return template.replace("${xuml.divs}", divs);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String generateDivs(Domains domains) {
		for (JAXBElement<? extends Domain> domain : domains.getDomain()) {
			if (domain.getValue() instanceof ModeledDomain) {
				ModeledDomain md = (ModeledDomain) domain.getValue();
				return generateDivs(md.getSubsystem().get(0));
			}
		}
		return "";
	}

	public String generate(Domains domains, int domain, int ss) {
		ModeledDomain md = (ModeledDomain) domains.getDomain().get(domain)
				.getValue();
		Subsystem sub = md.getSubsystem().get(ss);
		return placeInTemplate(generateDivs(sub));
	}

	private String generateDivs(Subsystem subsystem) {
		StringBuilder s = new StringBuilder();
		for (JAXBElement<? extends SubsystemElement> element : subsystem
				.getSubsystemElement())
			if (element.getValue() instanceof miuml.jaxb.Class)
				generateClass(s, (miuml.jaxb.Class) element.getValue());
			else if (element.getValue() instanceof Relationship) {
				Relationship r = (Relationship) element.getValue();
				if (r instanceof BinaryAssociation)
					generateAssociation(s, (BinaryAssociation) r, subsystem);
				else if (r instanceof UnaryAssociation)
					generateAssociation(s, (UnaryAssociation) r, subsystem);
				else if (r instanceof Generalization)
					generateGeneralization(s, (Generalization) r);
			}
		return s.toString();
	}

	private void generateGeneralization(StringBuilder s, Generalization g) {
		for (Named sp : g.getSpecializedClass())
			s.append("<div class=\"generalization\" id=\""
					+ replaceSpaces(sp.getName()) + "-"
					+ getRelationshipName(g.getRnum()) + "\" groupName=\""
					+ getRelationshipName(g.getRnum()) + "\" superClassName=\""
					+ replaceSpaces(g.getSuperclass()) + "\" subClassName=\""
					+ replaceSpaces(sp.getName()) + "\"></div>\n");
	}

	private String replaceSpaces(String s) {
		return s.replaceAll(" ", "_");
	}

	private void generateAssociation(StringBuilder s, BinaryAssociation r,
			Subsystem ss) {
		if (r.getActivePerspective().getViewedClass()
				.equals(r.getPassivePerspective().getViewedClass())) {
			System.out
					.println("binary association involving one class only not yet supported");
			return;
		}
		s.append("<div class=\"relationship\" id=\""
				+ getRelationshipName(r.getRnum())
				+ "\" className1=\""
				+ r.getActivePerspective().getViewedClass()
						.replaceAll(" ", "_")
				+ "\" className2=\""
				+ r.getPassivePerspective().getViewedClass()
						.replaceAll(" ", "_") + "\" verbClause1=\""
				+ r.getActivePerspective().getPhrase() + "\" verbClause2=\""
				+ r.getPassivePerspective().getPhrase() + "\" multiplicity1=\""
				+ getMultiplicityAbbreviation(r.getActivePerspective())
				+ "\" multiplicity2=\""
				+ getMultiplicityAbbreviation(r.getPassivePerspective())
				+ "\"></div>\n");
	}

	private void generateAssociation(StringBuilder s, UnaryAssociation r,
			Subsystem subsystem) {
		// TODO implement unary associations
	}

	private String getMultiplicityAbbreviation(Perspective p) {
		if (p.isConditional() && p.isOnePerspective())
			return "0..1";
		else if (!p.isConditional() && p.isOnePerspective())
			return "1";
		else if (p.isConditional() && !p.isOnePerspective())
			return "*";
		else
			return "1..*";
	}

	private static String getIdentifierName(BigInteger i) {
		if (i.intValue() == 1)
			return "I";
		else
			return "I" + i;
	}

	private static String getRelationshipName(BigInteger n) {
		return "R" + n;
	}

	private void generateClass(StringBuilder s, miuml.jaxb.Class cls) {
		System.out.println("class=" + cls.getName());
		s.append("<div id=\"" + cls.getName().replaceAll(" ", "_")
				+ "\" class=\"cls draggable");

		if (cls.getAssociation() != null)
			s.append(" associationClass");
		s.append("\"");
		if (cls.getAssociation() != null)
			s.append(" relationshipName=\""
					+ getRelationshipName(cls.getAssociation()) + "\" ");
		s.append(">\n");
		s.append("  <div class=\"attributes\">\n");
		for (JAXBElement<? extends Attribute> attr : cls.getAttribute()) {

			System.out.println("attribute=" + attr.getValue().getName());
			List<String> items = new ArrayList<String>();
			for (IdentifierAttribute id : attr.getValue().getIdentifier())
				items.add(getIdentifierName(id.getNumber()));
			if (attr.getValue() instanceof IndependentAttribute) {
				IndependentAttribute a = (IndependentAttribute) attr.getValue();
				s.append("    <div class=\"attribute\">" + a.getName() + ": "
						+ a.getType() + " ");
				// if (!a.isMandatory())
				// items.add("O");
			} else if (attr.getValue() instanceof ReferentialAttribute) {
				ReferentialAttribute r = (ReferentialAttribute) attr.getValue();
				Reference ref = r.getReference().getValue();
				items.add(getRelationshipName(ref.getRelationship()));
				s.append("<div class=\"attribute\">" + r.getName() + ": ");
			} else if (attr.getValue() instanceof DerivedAttribute) {
				DerivedAttribute d = (DerivedAttribute) attr.getValue();
				s.append("<div class=\"attribute\">" + "/ " + d.getName()
						+ ": " + d.getType());
			}
			StringBuilder b = new StringBuilder();
			for (String item : items) {
				if (b.length() > 0)
					b.append(",");
				b.append(item);
			}
			if (b.length() > 0)
				s.append("{" + b + "}");
			s.append("</div>\n");
		}
		s.append("  </div>\n");

		// if (!cls.getOperation().isEmpty()) {
		// s.append("<div class=\"operations\">");
		// for (Operation op : cls.getOperation()) {
		// s.append("<div class=\"operation\">");
		// s.append(op.getName() + "(");
		// boolean first = true;
		// for (OperationParameter p : op.getParameter()) {
		// if (!first)
		// s.append(",");
		// s.append(p.getName());
		// first = false;
		// }
		// s.append(")");
		// s.append("</div>");
		// }
		// s.append("</div>");
		// }
		if (cls.getLifecycle() != null
				&& !cls.getLifecycle().getEvent().isEmpty()) {
			s.append("<div class=\"events\">");
			for (JAXBElement<? extends Event> event : cls.getLifecycle()
					.getEvent()) {
				s.append("<div class=\"event\">");
				s.append(event.getValue().getName());
				s.append("</div>");
			}
			s.append("</div>");
		}
		s.append("</div>\n");
	}

}
