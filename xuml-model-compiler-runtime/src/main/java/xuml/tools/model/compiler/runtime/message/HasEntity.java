package xuml.tools.model.compiler.runtime.message;

import xuml.tools.model.compiler.runtime.Entity;

public interface HasEntity<T> {
	Entity<T> getEntity();
}
