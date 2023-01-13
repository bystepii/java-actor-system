package monitoring;

/**
 * This class represents an event that occurs in an Actor.
 */
public class ActorEvent {

    /**
     * The source of the event.
     */
    private final String source;

    /**
     * The type of the event.
     */
    private final EventType eventType;

    /**
     * Constructs an ActorEvent.
     *
     * @param source    the name of the Actor that generated the event.
     * @param eventType the type of the event.
     * @throws IllegalArgumentException if source is null
     */
    public ActorEvent(String source, EventType eventType) {
        this.source = source;
        this.eventType = eventType;
    }

    /**
     * Getter for the source of the event.
     *
     * @return the source of the event.
     */
    public String getSource() {
        return source;
    }

    /**
     * Getter for the type of the event.
     *
     * @return the type of the event.
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * This enum represents the type of ActorEvent.
     */
    public enum EventType {
        /**
         * The Actor has been created.
         */
        CREATED,
        /**
         * The Actor has been stopped.
         */
        STOPPED,
        /**
         * The Actor has been aborted (due to an exception).
         */
        ABORTED,
        /**
         * The Actor has sent a message.
         */
        MESSAGE_SENT,
        /**
         * The Actor has fiºnished processing a message.
         */
        MESSAGE_PROCESSED,
        /**
         * The Actor has received a message.
         */
        MESSAGE_RECEIVED
    }
}
