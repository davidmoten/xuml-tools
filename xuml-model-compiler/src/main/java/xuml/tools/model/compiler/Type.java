package xuml.tools.model.compiler;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

public class Type {
	private final String base;
	private final List<Type> generics;
	private final boolean isArray;

	public Type(String base, List<Type> generics, boolean isArray) {
		super();
		this.base = base;
		if (generics != null)
			this.generics = generics;
		else
			this.generics = Lists.newArrayList();
		this.isArray = isArray;
	}

	public Type(String base, Type generic) {
		this(base, Arrays.asList(new Type[] { generic }), false);
	}

	public Type(Class<?> cls) {
		this(cls.getName(), null, false);
	}

	public Type(Class<?> cls, Type... generic) {
		this(cls.getName(), Arrays.asList(generic), false);
	}

	public Type(String base) {
		this(base, null, false);
	}

	public String getBase() {
		return base;
	}

	public List<Type> getGenerics() {
		return generics;
	}

	public boolean isArray() {
		return isArray;
	}
}
