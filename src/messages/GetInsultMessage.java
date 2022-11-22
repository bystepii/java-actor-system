package messages;

import actors.ActorRef;

public class GetInsultMessage extends Message<String> {
    public GetInsultMessage(ActorRef sender, String senderName) {
        super(sender, senderName);
    }

    public GetInsultMessage() {
        super();
    }
}
