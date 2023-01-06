package messages;

import actors.ActorRef;

/**
 * A message that is sent between {@link actors.RingActor}s.
 *
 * @see actors.RingActor
 */
public class RingMessage extends Message<String> implements HopLimitedMessage {

    /**
     * The number of remaining hops.
     */
    private int remainingHops;

    /**
     * Creates a new ring message.
     *
     * @param sender     the sender of the message
     * @param senderName the name of the sender
     * @param body       the body of the message
     * @param maxHops    the maximum number of hops the message can make
     */
    public RingMessage(ActorRef sender, String senderName, String body, int maxHops) {
        super(sender, senderName, body);
        this.remainingHops = maxHops;
    }

    /**
     * Creates a new ring message with empty sender and empty senderName.
     * @param body the body of the message
     * @param maxHops the maximum number of hops the message can make
     */
    public RingMessage(String body, int maxHops) {
        super(body);
        this.remainingHops = maxHops;
    }

    /**
     * Creates an empty ring message.
     *
     * @param maxHops the maximum number of hops the message can make
     */
    public RingMessage(int maxHops) {
        super();
        this.remainingHops = maxHops;
    }

    @Override
    public void decrementRemainingHops() {
        this.remainingHops--;
    }

    @Override
    public int getRemainingHops() {
        return this.remainingHops;
    }
}
