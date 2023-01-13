package messages;

/**
 * A message that blocks the sender for a given amount of time.
 */
public interface DelayedMessage {
    /**
     * Get the number of milliseconds to delay.
     *
     * @return the number of milliseconds to delay.
     */
    int getMillis();
}
