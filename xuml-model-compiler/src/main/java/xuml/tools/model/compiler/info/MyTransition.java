package xuml.tools.model.compiler.info;

public class MyTransition {
    private final String eventName;
    private final String eventSimpleClassName;
    private final String fromState;
    private final String toState;
    private final String eventId;

    public MyTransition(String eventName, String eventSimpleClassName, String eventId,
            String fromState, String toState) {
        this.eventName = eventName;
        this.eventSimpleClassName = eventSimpleClassName;
        this.eventId = eventId;
        this.fromState = fromState;
        this.toState = toState;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventSimpleClassName() {
        return eventSimpleClassName;
    }

    public String getEventId() {
        return eventId;
    }

    public String getFromState() {
        return fromState;
    }

    public String getToState() {
        return toState;
    }

    public boolean isCreationTransition() {
        return fromState == null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MyTransition [eventName=");
        builder.append(eventName);
        builder.append(", fromState=");
        builder.append(fromState);
        builder.append(", toState=");
        builder.append(toState);
        builder.append(", eventId=");
        builder.append(eventId);
        builder.append("]");
        return builder.toString();
    }
}