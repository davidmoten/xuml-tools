package ordertracker.rs;

import java.io.IOException;

import org.eclipse.jetty.servlets.EventSource;

public class MyEventSource implements EventSource {

	private Emitter emitter;

	@Override
	public void onOpen(Emitter emitter) throws IOException {
		this.emitter = emitter;
	}

	@Override
	public void onClose() {
		// TODO Auto-generated method stub

	}

}
