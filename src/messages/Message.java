package messages;

import actors.Actor;
import actors.ActorRef;

public class Message {
    private Actor from;
    private String text;

    public Message(Actor from, String text) {
        this.from = from;
        this.text = text;
    }

    public Message(Actor from) {
        this(from, "");
    }

    public Message(String text) {
        this(null, text);
    }

    public Message() {
        this(null, "");
    }

    public Actor getFrom() {
        return from;
    }

    public void setFrom(Actor from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
