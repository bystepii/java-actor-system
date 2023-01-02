package messages;

import actors.ActorRef;

public class Message<T> {
    private transient ActorRef sender;
    private String senderName;
    private T body;

    public Message(ActorRef sender, String senderName, T body) {
        this.sender = sender;
        this.senderName = senderName;
        this.body = body;
    }

    public Message(ActorRef sender, String senderName) {
        this(sender, senderName, null);
    }

    public Message(T body) {
        this(null, null, body);
    }

    public Message() {
        this(null, null, null);
    }

    public ActorRef getSender() {
        return sender;
    }

    public void setSender(ActorRef sender) {
        this.sender = sender;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
