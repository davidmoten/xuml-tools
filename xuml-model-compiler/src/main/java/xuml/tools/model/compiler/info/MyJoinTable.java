package xuml.tools.model.compiler.info;

import java.util.List;


public class MyJoinTable {
	private final String joinTable;
	private final String joinTableSchema;
	private final List<MyJoinColumn> joinColumns;
	private final List<MyJoinColumn> inverseJoinColumns;

	public MyJoinTable(String joinTable, String joinTableSchema,
			List<MyJoinColumn> joinColumns,
			List<MyJoinColumn> inverseJoinColumns) {
		super();
		this.joinTable = joinTable;
		this.joinTableSchema = joinTableSchema;
		this.joinColumns = joinColumns;
		this.inverseJoinColumns = inverseJoinColumns;
	}

	public String getJoinTable() {
		return joinTable;
	}

	public String getJoinTableSchema() {
		return joinTableSchema;
	}

	public List<MyJoinColumn> getJoinColumns() {
		return joinColumns;
	}

	public List<MyJoinColumn> getInverseJoinColumns() {
		return inverseJoinColumns;
	}

}