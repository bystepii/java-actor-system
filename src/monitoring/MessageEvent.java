package monitoring;

import actors.Actor;
import messages.Message;

public class MessageEvent<T> extends ActorEvent {
    private final Message<T> message;

    public MessageEvent(Actor source, EventType eventType, Message<T> message) {
        super(source, eventType);
        this.message = message;
    }

    public Message<T> getMessage() {
        return message;
    }
}
