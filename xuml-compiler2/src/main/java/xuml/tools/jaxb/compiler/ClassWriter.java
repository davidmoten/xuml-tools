package xuml.tools.jaxb.compiler;

import static xuml.tools.jaxb.compiler.Util.upperFirst;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import xuml.tools.jaxb.compiler.ClassInfo.Mult;
import xuml.tools.jaxb.compiler.ClassInfo.MyEvent;
import xuml.tools.jaxb.compiler.ClassInfo.MyIndependentAttribute;
import xuml.tools.jaxb.compiler.ClassInfo.MyParameter;
import xuml.tools.jaxb.compiler.ClassInfo.MyPrimaryIdAttribute;
import xuml.tools.jaxb.compiler.ClassInfo.MyReferenceMember;
import xuml.tools.jaxb.compiler.ClassInfo.MySubclassRole;
import xuml.tools.jaxb.compiler.ClassInfo.MyTransition;

import com.google.common.base.Preconditions;

public class ClassWriter {

	private static final String BEHAVIOUR_COMMENT = "All actions like onEntry actions and defined operations are performed by this Behaviour class.";
	private static final String STATE_COMMENT = "For internal use only by the state machine but is persisted by the jpa provider.";
	private static final String NO_IDENTIFIERS = "no identifiers";
	public static boolean modelInheritanceWithZeroOneToOneAssociations = true;
	private final ClassInfo info;

	public ClassWriter(ClassInfo info) {
		this.info = info;
	}

	public String generate() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bytes);
		writeClassJavadoc(out, info);
		writeClassAnnotation(out, info);
		writeClassDeclaration(out, info);
		writeConstructors(out, info);
		// writeSignaller(out, info);
		writeIdMember(out, info);
		writeNonIdIndependentAttributeMembers(out, info);
		writeStateMember(out, info);
		writeReferenceMembers(out, info);
		writeSuperclassValidationCheck(out, info);
		writeAtLeastOneFieldChecks(out, info);
		writeIdGetterAndSetter(out, info);
		writeNonIdIndependentAttributeGettersAndSetters(out, info);
		writeStateGetterAndSetter(out, info);
		writeStates(out, info);
		writeEventsStart(out, info);
		writeEvents(out, info);
		writeEventCallMethods(out, info);
		writeEventsFinish(out, info);

		writeClassClose(out);
		ByteArrayOutputStream headerBytes = new ByteArrayOutputStream();
		PrintStream header = new PrintStream(headerBytes);
		writePackage(header, info);
		writeImports(header, info);
		out.close();
		header.close();
		return headerBytes.toString() + bytes.toString();
	}

	private void writeSuperclassValidationCheck(PrintStream out, ClassInfo info) {
		if (info.isSuperclass()) {
			// TODO write superclass validation check
		}
	}

	private void writeClassJavadoc(PrintStream out, ClassInfo info) {
		jd(out, info.getClassDescription(), "");
	}

	private void writeClassAnnotation(PrintStream out, ClassInfo info) {
		info.addType(Entity.class);
		info.addType(Table.class);
		out.format("@Entity\n");
		List<List<String>> uniqueConstraints = info
				.getUniqueConstraintColumnNames();
		Preconditions.checkState(uniqueConstraints.size() > 0, NO_IDENTIFIERS);
		if (uniqueConstraints.size() > 1) {
			info.addType(UniqueConstraint.class);
			out.format("@Table(schema=\"%s\",name=\"%s\",\n", info.getSchema(),
					info.getTable());
			StringBuilder s = new StringBuilder();
			for (List<String> list : uniqueConstraints) {
				if (s.length() > 0)
					s.append(",\n");
				s.append("        @UniqueConstraint(columnNames={"
						+ getCommaDelimitedQuoted(list) + "})");
			}
			out.format("    uniqueConstraints={\n");
			out.format("%s})\n", s);
		} else {
			out.format("@Table(schema=\"%s\",name=\"%s\")\n", info.getSchema(),
					info.getTable());
		}

		if (!modelInheritanceWithZeroOneToOneAssociations)
			writeJpaInheritanceAnnotations(out, info);
	}

	private void writeJpaInheritanceAnnotations(PrintStream out, ClassInfo info) {

		if (info.isSuperclass()) {
			info.addType(Inheritance.class);
			info.addType(InheritanceType.class);
			info.addType(DiscriminatorColumn.class);
			info.addType(DiscriminatorType.class);
			out.format("@Inheritance(strategy = InheritanceType.JOINED)\n");
			out.format("//DiscriminatorColumn annotation is ignored by Hibernate but may be used\n");
			out.format("//by other JPA providers. See https://hibernate.onjira.com/browse/ANN-140\n");
			out.format("@DiscriminatorColumn(name = \"DISCRIMINATOR\", discriminatorType = DiscriminatorType.STRING, length = 255)\n");
		}
		if (info.isSubclass()) {
			MySubclassRole subclass = info.getSubclassRole();
			info.addType(DiscriminatorValue.class);
			out.format("@DiscriminatorValue(\"%s\")\n",
					subclass.getDiscriminatorValue());
		}
	}

	private void writeClassDeclaration(PrintStream out, ClassInfo info) {
		String extension;
		if (!modelInheritanceWithZeroOneToOneAssociations && info.isSubclass()) {
			MySubclassRole subclass = info.getSubclassRole();
			extension = " extends "
					+ info.addType(subclass.getSuperclassJavaFullClassName());
		} else
			extension = "";

		Type idType = getIdType(info);

		out.format("public class %s%s implements %s<%1$s,%s> {\n\n",
				info.getJavaClassSimpleName(), extension,
				info.addType(xuml.tools.jaxb.compiler.Entity.class),
				info.addType(idType));
	}

	private Type getIdType(ClassInfo info) {
		Type idType;
		if (hasEmbeddedId())
			idType = new Type(info.getPackage() + "."
					+ info.getJavaClassSimpleName() + "."
					+ info.getEmbeddedIdSimpleClassName());
		else
			idType = info.getPrimaryIdAttributeMembers().get(0).getType();
		return idType;
	}

	private void writeConstructors(PrintStream out, ClassInfo info) {
		// constructor
		String factoryTypeName = info.addType(info
				.getBehaviourFactoryFullClassName());
		jd(out, BEHAVIOUR_COMMENT, "    ");
		String behaviourTypeName = info.addType(info
				.getBehaviourFullClassName());
		out.format("    private %s behaviour;\n\n", behaviourTypeName);

		jd(out, "Constructor using BehaviourFactory.", "    ");
		out.format("    public %s(%s behaviourFactory){\n",
				info.getJavaClassSimpleName(), factoryTypeName);
		out.format("        this.behaviour = behaviourFactory.create(this);\n");
		out.format("    }\n\n");
		jd(out, "No argument constructor required by JPA.", "    ");
		out.format("    public %s(){\n", info.getJavaClassSimpleName());
		out.format("        //JPA requires no-arg constructor\n");
		out.format("    }\n\n");

		// TODO optionallly add Guice injection
		// out.format("    @%s\n", info.addType(Inject.class));
		out.format("    public void setBehaviour(%s behaviourFactory){\n",
				factoryTypeName);
		out.format("        this.behaviour = behaviourFactory.create(this);\n");
		out.format("    }\n\n");
	}

	private void writeSignaller(PrintStream out, ClassInfo info) {
		jd(out,
				"Used for signalling instances of "
						+ info.getJavaClassSimpleName(), "    ");
		String signaller = info.addType(Signaller.class);
		out.format(
				"    private static %3$s<%1$s,%2$s> signaller =\n"
						+ "        new %3$s<%1$s,%2$s>(%4$s.getEntityManagerFactory(),%1$s.class);\n\n",
				info.addType(info.getJavaClassSimpleName()),
				info.addType(info.getPrimaryIdAttributeMembers().get(0)
						.getType()), signaller,
				info.addType(info.getContextPackageName() + ".Context"));

		jd(out, "Find the " + info.getJavaClassSimpleName()
				+ " with id and send the event to it as a signal.", "    ");
		out.format(
				"    public static void signal(%s id, Event<%s> event){\n",
				info.addType(info.getPrimaryIdAttributeMembers().get(0)
						.getType()),
				info.addType(info.getJavaClassSimpleName()));
		out.format("        signaller.signal(id,event);\n");
		out.format("    }\n\n");
	}

	private boolean hasEmbeddedId() {
		return info.getPrimaryIdAttributeMembers().size() > 1;
	}

	private void writeIdMember(PrintStream out, ClassInfo info) {
		jd(out, "Primary key", "    ");
		if (!hasEmbeddedId()) {
			info.addType(Id.class);
			out.format("    @Id\n");
			writeIndependentAttributeMember(out, info
					.getPrimaryIdAttributeMembers().get(0), "    ");
		} else {
			info.addType(EmbeddedId.class);
			out.format("    @EmbeddedId\n");
			out.format("    private %s %s;\n\n",
					info.getEmbeddedIdSimpleClassName(),
					info.getEmbeddedIdAttributeName());
			info.addType(Embeddable.class);
			out.format("    @Embeddable\n");
			out.format("    public static class %s {\n\n",
					info.getEmbeddedIdSimpleClassName());
			for (MyPrimaryIdAttribute member : info
					.getPrimaryIdAttributeMembers()) {
				info.addType(Column.class);
				if (member.getReferenceClass() == null)
					out.format("        @Column(name=\"%s\")\n",
							member.getColumnName());
				else {
					out.format(
							"        @Column(name=\"%s\",insertable=true,updatable=true)\n",
							member.getColumnName());
				}
				out.format("%sprivate %s %s;\n\n", "        ",
						info.addType(member.getType()), member.getFieldName());
			}
			for (MyPrimaryIdAttribute member : info
					.getPrimaryIdAttributeMembers()) {
				out.format("%spublic %s get%s(){\n", "        ",
						info.addType(member.getType()),
						Util.upperFirst(member.getFieldName()));
				out.format("%sreturn %s;\n", "            ",
						member.getFieldName());
				out.format("%s}\n\n", "        ");

				out.format("%spublic void set%s(%s %s){\n", "        ",
						Util.upperFirst(member.getFieldName()),
						info.addType(member.getType()), member.getFieldName());
				out.format("%sthis.%s=%s;\n", "            ",
						member.getFieldName(), member.getFieldName());
				out.format("%s}\n\n", "        ");
			}
			out.format("    }\n\n");
		}
	}

	private void writeIndependentAttributeMember(PrintStream out,
			MyPrimaryIdAttribute attribute, String indent) {
		writeIndependentAttributeMember(out, attribute.getFieldName(),
				attribute.getColumnName(), false, "    ",
				info.addType(attribute.getType()));
	}

	private void writeNonIdIndependentAttributeMembers(PrintStream out,
			ClassInfo info) {
		for (MyIndependentAttribute attribute : info
				.getNonIdIndependentAttributeMembers()) {
			writeIndependentAttributeMember(out, attribute, "    ");
		}
	}

	private void writeStateMember(PrintStream out, ClassInfo info) {
		info.addType(Column.class);
		jd(out, STATE_COMMENT, "    ");
		out.format("    @Column(name=\"state\",nullable=false)\n");
		out.format("    private String state;\n\n");
	}

	private void writeReferenceMembers(PrintStream out, ClassInfo info) {
		for (MyReferenceMember ref : info.getReferenceMembers()) {
			jd(out, ref.getThisMult() + " " + info.getJavaClassSimpleName()
					+ " " + ref.getThatVerbClause() + " " + ref.getThatMult()
					+ " " + ref.getSimpleClassName(), "    ");
			if (isRelationship(ref, Mult.ONE, Mult.ZERO_ONE)) {
				info.addType(OneToOne.class);
				info.addType(FetchType.class);
				out.format(
						"    @OneToOne(mappedBy=\"%s\",fetch=FetchType.LAZY,targetEntity=%s.class)\n",
						ref.getThisName(), info.addType(ref.getFullClassName()));
				writeField(out, ref);
			} else if (isRelationship(ref, Mult.ZERO_ONE, Mult.ONE)) {
				info.addType(OneToOne.class);
				info.addType(FetchType.class);
				info.addType(JoinColumn.class);
				info.addType(CascadeType.class);
				out.format(
						"    @OneToOne(targetEntity=%s.class,cascade=CascadeType.ALL,fetch=FetchType.LAZY)\n",
						info.addType(ref.getFullClassName()));
				writeJoinColumnsAnnotation(out, ref, false);
				writeField(out, ref);
			} else if (isRelationship(ref, Mult.ONE, Mult.MANY)) {
				info.addType(OneToMany.class);
				info.addType(CascadeType.class);
				info.addType(FetchType.class);
				out.format(
						"    @OneToMany(mappedBy=\"%s\",cascade=CascadeType.ALL,fetch=FetchType.LAZY,targetEntity=%s.class)\n",
						ref.getThisName(), info.addType(ref.getFullClassName()));
				writeMultipleField(out, ref);
			} else if (isRelationship(ref, Mult.MANY, Mult.ONE)) {
				info.addType(ManyToOne.class);
				info.addType(FetchType.class);
				info.addType(JoinColumn.class);
				out.format(
						"    @ManyToOne(targetEntity=%s.class,fetch=FetchType.LAZY)\n",
						ref.getSimpleClassName());
				writeJoinColumnsAnnotation(out, ref, false);
				writeField(out, ref);
			} else if (isRelationship(ref, Mult.ONE, Mult.ONE_MANY)) {
				info.addType(OneToMany.class);
				info.addType(FetchType.class);
				info.addType(JoinColumn.class);
				info.addType(CascadeType.class);
				out.format(
						"    @OneToMany(mappedBy=\"%s\",cascade=CascadeType.ALL,fetch=FetchType.LAZY,targetEntity=%s.class)\n",
						ref.getThisName(), info.addType(ref.getFullClassName()));
				writeMultipleField(out, ref);
			} else if (isRelationship(ref, Mult.ONE_MANY, Mult.ONE)) {
				info.addType(ManyToOne.class);
				info.addType(JoinColumn.class);
				out.format("    @ManyToOne(targetEntity=%s.class)\n",
						info.addType(ref.getFullClassName()));
				writeJoinColumnsAnnotation(out, ref, false);
				writeField(out, ref);
			} else if (isRelationship(ref, Mult.ZERO_ONE, Mult.ZERO_ONE)) {
				if (info.getJavaClassSimpleName().compareTo(
						ref.getSimpleClassName()) < 0) {
					// primary
					out.format("    //primary side of relationship\n");
					info.addType(OneToOne.class);
					info.addType(FetchType.class);
					out.format(
							"    @OneToOne(mappedBy=\"%s\",fetch=FetchType.LAZY,targetEntity=%s.class)\n",
							ref.getThisName(),
							info.addType(ref.getFullClassName()));
				} else {
					// secondary
					out.format("    //secondary side of relationship\n");
					info.addType(OneToOne.class);
					info.addType(JoinColumn.class);
					info.addType(FetchType.class);
					out.format(
							"    @OneToOne(targetEntity=%s.class,fetch=FetchType.LAZY)\n",
							info.addType(ref.getFullClassName()));
					writeJoinColumnsAnnotation(out, ref, true);
				}
				writeField(out, ref);
			} else if (isRelationship(ref, Mult.ZERO_ONE, Mult.MANY)) {
				info.addType(OneToMany.class);
				info.addType(CascadeType.class);
				info.addType(FetchType.class);
				out.format(
						"    @OneToMany(mappedBy=\"%s\",cascade=CascadeType.ALL,fetch=FetchType.LAZY,targetEntity=%s.class)\n",
						ref.getThisName(), info.addType(ref.getFullClassName()));
				writeMultipleField(out, ref);
			} else if (isRelationship(ref, Mult.MANY, Mult.ZERO_ONE)) {
				info.addTypes(ManyToOne.class);
				info.addType(FetchType.class);
				info.addType(JoinColumn.class);
				out.format(
						"    @ManyToOne(targetEntity=%s.class,fetch=FetchType.LAZY)\n",
						info.addType(ref.getFullClassName()));
				writeJoinColumnsAnnotation(out, ref, true);
				writeField(out, ref);
			} else if (isRelationship(ref, Mult.ZERO_ONE, Mult.ONE_MANY)) {
				info.addType(OneToMany.class);
				info.addType(CascadeType.class);
				info.addType(FetchType.class);
				out.format(
						"    @OneToMany(mappedBy=\"%s\",cascade=CascadeType.ALL,fetch=FetchType.LAZY,targetEntity=%s.class)\n",
						ref.getThisName(), info.addType(ref.getFullClassName()));
				writeMultipleField(out, ref);
			} else if (isRelationship(ref, Mult.ONE_MANY, Mult.ZERO_ONE)) {
				info.addType(ManyToOne.class);
				info.addType(FetchType.class);
				info.addType(JoinColumn.class);
				out.format(
						"    @ManyToOne(targetEntity=%s.class,fetch=FetchType.LAZY)\n",
						info.addType(ref.getFullClassName()));
				writeJoinColumnsAnnotation(out, ref, true);
				writeField(out, ref);
			} else if (isRelationship(ref, Mult.MANY, Mult.MANY)) {
				writeManyToMany(out, info, ref);
			} else if (isRelationship(ref, Mult.ONE_MANY, Mult.ONE_MANY)) {
				writeManyToMany(out, info, ref);
			} else if (isRelationship(ref, Mult.ONE_MANY, Mult.MANY)) {
				writeManyToManySecondarySide(out, info, ref);
			} else if (isRelationship(ref, Mult.MANY, Mult.ONE_MANY)) {
				writeManyToManyPrimarySide(out, info, ref);
			}
		}
	}

	private void writeJoinColumnsAnnotation(PrintStream out,
			MyReferenceMember ref, boolean nullable) {
		info.addType(JoinColumns.class);
		out.format("    @JoinColumns(value={\n");
		boolean first = true;
		for (xuml.tools.jaxb.compiler.ClassInfo.JoinColumn col : ref
				.getJoinColumns()) {
			if (!first)
				out.format(",\n");
			first = false;
			out.format(
					"        @JoinColumn(name=\"%s\",referencedColumnName=\"%s\",nullable=%s)",
					col.getThisColumnName(), col.getOtherColumnName(), nullable);
		}
		out.format("})\n");
	}

	private void writeAtLeastOneFieldChecks(PrintStream out, ClassInfo info) {
		for (String fieldName : info.getAtLeastOneFieldChecks()) {
			writeAtLeastOneCheck(out, fieldName);
		}
		writePreUpdateCheck(out, info);
	}

	private void writeIdGetterAndSetter(PrintStream out, ClassInfo info) {
		out.format("    public %s getId() {\n", info.addType(getIdType(info)));
		out.format("        return id;\n");
		out.format("    }\n\n");
		out.format("    public void setId(%s id) {\n",
				info.addType(getIdType(info)));
		out.format("        this.id = id;\n");
		out.format("    }\n\n");
	}

	private void writeIndependentAttributeGetterAndSetter(PrintStream out,
			MyPrimaryIdAttribute attribute) {

		out.format("    public %s getId() {\n", info.addType(getIdType(info)));
		out.format("        return id;\n");
		out.format("    }\n\n");
		out.format("    public void setId(%s id) {\n",
				info.addType(getIdType(info)));
		out.format("        this.id = id;\n");
		out.format("    }\n\n");
	}

	private void writeNonIdIndependentAttributeGettersAndSetters(
			PrintStream out, ClassInfo info) {
		for (MyIndependentAttribute attribute : info
				.getNonIdIndependentAttributeMembers()) {
			writeIndependentAttributeGetterAndSetter(out, attribute);
		}
	}

	private void writeStateGetterAndSetter(PrintStream out, ClassInfo info) {
		jd(out, STATE_COMMENT, "    ");
		out.format("    public String getState(){\n");
		out.format("        return state;\n");
		out.format("    }\n\n");
		jd(out, STATE_COMMENT, "    ");
		out.format("    private void setState(String state){\n");
		out.format("        this.state= state;\n");
		out.format("    }\n\n");
	}

	private void writeStates(PrintStream out, ClassInfo info) {
		jd(out,
				"The list of all states from the state machine for this entity.",
				"    ");
		out.format("    private static enum State {\n");
		boolean first = true;
		out.format("        ");
		for (String state : info.getStateNames()) {
			if (!first)
				out.format(",");
			out.format(info.getStateIdentifier(state));
			first = false;
		}
		out.format(";\n");
		out.format("    }\n\n");
	}

	private void writeEventsStart(PrintStream out, ClassInfo info) {
		if (info.getEvents().size() == 0)
			return;
		// create Events static class and each Event declared within
		jd(out, "Event declarations.", "    ");
		out.format("    public static class Events {\n\n");
	}

	private void writeEvents(PrintStream out, ClassInfo info) {
		List<MyEvent> events = info.getEvents();
		if (events.size() == 0)
			return;

		for (MyEvent event : info.getEvents()) {
			out.format("        public static class %s implements %s<%s>{\n\n",
					event.getSimpleClassName(), info.addType(Event.class),
					info.getJavaClassSimpleName());

			StringBuilder constructorBody = new StringBuilder();
			for (MyParameter p : event.getParameters()) {
				constructorBody.append("                this."
						+ p.getFieldName() + " = " + p.getFieldName() + ";\n");
			}

			StringBuilder constructor = new StringBuilder();
			constructor.append("            public "
					+ event.getSimpleClassName() + "(");
			for (MyParameter p : event.getParameters()) {
				out.format("            private final %s %s;\n",
						info.addType(p.getType()), p.getFieldName());
				constructor.append(info.addType(p.getType()) + " "
						+ p.getFieldName());
			}
			constructor.append("){\n");
			constructor.append(constructorBody);
			constructor.append("            }\n");
			out.println();
			out.println(constructor);

			// getters
			for (MyParameter p : event.getParameters()) {
				out.format("            public %s get%s(){\n",
						info.addType(p.getType()), upperFirst(p.getFieldName()));
				out.format("                return %s;\n", p.getFieldName());
				out.format("            }\n\n");
			}
			out.format("        }\n");

		}
		out.format("    }\n\n");
	}

	private void writePreUpdateCheck(PrintStream out, ClassInfo info) {
		info.addType(Transient.class);
		info.addType(PreUpdate.class);
		out.format("    @Transient\n");
		out.format("    @PreUpdate\n");
		out.format("    private void validateBeforeUpdate(){\n");
		for (String fieldName : info.getAtLeastOneFieldChecks()) {
			out.format("        check%sValid();\n", upperFirst(fieldName));
		}
		out.format("    }\n\n");

	}

	private boolean isRelationship(MyReferenceMember ref, Mult here, Mult there) {
		return ref.getThisMult().equals(here)
				&& ref.getThatMult().equals(there);
	}

	private void writeAtLeastOneCheck(PrintStream out, String fieldName) {
		jd(out,
				"Throws RuntimeException if the field collection is empty and should contain at least one value.",
				"    ");
		info.addType(Transient.class);
		out.format("    @Transient\n");
		out.format("    private void check%sValid() {\n", upperFirst(fieldName));
		out.format("         if (this.%s.size()==0)\n", fieldName);
		out.format(
				"             throw new RuntimeException(\"%s collection cannot be empty\");\n",
				fieldName);
		out.format("     }\n\n");
	}

	private void writeManyToMany(PrintStream out, ClassInfo info,
			MyReferenceMember ref) {
		if (info.getJavaClassSimpleName().compareTo(ref.getSimpleClassName()) < 0) {
			// primary
			writeManyToManyPrimarySide(out, info, ref);
		} else {
			// secondary
			writeManyToManySecondarySide(out, info, ref);
		}
	}

	private void writeManyToManyPrimarySide(PrintStream out, ClassInfo info,
			MyReferenceMember ref) {
		out.format("    //primary side of relationship\n");
		info.addType(ManyToMany.class);
		info.addType(CascadeType.class);
		info.addType(FetchType.class);
		info.addType(JoinTable.class);
		info.addType(JoinColumn.class);
		out.format(
				"    @ManyToMany(targetEntity=%s.class,cascade=CascadeType.ALL,fetch=FetchType.LAZY)\n",
				info.addType(ref.getFullClassName()));
		out.format("        @JoinTable(name=\"%s\",schema=\"%s\",\n", ref
				.getManyToMany().getJoinTable(), ref.getManyToMany()
				.getJoinTableSchema());
		out.format("            joinColumns=@JoinColumn(name=\"%s\"),\n", ref
				.getManyToMany().getThisColumnName());
		out.format(
				"            inverseJoinColumns=@JoinColumn(name=\"%s\"))\n",
				ref.getManyToMany().getThatColumnName());
		writeMultipleField(out, ref);
	}

	private void writeManyToManySecondarySide(PrintStream out, ClassInfo info,
			MyReferenceMember ref) {
		out.format("    //secondary side of relationship\n");
		info.addType(ManyToMany.class);
		info.addType(CascadeType.class);
		info.addType(FetchType.class);
		out.format(
				"    @ManyToMany(mappedBy=\"%s\",targetEntity=%s.class,cascade=CascadeType.ALL,fetch=FetchType.LAZY)\n",
				ref.getThisName(), info.addType(ref.getFullClassName()));
		writeMultipleField(out, ref);
	}

	private void writeField(PrintStream out, MyReferenceMember ref) {
		out.format("    private %s %s;\n\n",
				info.addType(ref.getFullClassName()), ref.getFieldName());
		writeGetterAndSetter(out, info, ref.getSimpleClassName(),
				ref.getFullClassName(), ref.getFieldName(), false);
	}

	private void writeGetterAndSetter(PrintStream out, ClassInfo info,
			String simpleClassName, String fullClassName, String fieldName,
			boolean isMultiple) {
		String type;
		if (isMultiple)
			type = info.addType(new Type(Set.class.getName(), new Type(
					fullClassName)));
		else
			type = info.addType(fullClassName);
		// write getter and setter
		out.format("    public %s get%s(){\n", type, upperFirst(fieldName));
		out.format("        return %s;\n", fieldName);
		out.format("    }\n\n");
		out.format("    public void set%s(%s %s){\n", upperFirst(fieldName),
				type, fieldName);
		out.format("        this.%1$s=%1$s;\n", fieldName);
		out.format("    }\n\n");
	}

	private void writeMultipleField(PrintStream out, MyReferenceMember ref) {
		out.format("    private %s %s;\n\n", info.addType(new Type(Set.class
				.getName(), new Type(ref.getFullClassName()))), ref
				.getFieldName());
		writeGetterAndSetter(out, info, ref.getSimpleClassName(),
				ref.getFullClassName(), ref.getFieldName(), true);
	}

	private void writeEventCallMethods(PrintStream out, ClassInfo info) {
		// add event call methods

		out.format("    public void event(%s<%s> event){\n",
				info.addType(Event.class), info.getJavaClassSimpleName());
		out.format("    }\n\n");
		for (MyEvent event : info.getEvents()) {
			info.addType(Transient.class);
			jd(out,
					"Synchronously perform the change. This method should be considered\nfor internal use only. Use the signal method instead.",
					"    ");
			out.format("    @Transient\n");
			out.format("    private void processEvent(Events.%s event){\n",
					event.getSimpleClassName());
			boolean first = true;
			for (MyTransition transition : info.getTransitions()) {
				// constraint is no event overloading
				if (transition.getEventName().equals(event.getName())) {
					if (first)
						out.format("        if");
					else
						out.format("        else if");
					first = false;
					out.format(" (state.equals(State.%s.toString())){\n",
							info.getStateIdentifier(transition.getFromState()));
					out.format("            state=State.%s.toString();\n",
							info.getStateIdentifier(transition.getToState()));
					out.format("            synchronized(this) {\n");
					out.format("                behaviour.onEntry(event);\n");
					out.format("            }\n");
					out.format("        }\n");
				}
			}
		}
	}

	private void writeEventsFinish(PrintStream out, ClassInfo info) {
		if (info.getEvents().size() == 0)
			return;
		out.format("    }\n\n");
	}

	private void writeIndependentAttributeMember(PrintStream out,
			MyIndependentAttribute attribute, String indent) {
		String type = info.addType(attribute.getType());
		info.addType(Column.class);
		writeIndependentAttributeMember(out, attribute.getFieldName(),
				attribute.getColumnName(), attribute.isNullable(), indent, type);
	}

	public static class MyType {
		private String javaClassFullName;

		public MyType(String javaClassFullName) {
			super();
			this.javaClassFullName = javaClassFullName;
		}

		public String getJavaClassFullName() {
			return javaClassFullName;
		}

		public void setJavaClassFullName(String javaClassFullName) {
			this.javaClassFullName = javaClassFullName;
		}
	}

	private void writeIndependentAttributeMember(PrintStream out,
			String fieldName, String columnName, boolean isNullable,
			String indent, String type) {
		out.format("%s@Column(name=\"%s\",nullable=%s)\n", indent, columnName,
				isNullable);
		out.format("%sprivate %s %s;\n\n", indent, type, fieldName);
	}

	private void writeIndependentAttribute(PrintStream out,
			MyIndependentAttribute attribute, String indent, String type) {
	}

	private void writeIndependentAttributeGetterAndSetter(PrintStream out,
			MyIndependentAttribute attribute) {
		String type = info.addType(attribute.getType());
		jd(out, "Returns " + attribute.getFieldName() + ".", "    ");
		if (attribute.getFieldName().equals("id")) {
			info.addType(Override.class);
			out.format("    @Override\n");
		}
		out.format("    public %s get%s(){\n", type,
				upperFirst(attribute.getFieldName()));
		out.format("        return %s;\n", attribute.getFieldName());
		out.format("    }\n\n");

		jd(out, "Sets " + attribute.getFieldName() + " to the given value.",
				"    ");
		out.format("    public void set%s(%s %s){\n",
				upperFirst(attribute.getFieldName()), type,
				attribute.getFieldName());
		out.format("        this.%1$s=%1$s;\n", attribute.getFieldName());
		out.format("    }\n\n");
	}

	private void writeClassClose(PrintStream out) {
		out.format("}");
	}

	private void writePackage(PrintStream out, ClassInfo info) {
		out.format("package %s;\n\n", info.getPackage());
	}

	private void writeImports(PrintStream out, ClassInfo info) {
		out.println(info.getImports());
	}

	// ///////////////////////////////////////
	// Utils
	// ///////////////////////////////////////

	private void jd(PrintStream out, String comment, String indent) {
		out.format("%s/**\n", indent);
		for (String line : comment.split("\n")) {
			out.format("%s * %s\n", indent, line);
		}
		out.format("%s */\n", indent);
	}

	private String getDelimited(Collection<String> items, String delimiter,
			String itemBefore, String itemAfter) {
		StringBuilder s = new StringBuilder();
		for (String item : items) {
			if (s.length() > 0)
				s.append(delimiter);
			s.append(itemBefore);
			s.append(item);
			s.append(itemAfter);
		}
		return s.toString();
	}

	private String getCommaDelimitedQuoted(List<String> items) {
		return getDelimited(items, ",", "\"", "\"");
	}

}
