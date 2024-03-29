package messages;

import actors.ActorRef;

/**
 * A message that is sent to {@link actors.PingActor}.
 *
 * @see actors.PingActor
 * @see PongMessage
 */
public class PingMessage extends Message<String> {
    /**
     * Creates a new ping message.
     *
     * @param sender     the sender of the message
     * @param senderName the name of the sender
     * @param body       the body of the message
     */
    public PingMessage(ActorRef sender, String senderName, String body) {
        super(sender, senderName, body);
    }

    /**
     * Creates a new ping message.
     *
     * @param sender     the sender of the message
     * @param senderName the name of the sender
     */
    public PingMessage(ActorRef sender, String senderName) {
        super(sender, senderName);
    }

    /**
     * Creates a new ping message.
     *
     * @param body the body of the message
     */
    public PingMessage(String body) {
        super(body);
    }

    /**
     * Creates an empty ping message.
     */
    public PingMessage() {
        super();
    }
}
