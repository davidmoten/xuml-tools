package xuml.tools.jaxb.compiler.message;

import xuml.tools.jaxb.compiler.Entity;

public interface HasEntity<T, R> {
	Entity<T, R> getEntity();
}
