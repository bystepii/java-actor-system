package messages;

import actors.ActorRef;

public class RingMessage extends Message<String> {
    public RingMessage(ActorRef sender, String senderName, String body) {
        super(sender, senderName, body);
    }
}
