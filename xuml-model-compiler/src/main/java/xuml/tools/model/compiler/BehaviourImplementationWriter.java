package xuml.tools.model.compiler;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.ByteArrayOutputStream;

import xuml.tools.model.compiler.ClassInfoBase.MyEvent;
import xuml.tools.model.compiler.ClassInfoBase.MyTransition;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class BehaviourImplementationWriter {

	private final ClassInfo info;
	private final String behaviourFullClassName;
	private final TypeRegister types = new TypeRegister();

	public BehaviourImplementationWriter(ClassInfo info,
			String behaviourFullClassName) {
		this.info = info;
		this.behaviourFullClassName = behaviourFullClassName;
	}

	public String generate() {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bytes);
		generate(out);
		out.close();
		String result = bytes.toString().replace("<IMPORTS>",
				types.getImports(behaviourFullClassName).trim());
		return result;
	}

	private void generate(PrintStream out) {

		out.format("package %s;\n\n", Util.getPackage(behaviourFullClassName));
		out.format("<IMPORTS>\n\n");
		out.format("public class %s implements %s {\n\n",
				Util.getSimpleClassName(behaviourFullClassName),
				types.addType(info.getClassFullName() + ".Behaviour"));

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

		for (MyEvent event : stateEvent.values()) {
			out.format("    public void onEntry%s(%s event) {\n", Util
					.upperFirst(Util.toJavaIdentifier(event.getStateName())),
					types.addType(info.getClassFullName() + ".Events."
							+ event.getStateSignatureInterfaceSimpleName()));
			out.format("        //TODO write implementation here\n");
			out.format("    };\n\n");
		}

		for (MyEvent event : nonStateEvents) {
			for (MyTransition transition : info.getTransitions()) {
				// constraint is no event overloading
				if (transition.getEventName().equals(event.getName())) {
					out.format("    public void onEntry%s(%s event) {\n", Util
							.upperFirst(Util.toJavaIdentifier(transition
									.getToState())), types.addType(info
							.getClassFullName()
							+ ".Events."
							+ event.getSimpleClassName()));
					out.format("        //TODO write implementation here\n");
					out.format("    };\n\n");
				}
			}
		}

		out.format("}");
	}
}
