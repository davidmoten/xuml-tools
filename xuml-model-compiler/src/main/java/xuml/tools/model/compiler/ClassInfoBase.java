package xuml.tools.model.compiler;


import java.util.List;
import java.util.Set;

import xuml.tools.model.compiler.info.MyEvent;
import xuml.tools.model.compiler.info.MyIdAttribute;
import xuml.tools.model.compiler.info.MyIndependentAttribute;
import xuml.tools.model.compiler.info.MyReferenceMember;
import xuml.tools.model.compiler.info.MySubclassRole;
import xuml.tools.model.compiler.info.MyTransition;

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

	public String getEmbeddedIdSimpleClassName() {
		return getJavaClassSimpleName() + "Id";
	}

	public String getEmbeddedIdAttributeName() {
		return "id";
	}

	public boolean hasBehaviour() {
		return getEvents().size() > 0;
	}
}