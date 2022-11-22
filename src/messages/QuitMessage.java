package messages;

import actors.ActorRef;

public class QuitMessage extends Message<String> {
    public QuitMessage(ActorRef sender, String senderName, String body) {
        super(sender, senderName, body);
    }

    public QuitMessage(String body) {
        super(body);
    }

    public QuitMessage() {
        super();
    }
}
