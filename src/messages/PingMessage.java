package messages;

import actors.Actor;

public class PingMessage extends Message {
    public PingMessage(Actor from, String text) {
        super(from, text);
    }

    public PingMessage(String text) {
        super(text);
    }

    public PingMessage(Actor from) {
        super(from);
    }

    public PingMessage() {
        super();
    }
}
