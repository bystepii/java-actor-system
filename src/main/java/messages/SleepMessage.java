package messages;

import actors.ActorRef;

/**
 * A message that blocks the sender for a given amount of time.
 */
public class SleepMessage extends Message<Integer> implements DelayedMessage {
    /**
     * Default constructor.
     *
     * @param sender the sender of the message.
     * @param senderName the name of the sender.
     * @param millis the number of milliseconds to sleep.
     */
    public SleepMessage(ActorRef sender, String senderName, int millis) {
        super(sender, senderName, millis);
    }

    /**
     * Empty constructor.
     *
     * @param millis the number of milliseconds to sleep.
     */
    public SleepMessage(int millis) {
        super(millis);
    }

    /**
     * Get the number of milliseconds to sleep.
     *
     * @return the number of milliseconds to sleep.
     */
    public int getMillis() {
        return getBody();
    }
}
