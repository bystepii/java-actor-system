package messages;

import actors.ActorRef;

/**
 * A message that is sent back to the sender of a {@link PingMessage}.
 *
 * @see PingMessage
 * @see actors.PingActor
 */
public class PongMessage extends Message<String> {

    /**
     * Creates a new pong message.
     *
     * @param sender     the sender of the message
     * @param senderName the name of the sender
     * @param body       the body of the message
     */
    public PongMessage(ActorRef sender, String senderName, String body) {
        super(sender, senderName, body);
    }

    /**
     * Creates a new pong message.
     *
     * @param sender     the sender of the message
     * @param senderName the name of the sender
     */
    public PongMessage(ActorRef sender, String senderName) {
        super(sender, senderName);
    }

    /**
     * Creates a new pong message.
     *
     * @param body the body of the message
     */
    public PongMessage(String body) {
        super(body);
    }

    /**
     * Creates an empty pong message.
     */
    public PongMessage() {
        super();
    }
}
