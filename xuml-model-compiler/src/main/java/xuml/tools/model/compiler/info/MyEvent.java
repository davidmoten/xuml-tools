package xuml.tools.model.compiler.info;

import java.util.List;

import xuml.tools.miuml.metamodel.jaxb.EventSignature;
import xuml.tools.miuml.metamodel.jaxb.StateSignature;

import com.google.common.base.Preconditions;

public class MyEvent {
	private final String name;
	private String simpleClassName;
	private final List<MyParameter> parameters;
	private final String stateName;
	private final String stateSignatureInterfaceSimpleName;
	private final boolean creates;

	public List<MyParameter> getParameters() {
		return parameters;
	}

	public MyEvent(String name, String simpleClassName,
			List<MyParameter> parameters, String stateName,
			String stateSignatureInterfaceSimpleName, boolean creates) {
		Preconditions.checkNotNull(parameters);
		this.name = name;
		this.simpleClassName = simpleClassName;
		this.stateName = stateName;
		this.stateSignatureInterfaceSimpleName = stateSignatureInterfaceSimpleName;
		this.creates = creates;
		this.parameters = parameters;
	}

	public String getName() {
		return name;
	}

	public String getSimpleClassName() {
		return simpleClassName;
	}

	public void setSimpleClassName(String simpleClassName) {
		this.simpleClassName = simpleClassName;
	}

	/**
	 * If the parameter list was obtained from the {@link StateSignature}
	 * rather than the {@link EventSignature} then this returns the state
	 * name.
	 * 
	 * @return
	 */
	public String getStateName() {
		return stateName;
	}

	public String getStateSignatureInterfaceSimpleName() {
		return stateSignatureInterfaceSimpleName;
	}

	public boolean getCreates() {
		return creates;
	}
}