package ordertracker.rs;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.servlets.EventSource;
import org.eclipse.jetty.servlets.EventSourceServlet;

public class MyEventSourceServlet extends EventSourceServlet {

	private static final long serialVersionUID = 6453381546572793298L;

	@Override
	protected EventSource newEventSource(HttpServletRequest req) {
		System.out.println("newEventSource called");
		return new MyEventSource();
	}

}
