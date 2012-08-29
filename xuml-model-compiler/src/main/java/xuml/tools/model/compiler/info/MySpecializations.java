package xuml.tools.model.compiler.info;

import java.math.BigInteger;
import java.util.Set;

public class MySpecializations {
	private final BigInteger rnum;
	private final Set<String> fieldNames;

	public MySpecializations(BigInteger rnum, Set<String> fieldNames) {
		super();
		this.rnum = rnum;
		this.fieldNames = fieldNames;
	}

	public BigInteger getRnum() {
		return rnum;
	}

	public Set<String> getFieldNames() {
		return fieldNames;
	}

}