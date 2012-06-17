package xuml.tools.jaxb.compiler;

import static com.google.common.collect.Lists.newArrayList;
import static xuml.tools.jaxb.compiler.Util.toJavaConstantIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ClassInfoSample extends ClassInfo {

	private final TypeRegister types = new TypeRegister();

	/*
	 * (non-Javadoc)
	 * 
	 * @see xuml.tools.jaxb.compiler.IClassInfo#getPackage()
	 */
	@Override
	public String getPackage() {
		return "miuml";
	}

	@Override
	public String getClassDescription() {
		return "Represents a Class from the miUML metamodel.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xuml.tools.jaxb.compiler.IClassInfo#getUniqueConstraintColumnNames()
	 */
	@Override
	public List<List<String>> getUniqueConstraintColumnNames() {
		List<List<String>> list = newArrayList();
		List<String> list1 = newArrayList("name", "domain");
		List<String> list2 = newArrayList("domain", "number");
		list.add(list1);
		list.add(list2);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xuml.tools.jaxb.compiler.IClassInfo#getSchema()
	 */
	@Override
	public String getSchema() {
		return "xuml";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xuml.tools.jaxb.compiler.IClassInfo#getTable()
	 */
	@Override
	public String getTable() {
		return "t_class";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xuml.tools.jaxb.compiler.IClassInfo#getJavaClassSimpleName()
	 */
	@Override
	public String getJavaClassSimpleName() {
		return "Class";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xuml.tools.jaxb.compiler.IClassInfo#getOperations()
	 */
	@Override
	public List<String> getOperations() {
		ArrayList<String> list = newArrayList("validate");
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xuml.tools.jaxb.compiler.IClassInfo#getPrimaryId()
	 */
	@Override
	public List<MyPrimaryIdAttribute> getPrimaryIdAttributeMembers() {
		return Lists.newArrayList(new MyPrimaryIdAttribute("class id", "id1",
				"class_id", "org.moten.david.School", "class_col", new Type(
						"Long")), new MyPrimaryIdAttribute("domain", "id2",
				"domain_id", new Type("Long")));
	}

	/*
	 * 00 (non-Javadoc)
	 * 
	 * @see
	 * xuml.tools.jaxb.compiler.IClassInfo#getNonIdIndependentAttributeMembers()
	 */
	@Override
	public List<MyIndependentAttribute> getNonIdIndependentAttributeMembers() {
		List<MyIndependentAttribute> list = newArrayList();
		list.add(new MyIndependentAttribute("description", "desc", new Type(
				"String"), true,
				"a description of the class in terms of its role in the xuml domain"));
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xuml.tools.jaxb.compiler.IClassInfo#getEvents()
	 */
	@Override
	public List<MyEvent> getEvents() {
		List<MyEvent> list = newArrayList();
		List<MyParameter> params = newArrayList();
		params.add(new MyParameter("message", "String"));
		list.add(new MyEvent("Validation Error", "ValidationError", params));
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xuml.tools.jaxb.compiler.IClassInfo#getStateNames()
	 */
	@Override
	public List<String> getStateNames() {
		List<String> list = newArrayList();
		list.add("Valid");
		list.add("Invalid");
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xuml.tools.jaxb.compiler.IClassInfo#getTransitions()
	 */
	@Override
	public List<MyTransition> getTransitions() {
		List<MyTransition> list = newArrayList();
		list.add(new MyTransition("Validation Error", "2", "Valid", "Invalid"));
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * xuml.tools.jaxb.compiler.IClassInfo#getStateIdentifier(java.lang.String)
	 */
	@Override
	public String getStateIdentifier(String state) {
		return toJavaConstantIdentifier(state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xuml.tools.jaxb.compiler.IClassInfo#isSuperclass()
	 */
	@Override
	public boolean isSuperclass() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xuml.tools.jaxb.compiler.IClassInfo#isSubclass()
	 */
	@Override
	public boolean isSubclass() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xuml.tools.jaxb.compiler.IClassInfo#getSubclassRole()
	 */
	@Override
	public MySubclassRole getSubclassRole() {
		return new MySubclassRole(getPackage() + ".Element",
				getJavaClassSimpleName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xuml.tools.jaxb.compiler.IClassInfo#getReferenceMembers()
	 */
	@Override
	public List<MyReferenceMember> getReferenceMembers() {

		// otherColumnName,thisName,otherName,manyToMany
		List<MyReferenceMember> list = newArrayList();
		list.add(new MyReferenceMember("Domain", getPackage() + ".Domain",
				Mult.ONE, Mult.ZERO_ONE, "models", "is modelled in", "domain",
				null, "class", null, null));
		list.add(new MyReferenceMember("Barge", getPackage() + ".Barge",
				Mult.ZERO_ONE, Mult.ONE, "carries", "is carried by", "barge",
				newArrayList(new JoinColumn("barge_id", "barge_id"),
						new JoinColumn("region_id", "region_id")), null, null,
				null));
		list.add(new MyReferenceMember("Wheel", getPackage() + ".Wheel",
				Mult.ONE, Mult.MANY, "helps move", "moves on", "wheel", null,
				"class", null, null));
		list.add(new MyReferenceMember("Insect", getPackage() + ".Insect",
				Mult.MANY, Mult.ONE, "bites", "is bitten by", "insect",
				newArrayList(new JoinColumn("insect_id", "insect_id")), null,
				null, null));
		list.add(new MyReferenceMember("Train", getPackage() + ".Train",
				Mult.ONE, Mult.ONE_MANY, "carries", "is carried by", "train",
				null, "class", null, null));
		list.add(new MyReferenceMember("Light", getPackage() + ".Light",
				Mult.ONE_MANY, Mult.ONE, "lights", "is lit by", "light",
				newArrayList(new JoinColumn("light_id", "light_id")), null,
				null, null));
		list.add(new MyReferenceMember("Mouse", getPackage() + ".Mouse",
				Mult.ZERO_ONE, Mult.ZERO_ONE, "scares", "is scared by",
				"mouse", null, "class", null, null));
		list.add(new MyReferenceMember("Ant", getPackage() + ".Ant",
				Mult.ZERO_ONE, Mult.ZERO_ONE, "nibbles", "is nibbled by",
				"ant", newArrayList(new JoinColumn("ant_id", "ant_id")), null,
				null, null));
		list.add(new MyReferenceMember("Aircraft", getPackage() + ".Aircraft",
				Mult.ZERO_ONE, Mult.MANY, "flies", "is flown by", "aircraft",
				null, "class", null, null));
		list.add(new MyReferenceMember("Balloon", getPackage() + ".Balloon",
				Mult.MANY, Mult.ZERO_ONE, "floats", "is floated by", "balloon",
				newArrayList(new JoinColumn("balloon_id", "balloon_id")), null,
				null, null));
		list.add(new MyReferenceMember("Mower", getPackage() + ".Mower",
				Mult.ZERO_ONE, Mult.MANY, "mows", "is mown by", "mower", null,
				"class", null, null));
		list.add(new MyReferenceMember("Chair", getPackage() + ".Chair",
				Mult.ONE_MANY, Mult.ZERO_ONE, "is sat on by", "sits on",
				"chair", newArrayList(new JoinColumn("chair_id", "chair_id")),
				null, null, null));
		list.add(new MyReferenceMember("Lemon", getPackage() + ".Lemon",
				Mult.MANY, Mult.MANY, "is sucked by", "sucks", "lemon", null,
				null, null, new MyManyToMany("class_lemon", getSchema(),
						"class_id", "lemon_id")));
		list.add(new MyReferenceMember("Abacus", getPackage() + ".Abacus",
				Mult.MANY, Mult.MANY, "is clicked by", "clicks", "abacus",
				null, "class", null, new MyManyToMany("class_lemon",
						getSchema(), "class_id", "abacus_id")));
		list.add(new MyReferenceMember("Lemon", getPackage() + ".Lemon",
				Mult.ONE_MANY, Mult.ONE_MANY, "is sucked by", "sucks", "lemon",
				null, null, null, new MyManyToMany("class_lemon", getSchema(),
						"class_id", "lemon_id")));
		list.add(new MyReferenceMember("Abacus", getPackage() + ".Abacus",
				Mult.ONE_MANY, Mult.ONE_MANY, "is clicked by", "clicks",
				"abacus", null, "class", null, new MyManyToMany("class_lemon",
						getSchema(), "class_id", "abacus_id")));
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xuml.tools.jaxb.compiler.IClassInfo#getAtLeastOneFieldChecks()
	 */
	@Override
	public Set<String> getAtLeastOneFieldChecks() {
		Set<String> set = Sets.newTreeSet();
		set.add("train");
		set.add("lemon");
		set.add("abacus");
		return set;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see xuml.tools.jaxb.compiler.IClassInfo#getImports()
	 */
	@Override
	public String getImports() {
		return types.getImports();
	}

	@Override
	public String getIdColumnName() {
		return "cls_id";
	}

	@Override
	public String getContextPackageName() {
		return "xuml";
	}

	@Override
	TypeRegister getTypes() {
		return types;
	}

	@Override
	Type getType(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
