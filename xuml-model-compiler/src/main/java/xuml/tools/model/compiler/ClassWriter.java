package xuml.tools.model.compiler;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import xuml.tools.model.compiler.ClassInfoBase.Mult;
import xuml.tools.model.compiler.ClassInfoBase.MyEvent;
import xuml.tools.model.compiler.ClassInfoBase.MyIdAttribute;
import xuml.tools.model.compiler.ClassInfoBase.MyIndependentAttribute;
import xuml.tools.model.compiler.ClassInfoBase.MyParameter;
import xuml.tools.model.compiler.ClassInfoBase.MyReferenceMember;
import xuml.tools.model.compiler.ClassInfoBase.MySpecializations;
import xuml.tools.model.compiler.ClassInfoBase.MySubclassRole;
import xuml.tools.model.compiler.ClassInfoBase.MyTransition;
import xuml.tools.model.compiler.ClassInfoBase.MyType;
import xuml.tools.model.compiler.ClassInfoBase.MyTypeDefinition;
import xuml.tools.model.compiler.runtime.CreationEvent;
import xuml.tools.model.compiler.runtime.EntityHelper;
import xuml.tools.model.compiler.runtime.Event;
import xuml.tools.model.compiler.runtime.RelationshipNotEstablished;
import xuml.tools.model.compiler.runtime.Signaller;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ClassWriter {

	private static final String BEHAVIOUR_COMMENT = "All actions like onEntry actions and defined\noperations are performed by this Behaviour class.";
	private static final String STATE_COMMENT = "For internal use only by the state machine but is persisted by the jpa provider.";
	public static boolean useJpaJoinedStrategyForSpecialization = false;
	private final ClassInfo info;

	public ClassWriter(ClassInfo info) {
		this.info = info;
	}

	public String generate() {
		Set<String> validationMethods = Sets.newTreeSet();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bytes);
		writeClassJavadoc(out, info);
		writeClassAnnotation(out, info);
		writeClassDeclaration(out, info);
		writeConstructors(out, info);
		writeEntityHelper(out, info);
		writeIdMember(out, info, validationMethods);
		writeUniqueIdMethod(out, info);
		writeNonIdIndependentAttributeMembers(out, info, validationMethods);
		writeStateMember(out, info);
		writeReferenceMembers(out, info, validationMethods);
		writeSuperclassValidationCheck(out, info, validationMethods);
		writePreUpdateCheck(out, info, validationMethods);
		writeIdGetterAndSetter(out, info);
		writeNonIdIndependentAttributeGettersAndSetters(out, info);
		writeStateGetterAndSetter(out, info);
		writeStates(out, info);
		writeEvents(out, info);
		writeEventCallMethods(out, info);
		writeStaticCreateMethods(out, info);
		writeMergeMethod(out, info);
		writePersistMethod(out, info);
		writeRefreshMethod(out, info);
		writeLoadMethod(out, info);
		writeToStringMethod(out, info);
		writeBehaviourInterface(out, info);
		writeBehaviourFactoryInterface(out, info);

		writeClassClose(out);
		ByteArrayOutputStream headerBytes = new ByteArrayOutputStream();
		PrintStream header = new PrintStream(headerBytes);
		writePackage(header, info);
		writeImports(header, info);
		out.close();
		header.close();
		return headerBytes.toString() + bytes.toString();
	}

	private void writeSuperclassValidationCheck(PrintStream out,
			ClassInfo info, Set<String> validationMethods) {
		if (info.isSuperclass()) {
			List<MySpecializations> list = info.getSpecializations();
			for (MySpecializations sp : list) {
				String methodName = "validateSpecializationR" + sp.getRnum();
				out.format("    private void %s() {\n", methodName);
				out.format("        int count = 0;\n");
				for (String fieldName : sp.getFieldNames()) {
					out.format("        if (%s != null)\n", fieldName);
					out.format("            count++;\n");
				}
				out.format("    if (count != 1)\n");
				out.format("        throw new RuntimeException(\"wrong number of specializations = \" + count);\n");
				out.format("    }\n\n");
				validationMethods.add(methodName);
			}
		}
	}

	private void writeClassJavadoc(PrintStream out, ClassInfo info) {
		jd(out, info.getClassDescription(), "");
	}

	private void writeClassAnnotation(PrintStream out, ClassInfo info) {
		out.format("@%s\n", info.addType(Entity.class));
		List<List<String>> uniqueConstraints = info
				.getUniqueConstraintColumnNames();
		if (uniqueConstraints.size() >= 1) {
			out.format("@%s(schema=\"%s\",name=\"%s\",\n",
					info.addType(Table.class), info.getSchema(),
					info.getTable());
			StringBuilder s = new StringBuilder();
			for (List<String> list : uniqueConstraints) {
				if (s.length() > 0)
					s.append(",\n");
				s.append("        @" + info.addType(UniqueConstraint.class)
						+ "(columnNames={" + getCommaDelimitedQuoted(list)
						+ "})");
			}
			out.format("    uniqueConstraints={\n");
			out.format("%s})\n", s);
		} else {
			out.format("@%s(schema=\"%s\",name=\"%s\")\n",
					info.addType(Table.class), info.getSchema(),
					info.getTable());
		}

		if (useJpaJoinedStrategyForSpecialization)
			writeJpaInheritanceAnnotations(out, info);
	}

	private void writeJpaInheritanceAnnotations(PrintStream out, ClassInfo info) {

		if (info.isSuperclass()) {
			out.format("@%s(strategy = %s.JOINED)\n",
					info.addType(Inheritance.class),
					info.addType(InheritanceType.class));
			out.format("//DiscriminatorColumn annotation is ignored by Hibernate but may be used\n");
			out.format("//by other JPA providers. See https://hibernate.onjira.com/browse/ANN-140\n");
			out.format(
					"@%s(name = \"DISCRIMINATOR\", discriminatorType = %s.STRING, length = 255)\n",
					info.addType(DiscriminatorColumn.class),
					info.addType(DiscriminatorType.class));
		}
		if (info.isSubclass()) {
			MySubclassRole subclass = info.getSubclassRole();
			out.format("@%s(\"%s\")\n", info.addType(DiscriminatorValue.class),
					subclass.getDiscriminatorValue());
		}
	}

	private void writeClassDeclaration(PrintStream out, ClassInfo info) {
		String extension;
		if (useJpaJoinedStrategyForSpecialization && info.isSubclass()) {
			MySubclassRole subclass = info.getSubclassRole();
			extension = " extends "
					+ info.addType(subclass.getSuperclassJavaFullClassName());
		} else
			extension = "";

		out.format("public class %s%s implements %s<%1$s> {\n\n",
				info.getJavaClassSimpleName(), extension,
				info.addType(xuml.tools.model.compiler.runtime.Entity.class));
	}

	private Type getIdType(ClassInfo info) {
		Type idType;
		if (hasEmbeddedId())
			idType = new Type(info.getPackage() + "."
					+ info.getJavaClassSimpleName() + "."
					+ info.getEmbeddedIdSimpleClassName());
		else
			idType = info.getPrimaryIdAttributeMembers().get(0).getType()
					.getType();
		return idType;
	}

	private void writeConstructors(PrintStream out, ClassInfo info) {
		// constructor
		jd(out, "No argument constructor required by JPA.", "    ");
		out.format("    public %s(){\n", info.getJavaClassSimpleName());
		out.format("        //JPA requires no-arg constructor\n");
		if (info.hasBehaviour()) {
			out.format("        _behaviour = _behaviourFactory.create(this);\n");
		}
		out.format("    }\n\n");
		if (info.hasBehaviour()) {
			String factoryTypeName = "BehaviourFactory";

			jd(out, "If behaviour is not explicitly specified then the\n"
					+ "behaviour factory is used to create behaviour.", "    ");
			String behaviourTypeName = "Behaviour";
			out.format("    private static %s _behaviourFactory;\n\n",
					factoryTypeName);

			jd(out, BEHAVIOUR_COMMENT, "    ");
			out.format("    @%s\n", info.addType(Transient.class));
			out.format("    private %s _behaviour;\n\n", behaviourTypeName);

			jd(out, "Constructor using Behaviour.", "    ");
			out.format("    public %s(%s behaviour){\n",
					info.getJavaClassSimpleName(), behaviourTypeName);
			out.format("        %s.checkNotNull(_behaviourFactory,\n",
					info.addType(Preconditions.class));
			out.format(
					"            \"You need to call static method setBehaviourFactory before instantiating \" + %s.class.getName());\n",
					info.getJavaClassSimpleName());
			out.format("        this._behaviour = behaviour;\n");
			out.format("    }\n\n");

			// TODO optionallly add Guice injection
			// out.format("    @%s\n", info.addType(Inject.class));
			jd(out, "Sets the BehaviourFactory for all instances of\n"
					+ "this class. It will only be used when Behaviour\n"
					+ "is not explicitly provided in the constructor.", "    ");
			out.format(
					"    public static void setBehaviourFactory(%s factory){\n",
					factoryTypeName);
			out.format("        _behaviourFactory = factory;\n");
			out.format("    }\n\n");
		}
		String idClassName;
		if (!hasEmbeddedId()) {
			idClassName = info.addType(info.getPrimaryIdAttributeMembers()
					.get(0).getType().getType());
		} else
			idClassName = info.getEmbeddedIdSimpleClassName();

		// constructor using Id
		out.format("    public %s(%s id) {\n", info.getJavaClassSimpleName(),
				idClassName);
		out.format("        this.id = id;\n");
		out.format("    }\n\n");

		// static creator using Id
		out.format("    public static %s create(%s id) {\n",
				info.getJavaClassSimpleName(), idClassName);
		out.format("        return new %s(id);\n",
				info.getJavaClassSimpleName());
		out.format("    }\n\n");

	}

	private boolean hasEmbeddedId() {
		return info.getPrimaryIdAttributeMembers().size() > 1;
	}

	private void writeEntityHelper(PrintStream out, ClassInfo info) {
		out.format("    private static %s signaller;\n\n",
				info.addType(Signaller.class));

		out.format("    static void setSignaller_(%s sig) {\n",
				info.addType(Signaller.class));
		out.format("        signaller = sig;\n");
		out.format("    }\n\n");

		out.format("    @%s\n", info.addType(Transient.class));
		out.format("    private %s _helper;\n\n",
				info.addType(EntityHelper.class));
		out.format("    public synchronized %s helper() {\n",
				info.addType(EntityHelper.class));
		out.format("        if (_helper==null)\n");
		out.format("            _helper = new %s(signaller,this);\n",
				info.addType(EntityHelper.class));
		out.format("        return _helper;\n");
		out.format("    }\n\n");
	}

	private void writeIdMember(PrintStream out, ClassInfo info,
			Set<String> validationMethods) {
		jd(out, "Primary key", "    ");
		if (!hasEmbeddedId()) {
			out.format("    @%s\n", info.addType(Id.class));
			MyIdAttribute attribute = info.getPrimaryIdAttributeMembers()
					.get(0);
			// override attribute field name to 'id'
			writeIndependentAttributeMember(out, "id",
					attribute.getColumnName(), false, "    ",
					attribute.getType());
		} else {
			writeEmbeddedIdField(out, info);

			writeEmbeddedIdDeclaration(out, info);

			writeEmbeddedIdConstructor(out, info);

			writeEmbeddedIdFields(out, info, validationMethods);

			writeEmbeddedIdGettersAndSetters(out, info);

			writeEmbeddedIdToString(out, info);

			writeEmbeddedIdEquals(out, info);

			writeEmbeddedIdHashCode(out, info);

			out.format("    }\n\n");
		}
	}

	private void writeEmbeddedIdEquals(PrintStream out, ClassInfo info) {
		out.format("        @%s\n", info.addType(Override.class));
		out.format("        public boolean equals(Object obj) {\n");
		out.format("            if (obj==null)\n");
		out.format("                return false;\n");
		out.format("            if (getClass() != obj.getClass())\n");
		out.format("                return false;\n");
		out.format("            final %1$s other = (%1$s) obj;\n",
				info.getEmbeddedIdSimpleClassName());
		out.format("            return ");
		boolean first = true;
		for (MyIdAttribute member : info.getPrimaryIdAttributeMembers()) {
			if (!first) {
				out.println();
				out.format("                && ");
			}
			out.format("%s.equal(this.%2$s, other.%2$s)",
					info.addType(Objects.class), member.getFieldName());
			first = false;
		}
		out.format(";\n");

		out.format("        }\n\n");
	}

	private void writeEmbeddedIdHashCode(PrintStream out, ClassInfo info) {
		out.format("        @%s\n", info.addType(Override.class));
		out.format("        public int hashCode() {\n");
		out.format("            return %s.hashCode(\n",
				info.addType(Objects.class));
		boolean first = true;
		for (MyIdAttribute member : info.getPrimaryIdAttributeMembers()) {
			if (!first) {
				out.format(",\n");
			}
			out.format("                ");
			out.format("this.%s", member.getFieldName());
			first = false;
		}
		out.format(");\n");
		out.format("        }\n\n");
	}

	private void writeEmbeddedIdDeclaration(PrintStream out, ClassInfo info) {
		out.format("    @%s\n", info.addType(Embeddable.class));
		out.format("    @%s(\"serial\")\n",
				info.addType(SuppressWarnings.class));
		out.format("    public static class %s implements %s {\n\n",
				info.getEmbeddedIdSimpleClassName(),
				info.addType(Serializable.class));
		out.format("        public %s() {\n",
				info.getEmbeddedIdSimpleClassName());
		out.format("            //JPA requires no-arg constructor\n");
		out.format("        }\n\n");
	}

	private void writeEmbeddedIdToString(PrintStream out, ClassInfo info) {
		out.format("%s@%s\n", "        ", info.addType(Override.class));
		out.format("%spublic %s toString(){\n", "        ",
				info.addType(String.class));
		out.format("%s%s _s = new %s();\n", "            ",
				info.addType(StringBuffer.class),
				info.addType(StringBuffer.class));
		out.format("            _s.append(\"%s [\");\n",
				info.getEmbeddedIdSimpleClassName());
		boolean first = true;
		for (MyIdAttribute member : info.getPrimaryIdAttributeMembers()) {
			if (!first)
				out.format("%s_s.append(\",\");\n", "            ");
			out.format("%s_s.append(\"%s=\");\n", "            ",
					member.getFieldName());
			out.format("%s_s.append(%s.toString());\n", "            ",
					member.getFieldName());
			first = false;
		}
		out.format("            _s.append(\"]\");\n");
		out.format("%sreturn _s.toString();\n", "            ");
		out.format("%s}\n\n", "        ");
	}

	private void writeEmbeddedIdGettersAndSetters(PrintStream out,
			ClassInfo info) {
		for (MyIdAttribute member : info.getPrimaryIdAttributeMembers()) {
			out.format("%spublic %s get%s(){\n", "        ",
					info.addType(member.getType().getType()),
					Util.upperFirst(member.getFieldName()));
			out.format("%sreturn %s;\n", "            ", member.getFieldName());
			out.format("%s}\n\n", "        ");

			out.format("%spublic void set%s(%s %s){\n", "        ",
					Util.upperFirst(member.getFieldName()),
					info.addType(member.getType().getType()),
					member.getFieldName());
			out.format("%sthis.%s=%s;\n", "            ",
					member.getFieldName(), member.getFieldName());
			out.format("%s}\n\n", "        ");
		}
	}

	private void writeEmbeddedIdField(PrintStream out, ClassInfo info) {
		out.format("    @%s\n", info.addType(EmbeddedId.class));
		out.format("    private %s %s;\n\n",
				info.getEmbeddedIdSimpleClassName(),
				info.getEmbeddedIdAttributeName());
	}

	private void writeEmbeddedIdFields(PrintStream out, ClassInfo info,
			Set<String> validationMethods) {
		for (MyIdAttribute member : info.getPrimaryIdAttributeMembers()) {
			if (member.getReferenceClass() == null) {
				writeFieldAnnotation(out, member.getColumnName(), false,
						"        ", member.getType(), true, true);
			} else {
				writeFieldAnnotation(out, member.getColumnName(), false,
						"        ", member.getType(), false, false);
			}
			out.format("%sprivate %s %s;\n\n", "        ",
					info.addType(member.getType().getType()),
					member.getFieldName());
			writeAttributeValidationMethod(out, member.getFieldName(),
					member.getType(), info, validationMethods, true);
		}
	}

	private void writeEmbeddedIdConstructor(PrintStream out, ClassInfo info) {
		// write constructor
		out.format("        public %s(", info.getEmbeddedIdSimpleClassName());
		boolean first = true;
		for (MyIdAttribute member : info.getPrimaryIdAttributeMembers()) {
			if (!first)
				out.format(", ");
			out.format("%s %s", info.addType(member.getType().getType()),
					member.getFieldName());
			first = false;
		}
		out.format(") {\n");
		first = true;
		for (MyIdAttribute member : info.getPrimaryIdAttributeMembers()) {
			out.format("            this.%s = %s;\n", member.getFieldName(),
					member.getFieldName());
			first = false;
		}
		out.format("        }\n\n");
	}

	private void writeUniqueIdMethod(PrintStream out, ClassInfo info) {
		out.format("    @%s\n", info.addType(Transient.class));
		out.format("    @%s\n", info.addType(Override.class));
		out.format("    public String uniqueId(){\n");
		out.format("        return %s.class.getName() + \":\" + getId();\n",
				info.getJavaClassSimpleName());
		out.format("    }\n\n");

	}

	private void writeNonIdIndependentAttributeMembers(PrintStream out,
			ClassInfo info, Set<String> validationMethods) {
		for (MyIndependentAttribute attribute : info
				.getNonIdIndependentAttributeMembers()) {
			writeIndependentAttributeMember(out, attribute, "    ");
			writeAttributeValidationMethod(out, attribute, info,
					validationMethods);
		}
	}

	private void writeAttributeValidationMethod(PrintStream out,
			MyIndependentAttribute attribute, ClassInfo info,
			Set<String> validationMethods) {
		writeAttributeValidationMethod(out, attribute.getFieldName(),
				attribute.getType(), info, validationMethods, false);
	}

	private void writeAttributeValidationMethod(PrintStream out,
			String fieldName, MyTypeDefinition type, ClassInfo info,
			Set<String> validationMethods, boolean inEmbeddedId) {

		String indent;
		if (inEmbeddedId)
			indent = "        ";
		else
			indent = "    ";
		MyType myType = type.getMyType();
		String attributeConstantIdentifier = Util
				.toJavaConstantIdentifier(fieldName);
		if (myType.equals(MyType.REAL) || myType.equals(MyType.INTEGER)) {
			out.format(
					"%sprivate static final %s %s_UPPER_LIMIT=new BigDecimal(\"%s\");\n",
					indent, info.addType(BigDecimal.class),
					attributeConstantIdentifier, type.getUpperLimit());
			out.format(
					"%sprivate static final %s %s_LOWER_LIMIT=new BigDecimal(\"%s\");\n\n",
					indent, info.addType(BigDecimal.class),
					attributeConstantIdentifier, type.getLowerLimit());
		}
		String validationMethodName = "validate" + Util.upperFirst(fieldName);
		if (inEmbeddedId)
			validationMethods.add("id." + validationMethodName);
		else
			validationMethods.add(validationMethodName);
		out.format("%sprivate void %s() {\n", indent, validationMethodName);
		if (myType.equals(MyType.REAL) || myType.equals(MyType.INTEGER)) {
			out.format("%s    if (%s_UPPER_LIMIT.doubleValue() < %s) \n",
					indent, attributeConstantIdentifier, fieldName);
			out.format(
					"%s        throw new %s(\"upper limit of %s failed\");\n",
					indent, info.addType(RuntimeException.class), type
							.getUpperLimit().toString());
			out.format("%s    if (%s_LOWER_LIMIT.doubleValue() > %s)\n",
					indent, attributeConstantIdentifier, fieldName);
			out.format(
					"%s         throw new %s(\"lower limit of %s failed\");\n",
					indent, info.addType(RuntimeException.class), type
							.getLowerLimit().toString());
		} else if (myType.equals(MyType.STRING)) {
			if (type.getMinLength().intValue() > 0) {
				out.format("%s    if (%s == null || %s.length() < %s)\n",
						indent, fieldName, fieldName, type.getMinLength()
								.toString());
				out.format(
						"%s        throw new %s(\"min length constraint not met\");\n",
						indent, info.addType(RuntimeException.class));
			}
			if (type.getPrefix() != null) {
				out.format(
						"%s     if (%s == null || !%s.startsWith(\"%s\"))\n",
						indent, fieldName, fieldName,
						escapeJava(type.getPrefix()));
				out.format(
						"%s        throw new %s(\"prefix constraint not met\");\n",
						indent, info.addType(RuntimeException.class));
			}
			if (type.getSuffix() != null) {
				out.format("%s    if (%s == null || !%s.endsWith(\"%s\"))\n",
						indent, fieldName, fieldName,
						escapeJava(type.getSuffix()));
				out.format(
						"%s        throw new %s(\"suffix constraint not met\");\n",
						indent, info.addType(RuntimeException.class));
			}
			if (type.getValidationPattern() != null) {
				out.format(
						"%s    if (%s == null || !%s.matches(\"%s\", %s))\n",
						indent, fieldName, info.addType(Pattern.class),
						escapeJava(type.getValidationPattern()), fieldName);
				out.format(
						"%s        throw new %s(\"validation pattern constraint not met\");\n",
						indent, info.addType(RuntimeException.class));
			}
		}

		out.format("    }\n\n");
	}

	private void writeStateMember(PrintStream out, ClassInfo info) {
		if (info.hasBehaviour()) {
			info.addType(Column.class);
			jd(out, STATE_COMMENT, "    ");
			out.format("    @%s(name=\"state\",nullable=false)\n",
					info.addType(Column.class));
			out.format("    private String state;\n\n");
		}
	}

	private void writeReferenceMembers(PrintStream out, ClassInfo info,
			Set<String> validationMethods) {
		for (MyReferenceMember ref : info.getReferenceMembers()) {
			jd(out, ref.getThisMult() + " " + info.getJavaClassSimpleName()
					+ " " + ref.getThatVerbClause() + " " + ref.getThatMult()
					+ " " + ref.getSimpleClassName(), "    ");
			if (isRelationship(ref, Mult.ONE, Mult.ONE)) {
				// make an arbitrary deterministic decision about which side is
				// annotated in which way
				if (info.getJavaClassSimpleName().compareTo(
						ref.getSimpleClassName()) < 0) {
					writeValidationNotNull(out, ref.getFieldName(),
							validationMethods);
					out.format(
							"    @%s(mappedBy=\"%s\",fetch=%s.LAZY,targetEntity=%s.class)\n",
							info.addType(OneToOne.class),
							ref.getThisFieldName(),
							info.addType(FetchType.class),
							info.addType(ref.getFullClassName()));
					writeField(out, ref);
				} else {
					out.format(
							"    @%s(targetEntity=%s.class,cascade=%s.ALL,fetch=%s.LAZY)\n",
							info.addType(OneToOne.class),
							info.addType(ref.getFullClassName()),
							info.addType(CascadeType.class),
							info.addType(FetchType.class));

					writeJoinColumnsAnnotation(out, ref, false,
							!ref.isInPrimaryId(), !ref.isInPrimaryId());
					writeField(out, ref);
				}
			} else if (isRelationship(ref, Mult.ONE, Mult.ZERO_ONE)) {
				if (isUnary(ref, info)) {
					out.format(
							"    @%s(targetEntity=%s.class,cascade=%s.ALL,fetch=%s.LAZY)\n",
							info.addType(OneToOne.class),
							info.addType(ref.getFullClassName()),
							info.addType(CascadeType.class),
							info.addType(FetchType.class));

					writeJoinColumnsAnnotation(out, ref, true,
							!ref.isInPrimaryId(), !ref.isInPrimaryId());
					writeField(out, ref);
				} else {
					out.format(
							"    @%s(mappedBy=\"%s\",fetch=%s.LAZY,targetEntity=%s.class)\n",
							info.addType(OneToOne.class),
							ref.getThisFieldName(),
							info.addType(FetchType.class),
							info.addType(ref.getFullClassName()));
					writeField(out, ref);
				}
			} else if (isRelationship(ref, Mult.ZERO_ONE, Mult.ONE)) {
				out.format(
						"    @%s(targetEntity=%s.class,cascade=%s.ALL,fetch=%s.LAZY)\n",
						info.addType(OneToOne.class),
						info.addType(ref.getFullClassName()),
						info.addType(CascadeType.class),
						info.addType(FetchType.class));
				writeJoinColumnsAnnotation(out, ref, false,
						!ref.isInPrimaryId(), !ref.isInPrimaryId());
				writeField(out, ref);
			} else if (isRelationship(ref, Mult.ONE, Mult.MANY)) {
				out.format(
						"    @%s(mappedBy=\"%s\",cascade=%s.ALL,fetch=%s.LAZY,targetEntity=%s.class)\n",
						info.addType(OneToMany.class), ref.getThisFieldName(),
						info.addType(CascadeType.class),
						info.addType(FetchType.class),
						info.addType(ref.getFullClassName()));
				writeMultipleField(out, ref);
			} else if (isRelationship(ref, Mult.MANY, Mult.ONE)) {
				out.format("    @%s(targetEntity=%s.class,fetch=%s.LAZY)\n",
						info.addType(ManyToOne.class),
						ref.getSimpleClassName(), info.addType(FetchType.class));
				writeJoinColumnsAnnotation(out, ref, false,
						!ref.isInPrimaryId(), !ref.isInPrimaryId());
				writeField(out, ref);
			} else if (isRelationship(ref, Mult.ONE, Mult.ONE_MANY)) {
				writeValidationNotEmpty(out, ref.getFieldName(),
						validationMethods);
				out.format(
						"    @%s(mappedBy=\"%s\",cascade=%s.ALL,fetch=%s.LAZY,targetEntity=%s.class)\n",
						info.addType(OneToMany.class), ref.getThisFieldName(),
						info.addType(CascadeType.class),
						info.addType(FetchType.class),
						info.addType(ref.getFullClassName()));
				writeMultipleField(out, ref);
			} else if (isRelationship(ref, Mult.ONE_MANY, Mult.ONE)) {
				writeValidationNotNull(out, ref.getFieldName(),
						validationMethods);
				out.format("    @%s(targetEntity=%s.class)\n",
						info.addType(ManyToOne.class),
						info.addType(ref.getFullClassName()));
				writeJoinColumnsAnnotation(out, ref, false,
						!ref.isInPrimaryId(), !ref.isInPrimaryId());
				writeField(out, ref);
			} else if (isRelationship(ref, Mult.ZERO_ONE, Mult.ZERO_ONE)) {
				if (info.getJavaClassSimpleName().compareTo(
						ref.getSimpleClassName()) < 0) {
					// primary
					out.format("    //primary side of relationship\n");
					out.format(
							"    @%s(mappedBy=\"%s\",fetch=%s.LAZY,targetEntity=%s.class)\n",
							info.addType(OneToOne.class),
							ref.getThisFieldName(),
							info.addType(FetchType.class),
							info.addType(ref.getFullClassName()));
				} else {
					// secondary
					out.format("    //secondary side of relationship\n");
					out.format(
							"    @%s(targetEntity=%s.class,fetch=%s.LAZY)\n",
							info.addType(OneToOne.class),
							info.addType(ref.getFullClassName()),
							info.addType(FetchType.class));
					writeJoinColumnsAnnotation(out, ref, true,
							!ref.isInPrimaryId(), !ref.isInPrimaryId());
				}
				writeField(out, ref);
			} else if (isRelationship(ref, Mult.ZERO_ONE, Mult.MANY)) {
				out.format(
						"    @%s(mappedBy=\"%s\",cascade=%s.ALL,fetch=%s.LAZY,targetEntity=%s.class)\n",
						info.addType(OneToMany.class), ref.getThisFieldName(),
						info.addType(CascadeType.class),
						info.addType(FetchType.class),
						info.addType(ref.getFullClassName()));
				writeMultipleField(out, ref);
			} else if (isRelationship(ref, Mult.MANY, Mult.ZERO_ONE)) {
				out.format("    @%s(targetEntity=%s.class,fetch=%s.LAZY)\n",
						info.addType(ManyToOne.class),
						info.addType(ref.getFullClassName()),
						info.addType(FetchType.class));
				writeJoinColumnsAnnotation(out, ref, true,
						!ref.isInPrimaryId(), !ref.isInPrimaryId());
				writeField(out, ref);
			} else if (isRelationship(ref, Mult.ZERO_ONE, Mult.ONE_MANY)) {
				writeValidationNotEmpty(out, ref.getFieldName(),
						validationMethods);
				out.format(
						"    @%s(mappedBy=\"%s\",cascade=%s.ALL,fetch=%s.LAZY,targetEntity=%s.class)\n",
						info.addType(OneToMany.class), ref.getThisFieldName(),
						info.addType(CascadeType.class),
						info.addType(FetchType.class),
						info.addType(ref.getFullClassName()));
				writeMultipleField(out, ref);
			} else if (isRelationship(ref, Mult.ONE_MANY, Mult.ZERO_ONE)) {
				out.format("    @%s(targetEntity=%s.class,fetch=%s.LAZY)\n",
						info.addType(ManyToOne.class),
						info.addType(ref.getFullClassName()),
						info.addType(FetchType.class));
				writeJoinColumnsAnnotation(out, ref, true,
						!ref.isInPrimaryId(), !ref.isInPrimaryId());
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

	private boolean isUnary(MyReferenceMember ref, ClassInfo info) {
		return ref.getFullClassName().equals(info.getClassFullName());
	}

	private void writeValidationNotEmpty(PrintStream out, String fieldName,
			Set<String> validationMethods) {
		validationMethods.add("_validate" + Util.upperFirst(fieldName));
		out.format("    private void _validate%s() {\n",
				Util.upperFirst(fieldName));
		out.format("        if (%s.isEmpty())\n", fieldName);
		out.format(
				"            throw new %s(\"%s not established and is mandatory\");\n",
				info.addType(RelationshipNotEstablished.class), "?");
		out.format("    }\n\n");

	}

	private void writeValidationNotNull(PrintStream out, String fieldName,
			Set<String> validationMethods) {
		validationMethods.add("_validate" + Util.upperFirst(fieldName));
		out.format("    private void _validate%s() {\n",
				Util.upperFirst(fieldName));
		out.format("        if (%s == null)\n", fieldName);
		out.format(
				"            throw new %s(\"%s not established and is mandatory\");\n",
				info.addType(RelationshipNotEstablished.class), "?");
		out.format("    }\n\n");
	}

	private void writeJoinColumnsAnnotation(PrintStream out,
			MyReferenceMember ref, boolean nullable, boolean insertable,
			boolean updatable) {
		out.format("    @%s(value={\n", info.addType(JoinColumns.class));
		boolean first = true;
		for (xuml.tools.model.compiler.ClassInfo.JoinColumn col : ref
				.getJoinColumns()) {
			if (!first)
				out.format(",\n");
			first = false;
			out.format(
					"        @%s(name=\"%s\",referencedColumnName=\"%s\",nullable=%s,insertable=%s,updatable=%s)",
					info.addType(JoinColumn.class), col.getThisColumnName(),
					col.getOtherColumnName(), nullable, insertable, updatable);
		}
		out.format("})\n");
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

	private void writeNonIdIndependentAttributeGettersAndSetters(
			PrintStream out, ClassInfo info) {
		for (MyIndependentAttribute attribute : info
				.getNonIdIndependentAttributeMembers()) {
			writeIndependentAttributeGetterAndSetter(out, attribute);
		}
	}

	private void writeStateGetterAndSetter(PrintStream out, ClassInfo info) {
		if (info.hasBehaviour()) {
			jd(out, STATE_COMMENT, "    ");
			out.format("    public String getState(){\n");
			out.format("        return state;\n");
			out.format("    }\n\n");
			jd(out, STATE_COMMENT, "    ");
			out.format("    public void setState(String state){\n");
			out.format("        this.state= state;\n");
			out.format("    }\n\n");
		}
	}

	private void writeStates(PrintStream out, ClassInfo info) {
		if (info.hasBehaviour()) {
			jd(out,
					"The list of all states from the state machine for this entity.",
					"    ");
			out.format("    public static enum State {\n");
			boolean first = true;
			out.format("        ");
			for (String state : info.getStateNames()) {
				if (!first)
					out.format(",");
				out.format(info.getStateAsJavaIdentifier(state));
				first = false;
			}
			out.format(";\n");
			out.format("    }\n\n");
		}
	}

	private void writeEvents(PrintStream out, ClassInfo info) {
		List<MyEvent> events = info.getEvents();
		if (events.size() == 0)
			return;

		// create Events static class and each Event declared within
		jd(out, "Event declarations.", "    ");
		out.format("    public static class Events {\n\n");

		// write state names that have signatures
		Map<String, MyEvent> stateEvent = Maps.newHashMap();
		for (MyEvent event : info.getEvents()) {
			if (event.getStateName() != null)
				stateEvent.put(event.getStateName(), event);
		}

		for (MyEvent event : stateEvent.values()) {
			out.format("        public static interface %s {\n\n",
					event.getStateSignatureInterfaceSimpleName());
			// getters
			for (MyParameter p : event.getParameters()) {
				out.format("            %s get%s();\n\n",
						info.addType(p.getType()),
						Util.upperFirst(p.getFieldName()));

			}
			out.format("        }\n\n");
		}

		for (MyEvent event : info.getEvents()) {
			String stateSignatureImplements;
			if (event.getStateName() != null)
				stateSignatureImplements = ", "
						+ event.getStateSignatureInterfaceSimpleName();
			else
				stateSignatureImplements = "";
			String creationEventImplements;
			if (event.getCreates()) {
				creationEventImplements = ", "
						+ info.addType(CreationEvent.class) + "<"
						+ info.getJavaClassSimpleName() + ">";
			} else
				creationEventImplements = "";
			out.format("        @%s(\"serial\")\n",
					info.addType(SuppressWarnings.class));
			out.format(
					"        public static class %s implements %s<%s>, %s%s%s {\n\n",
					event.getSimpleClassName(), info.addType(Event.class),
					info.getJavaClassSimpleName(),
					info.addType(Serializable.class), stateSignatureImplements,
					creationEventImplements);

			StringBuilder constructorBody = new StringBuilder();
			for (MyParameter p : event.getParameters()) {
				constructorBody.append("                this."
						+ p.getFieldName() + " = " + p.getFieldName() + ";\n");
			}

			StringBuilder constructor = new StringBuilder();
			constructor.append("            public "
					+ event.getSimpleClassName() + "(");
			boolean first = true;
			for (MyParameter p : event.getParameters()) {
				out.format("            private final %s %s;\n",
						info.addType(p.getType()), p.getFieldName());
				if (!first)
					constructor.append(", ");
				constructor.append(info.addType(p.getType()) + " "
						+ p.getFieldName());
				first = false;
			}
			constructor.append("){\n");
			constructor.append(constructorBody);
			constructor.append("            }\n");
			out.println();
			out.println(constructor);

			// getters
			for (MyParameter p : event.getParameters()) {
				out.format("            public %s get%s(){\n",
						info.addType(p.getType()),
						Util.upperFirst(p.getFieldName()));
				out.format("                return %s;\n", p.getFieldName());
				out.format("            }\n\n");
			}
			out.format("        }\n\n");

		}
		out.format("    }\n\n");
	}

	private void writePreUpdateCheck(PrintStream out, ClassInfo info,
			Set<String> validationMethods) {
		out.format("    @%s\n", info.addType(Transient.class));
		out.format("    @%s\n", info.addType(PreUpdate.class));
		out.format("    void validateBeforeUpdate(){\n");
		for (String methodName : validationMethods)
			out.format("        %s();\n", methodName);
		out.format("    }\n\n");
		out.format("    @%s\n", info.addType(Transient.class));
		out.format("    @%s\n", info.addType(PrePersist.class));
		out.format("    void validateBeforePersist(){\n");
		for (String methodName : validationMethods)
			out.format("        %s();\n", methodName);
		out.format("    }\n\n");
	}

	private boolean isRelationship(MyReferenceMember ref, Mult here, Mult there) {
		return ref.getThisMult().equals(here)
				&& ref.getThatMult().equals(there);
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
		out.format(
				"    @%s(targetEntity=%s.class,cascade=%s.ALL,fetch=%s.LAZY)\n",
				info.addType(ManyToMany.class),
				info.addType(ref.getFullClassName()),
				info.addType(CascadeType.class), info.addType(FetchType.class));
		out.format("        @%s(name=\"%s\",schema=\"%s\",\n", info
				.addType(JoinTable.class), ref.getManyToMany().getJoinTable(),
				ref.getManyToMany().getJoinTableSchema());
		out.format("            joinColumns=@%s(name=\"%s\"),\n", info
				.addType(JoinColumn.class), ref.getManyToMany()
				.getThisColumnName());
		out.format("            inverseJoinColumns=@%s(name=\"%s\"))\n", info
				.addType(JoinColumn.class), ref.getManyToMany()
				.getThatColumnName());
		writeMultipleField(out, ref);
	}

	private void writeManyToManySecondarySide(PrintStream out, ClassInfo info,
			MyReferenceMember ref) {
		out.format("    //secondary side of relationship\n");
		out.format(
				"    @ManyToMany(mappedBy=\"%s\",targetEntity=%s.class,cascade=%s.ALL,fetch=%s.LAZY)\n",
				info.addType(ManyToMany.class), ref.getThisFieldName(),
				info.addType(ref.getFullClassName()),
				info.addType(CascadeType.class), info.addType(FetchType.class));
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
		out.format("    public %s get%s(){\n", type, Util.upperFirst(fieldName));
		out.format("        return %s;\n", fieldName);
		out.format("    }\n\n");
		out.format("    public void set%s(%s %s){\n",
				Util.upperFirst(fieldName), type, fieldName);
		out.format("        this.%1$s=%1$s;\n", fieldName);
		out.format("    }\n\n");
	}

	private void writeMultipleField(PrintStream out, MyReferenceMember ref) {
		out.format("    private %s %s = %s.newHashSet();\n\n", info
				.addType(new Type(Set.class.getName(), new Type(ref
						.getFullClassName()))), ref.getFieldName(), info
				.addType(Sets.class));
		writeGetterAndSetter(out, info, ref.getSimpleClassName(),
				ref.getFullClassName(), ref.getFieldName(), true);
	}

	private void writeEventCallMethods(PrintStream out, ClassInfo info) {

		// add event call methods

		out.format("    @%s\n", info.addType(Transient.class));
		out.format("    @%s\n", info.addType(Override.class));
		out.format("    public %s signal(%s<%s> event){\n",
				info.getJavaClassSimpleName(), info.addType(Event.class),
				info.getJavaClassSimpleName());
		if (info.hasBehaviour())
			out.format("        helper().signal(event);\n");
		else
			out.format("        //no behaviour for this class\n");
		out.format("        return this;\n");
		out.format("    }\n\n");
		out.format("    @%s\n", info.addType(Transient.class));
		out.format("    @%s\n", info.addType(Override.class));
		out.format("    public %s event(%s<%s> event){\n\n",
				info.getJavaClassSimpleName(), info.addType(Event.class),
				info.getJavaClassSimpleName());
		if (info.hasBehaviour()) {
			out.format("        helper().beforeEvent();\n\n");
			out.format("        // process the event\n");
			boolean first = true;
			for (MyEvent event : info.getEvents()) {
				out.format("        ");
				if (!first)
					out.format("else ");
				out.format("if (event instanceof Events.%s){\n",
						event.getSimpleClassName());
				out.format("            processEvent((Events.%s) event);\n",
						event.getSimpleClassName());
				out.format("        }\n");
				first = false;
			}
			out.println();
			out.format("        helper().afterEvent();\n");
		}
		out.format("        return this;\n");
		out.format("    }\n\n");
		if (info.hasBehaviour()) {
			for (MyEvent event : info.getEvents()) {

				jd(out, "Synchronously perform the change.", "    ");
				out.format("    @%s\n", info.addType(Transient.class));

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
						if (transition.getFromState() == null)
							// handle creation state
							out.format(" (state==null){\n");
						else
							out.format(
									" (state.equals(State.%s.toString())){\n",
									info.getStateAsJavaIdentifier(transition
											.getFromState()));
						out.format("            state=State.%s.toString();\n",
								info.getStateAsJavaIdentifier(transition
										.getToState()));
						out.format("            synchronized(this) {\n");
						out.format(
								"                _behaviour.onEntry%s(event);\n",
								Util.upperFirst(Util
										.toJavaIdentifier(transition
												.getToState())));
						out.format("            }\n");
						out.format("        }\n");
					}
				}
				out.format("    }\n\n");
			}
		}
	}

	private void writeStaticCreateMethods(PrintStream out, ClassInfo info) {
		if (info.hasBehaviour()) {
			for (MyTransition t : info.getTransitions()) {
				if (t.isCreationTransition()) {
					out.format(
							"    public static %s create(%s em, %s<%s> event) {\n",
							info.getJavaClassSimpleName(),
							info.addType(EntityManager.class),
							info.addType(CreationEvent.class),
							info.getJavaClassSimpleName());
					out.format("        %s entity = new %s();\n",
							info.getJavaClassSimpleName(),
							info.getJavaClassSimpleName());
					out.format("        entity.event(event);\n");
					out.format("        em.persist(entity);\n");
					out.format("        return entity;\n");
					out.format("    }\n\n");
				}
			}
		}
	}

	private void writeMergeMethod(PrintStream out, ClassInfo info) {
		out.format("    public %s merge(%s em) {\n",
				info.getJavaClassSimpleName(),
				info.addType(EntityManager.class));
		out.format("        return em.merge(this);\n");
		out.format("    }\n\n");
	}

	private void writePersistMethod(PrintStream out, ClassInfo info) {
		out.format("    public %s persist(%s em) {\n",
				info.getJavaClassSimpleName(),
				info.addType(EntityManager.class));
		out.format("        em.persist(this);\n");
		out.format("        return this;\n");
		out.format("    }\n\n");
	}

	private void writeRefreshMethod(PrintStream out, ClassInfo info) {
		out.format("    public %s refresh(%s em) {\n",
				info.getJavaClassSimpleName(),
				info.addType(EntityManager.class));
		out.format("        em.refresh(this);\n");
		out.format("        return this;\n");
		out.format("    }\n\n");
	}

	private void writeLoadMethod(PrintStream out, ClassInfo info) {
		out.format("    public %s load(%s em) {\n",
				info.getJavaClassSimpleName(),
				info.addType(EntityManager.class));
		out.format("        return merge(em).refresh(em);\n");
		out.format("    }\n\n");

		out.format("    public %s load() {\n", info.getJavaClassSimpleName());
		out.format("        return Context.load(this);\n");
		out.format("    }\n\n");

	}

	private void writeToStringMethod(PrintStream out, ClassInfo info) {
		// TODO
	}

	private void writeBehaviourInterface(PrintStream out, ClassInfo info) {

		if (info.getEvents().size() == 0)
			return;

		out.format("    public static interface Behaviour {\n\n");

		// write state names that have signatures
		Map<String, MyEvent> stateEvent = Maps.newLinkedHashMap();
		for (MyEvent event : info.getEvents()) {
			if (event.getStateName() != null)
				stateEvent.put(event.getStateName(), event);
		}
		List<MyEvent> nonStateEvents = Lists.newArrayList();
		for (MyEvent event : info.getEvents()) {
			if (event.getStateName() == null)
				nonStateEvents.add(event);
		}

		for (String state : stateEvent.keySet()) {
			MyEvent event = stateEvent.get(state);
			out.format("        void onEntry%s(Events.%s event);\n\n", Util
					.upperFirst(Util.toJavaIdentifier(event.getStateName())),
					event.getStateSignatureInterfaceSimpleName());
		}

		for (MyEvent event : nonStateEvents) {
			for (MyTransition transition : info.getTransitions()) {
				// constraint is no event overloading
				if (transition.getEventName().equals(event.getName())) {
					out.format("        void onEntry%s(Events.%s event);\n\n",
							Util.upperFirst(Util.toJavaIdentifier(transition
									.getToState())), event.getSimpleClassName());
				}
			}
		}

		out.format("    }\n\n");
	}

	private void writeBehaviourFactoryInterface(PrintStream out, ClassInfo info) {
		if (info.getEvents().size() == 0)
			return;
		out.format("    public static interface BehaviourFactory {\n\n");
		out.format("        Behaviour create(%s entity);\n\n",
				info.getJavaClassSimpleName());
		out.format("    }\n\n");
	}

	private void writeIndependentAttributeMember(PrintStream out,
			MyIndependentAttribute attribute, String indent) {
		writeIndependentAttributeMember(out, attribute.getFieldName(),
				attribute.getColumnName(), attribute.isNullable(), indent,
				attribute.getType());
	}

	private void writeIndependentAttributeMember(PrintStream out,
			String fieldName, String columnName, boolean isNullable,
			String indent, MyTypeDefinition type) {
		writeFieldAnnotation(out, columnName, isNullable, indent, type, true,
				true);
		out.format("%sprivate %s %s;\n\n", indent,
				info.addType(type.getType()), fieldName);
	}

	private void writeFieldAnnotation(PrintStream out, String columnName,
			boolean isNullable, String indent, MyTypeDefinition type,
			boolean insertable, boolean updatable) {
		final String length;
		if (type.getMyType().equals(MyType.STRING))
			length = ",length=" + type.getMaxLength();
		else
			length = "";
		final String insertableParameter;
		if (!insertable)
			insertableParameter = ",insertable=false";
		else
			insertableParameter = "";
		final String updatableParameter;
		if (!updatable)
			updatableParameter = ",updatable=false";
		else
			updatableParameter = "";
		final String precision;
		if (type.getMyType().equals(MyType.REAL))
			precision = ",precision=" + type.getPrecision();
		else
			precision = "";

		out.format("%s@%s(name=\"%s\",nullable=%s%s%s%s%s)\n", indent,
				info.addType(Column.class), columnName, isNullable, length,
				insertableParameter, updatableParameter, precision);
		if (type.getMyType().equals(MyType.DATE))
			out.format("%s@%s(%s.DATE)\n", indent,
					info.addType(Temporal.class),
					info.addType(TemporalType.class));
		else if (type.getMyType().equals(MyType.TIMESTAMP))
			out.format("%s@%s(%s.TIMESTAMP)\n", indent,
					info.addType(Temporal.class),
					info.addType(TemporalType.class));

	}

	private void writeIndependentAttributeGetterAndSetter(PrintStream out,
			MyIndependentAttribute attribute) {
		String type = info.addType(attribute.getType().getType());
		jd(out, "Returns " + attribute.getFieldName() + ".", "    ");
		if (attribute.getFieldName().equals("id")) {
			info.addType(Override.class);
			out.format("    @Override\n");
		}
		out.format("    public %s get%s(){\n", type,
				Util.upperFirst(attribute.getFieldName()));
		out.format("        return %s;\n", attribute.getFieldName());
		out.format("    }\n\n");

		jd(out, "Sets " + attribute.getFieldName() + " to the given value.",
				"    ");
		out.format("    public void set%s(%s %s){\n",
				Util.upperFirst(attribute.getFieldName()), type,
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
		out.println(info.getImports(info.getClassFullName()));
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
