package xuml.tools.model.compiler.message;

import xuml.tools.model.compiler.Entity;

public interface HasEntity<T> {
	Entity<T> getEntity();
}
