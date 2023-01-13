package messages;

import actors.ActorRef;

/**
 * Delayed variant of {@link RingMessage}.
 */
public class DelayedRingMessage extends RingMessage implements DelayedMessage {

    /**
     * The number of milliseconds to delay.
     */
    private final int millis;

    /**
     * Creates a new delayed ring message.
     * @param sender the sender of the message
     * @param senderName the name of the sender
     * @param body the body of the message
     * @param maxHops the maximum number of hops the message can make
     * @param millis the number of milliseconds to delay
     */
    public DelayedRingMessage(ActorRef sender, String senderName, String body, int maxHops, int millis) {
        super(sender, senderName, body, maxHops);
        this.millis = millis;
    }

    /**
     * Creates a new delayed ring message.
     * @param body the body of the message
     * @param maxHops the maximum number of hops the message can make
     * @param millis the number of milliseconds to delay
     */
    public DelayedRingMessage(String body, int maxHops, int millis) {
        super(body, maxHops);
        this.millis = millis;
    }

    /**
     * Creates a new delayed ring message.
     * @param maxHops the maximum number of hops the message can make
     * @param millis the number of milliseconds to delay
     */
    public DelayedRingMessage(int maxHops, int millis) {
        super(maxHops);
        this.millis = millis;
    }

    @Override
    public int getMillis() {
        return this.millis;
    }
}
