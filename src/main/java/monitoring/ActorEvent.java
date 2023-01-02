package monitoring;

import java.util.EventObject;

public class ActorEvent extends EventObject {

    private final EventType eventType;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public ActorEvent(String source, EventType eventType) {
        super(source);
        this.eventType = eventType;
    }

    @Override
    public String getSource() {
        return (String) super.getSource();
    }

    public EventType getEventType() {
        return eventType;
    }

    public enum EventType {
        CREATED,
        STOPPED,
        ABORTED,
        MESSAGE_SENT,
        MESSAGE_RECEIVED
    }
}
