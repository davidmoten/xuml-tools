package ordertracker.rs;

import java.io.IOException;

import ordertracker.EventService;

import org.eclipse.jetty.servlets.EventSource;

import rx.Subscription;
import rx.functions.Action1;

public class MyEventSource implements EventSource {

	private volatile Subscription subscription;

	@Override
	public void onOpen(final Emitter emitter) throws IOException {
		subscription = EventService.instance().events().subscribe(new Action1<String>() {

			@Override
			public void call(String data) {
				try {
					emitter.data(data);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}});
	}

	@Override
	public void onClose() {
		subscription.unsubscribe();
	}

}
