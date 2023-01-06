package monitoring;

/**
 * This class represents a publisher that can be used to publish {@link ActorEvent}s.
 *
 * @see ActorEvent
 * @see ActorListener
 * @see MonitorService
 */
public interface Publisher {

    /**
     * Adds a listener to the publisher.
     *
     * @param listener the listener to add.
     */
    void attach(ActorListener listener);

    /**
     * Removes a listener from the publisher.
     *
     * @param listener the listener to remove.
     */
    void detach(ActorListener listener);

    /**
     * Publishes an event.
     *
     * @param event the event to publish.
     */
    void notifyListeners(ActorEvent event);
}
