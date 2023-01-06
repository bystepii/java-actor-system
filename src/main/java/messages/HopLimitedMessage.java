package messages;

/**
 * This interface is implemented by messages that have a limited number of hops.
 */
public interface HopLimitedMessage {
    /**
     * Returns the number of remaining hops.
     * @return the number of remaining hops
     */
    int getRemainingHops();

    /**
     * Decrements the number of remaining hops.
     */
    void decrementRemainingHops();
}
