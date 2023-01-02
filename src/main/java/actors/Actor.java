package actors;

import messages.Message;

/**
 * An Actor is an object that can receive messages and react to them.
 * This is the interface that all Actors must implement.
 */
public interface Actor extends ActorRef, ProcessModifiable<Message<?>> {
    void start();

    /**
     * Returns the name of the Actor.
     *
     * @return the name of the Actor.
     */
    String getName();

    /**
     * Set the name of the Actor.
     *
     * @param name the name of the Actor.
     */
    void setName(String name);
}
