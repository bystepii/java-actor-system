package messages;

import actors.ActorRef;

/**
 * A message that is sent when a message has exceeded its hop limit.
 *
 * @see messages.HopLimitedMessage
 * @see messages.RingMessage
 * @see actors.RingActor
 * @see actors.RingAppActor
 */
public class TimeExceededMessage extends Message<String> {
    /**
     * Creates a new time exceeded message.
     *
     * @param sender     the sender of the message
     * @param senderName the name of the sender
     * @param body       the body of the message
     */
    public TimeExceededMessage(ActorRef sender, String senderName, String body) {
        super(sender, senderName, body);
    }

    /**
     * Creates a new time exceeded message.
     *
     * @param sender     the sender of the message
     * @param senderName the name of the sender
     */
    public TimeExceededMessage(ActorRef sender, String senderName) {
        super(sender, senderName);
    }

    /**
     * Creates a new time exceeded message.
     *
     * @param body the body of the message
     */
    public TimeExceededMessage(String body) {
        super(body);
    }

    /**
     * Creates an empty time exceeded message.
     */
    public TimeExceededMessage() {
        super();
    }
}
