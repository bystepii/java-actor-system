package messages;

import actors.ActorRef;

/**
 * This class represents a quit message that can be sent to an Actor to terminate it.
 */
public class QuitMessage extends Message<String> {

    /**
     * Default constructor.
     *
     * @param sender     the Actor that sent the message.
     * @param senderName the name of the Actor that sent the message.
     * @param body       the body of the message.
     */
    public QuitMessage(ActorRef sender, String senderName, String body) {
        super(sender, senderName, body);
    }

    /**
     * Creates a new QuitMessage with empty sender and empty senderName.
     *
     * @param body the body of the message.
     */
    public QuitMessage(String body) {
        super(body);
    }

    /**
     * Creates a new QuitMessage with empty body.
     */
    public QuitMessage() {
        super();
    }
}
