package messages;

import actors.ActorRef;

public class Message {
    private ActorRef from;
    private String text;

    public Message(ActorRef from, String text) {
        this.from = from;
        this.text = text;
    }

    public Message(ActorRef from) {
        this(from, "");
    }

    public Message(String text) {
        this(null, text);
    }

    public Message() {
        this(null, "");
    }

    public ActorRef getFrom() {
        return from;
    }

    public void setFrom(ActorRef from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
