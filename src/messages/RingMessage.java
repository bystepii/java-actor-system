package messages;

import actors.Actor;

public class RingMessage extends Message {
    public RingMessage(Actor from, String text) {
        super(from, text);
    }
}
