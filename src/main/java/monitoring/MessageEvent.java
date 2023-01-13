package monitoring;

import messages.Message;

/**
 * This class represents an event of a sent or received message.
 *
 * @param <T> the type of the message.
 */
public class MessageEvent<T extends Message<?>> extends ActorEvent {

    /**
     * The message that was sent or received.
     */
    private final T message;

    /**
     * The name of the message class
     */
    private final String messageClass;

    /**
     * Constructs a MessageEvent.
     *
     * @param source    the name of the Actor that generated the event.
     * @param eventType the type of the event.
     * @param message   the message that was sent or received.
     */
    public MessageEvent(String source, EventType eventType, T message) {
        super(source, eventType);
        this.message = message;
        this.messageClass = message.getClass().getSimpleName();
    }

    /**
     * Getter for the message that was sent or received.
     *
     * @return the message that was sent or received.
     */
    public T getMessage() {
        return message;
    }
}
