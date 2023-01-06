package monitoring;

/**
 * This functional interface represents a listener that handles ActorEvents.
 */
public interface ActorListener {

    /**
     * Handles an ActorEvent.
     *
     * @param event the event to handle.
     */
    void onEvent(ActorEvent event);
}
