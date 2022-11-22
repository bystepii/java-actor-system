package actors;

import messages.Message;

/**
 * An ActorRef is a reference to an Actor. It is used to send messages to the
 * Actor.
 */
public interface ActorRef {

    /**
     * Send a message to the Actor.
     *
     * @param msg the message to send.
     */
    void send(Message<?> msg);
}
