package ordertracker.rs;

import java.io.IOException;

import org.eclipse.jetty.servlets.EventSource;

import rx.Observable;
import rx.subjects.PublishSubject;

public class MyEventSource implements EventSource {

	private static PublishSubject<String> subject = PublishSubject.create();
	private static Observable<String> shared = subject.share();

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
