package messages;

import actors.ActorRef;

public class PingMessage extends Message<String> {
    public PingMessage(ActorRef sender, String senderName, String body) {
        super(sender, senderName, body);
    }

    public PingMessage(ActorRef sender, String senderName) {
        super(sender, senderName);
    }

    public PingMessage(String body) {
        super(body);
    }

    public PingMessage() {
        super();
    }
}
