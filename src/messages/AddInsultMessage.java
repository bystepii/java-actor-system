package messages;

import actors.ActorRef;

public class AddInsultMessage extends Message<String> {
    public AddInsultMessage(ActorRef sender, String senderName, String text) {
        super(sender, senderName, text);
    }

    public AddInsultMessage(String text) {
        super(text);
    }
}
