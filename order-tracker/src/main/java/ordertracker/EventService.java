package ordertracker;

import rx.Observable;
import rx.subjects.PublishSubject;

public class EventService {

    private static EventService instance = new EventService();

    private final PublishSubject<String> subject = PublishSubject.create();

    public static EventService instance() {
        return instance;
    }

    public void event(String data) {
        subject.onNext(data);
    }

    public Observable<String> events() {
        return subject.serialize().asObservable();
    }

}
