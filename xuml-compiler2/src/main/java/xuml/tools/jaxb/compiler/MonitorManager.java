package xuml.tools.jaxb.compiler;

import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Objects;
import com.google.common.util.concurrent.Monitor;

public class MonitorManager {

	ConcurrentHashMap<Group, Monitor> map = new ConcurrentHashMap<Group, Monitor>();

	public synchronized Monitor getMonitor(final Object... objects) {
		Group group = new Group(objects);
		Monitor m = map.get(group);
		if (m == null)
			return map.put(group, new Monitor());
		else
			return m;
	}

	private static class Group {
		private final Object[] objects;

		Group(Object... objects) {
			if (objects == null)
				throw new RuntimeException("objects parameter cannot be null");
			this.objects = objects;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(objects);
		}

		@Override
		public boolean equals(Object o) {
			if (o == null)
				return false;
			if (getClass() != o.getClass())
				return false;
			Group other = (Group) o;
			if (objects.length != other.objects.length)
				return false;
			for (int i = 0; i < objects.length; i++)
				if (!Objects.equal(objects[i], other.objects[i]))
					return false;
			return true;
		}
	}
}
