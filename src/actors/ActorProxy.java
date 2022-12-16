package actors;

import messages.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Represents a proxy for an Actor, which can be used to send and receive
 * messages from the Actor in an Object-Oriented way.
 */
public class ActorProxy implements ActorRef {

    /**
     * The reference to the Actor.
     */
    private final ActorRef targetActor;

    /**
     * The queue of messages received by the Actor.
     */
    private final BlockingQueue<Message<?>> receivedMessages = new LinkedBlockingQueue<>();

    /**
     * Creates a new ActorProxy for the given Actor.
     *
     * @param targetActor the Actor to create a proxy for.
     */
    public ActorProxy(ActorRef targetActor) {
        this.targetActor = targetActor;
    }

    /**
     * Sends a message to the Actor. If the message has not null from field, it
     * will be left unchanged and the ActorProxy will not be used as the sender
     * nor be able to receive the response. Otherwise, the ActorProxy will be
     * used as the sender and will be able to receive the response.
     *
     * @param msg the message to send.
     */
    public void send(Message<?> msg) {
        if (msg.getSender() == null)
            msg.setSender(receivedMessages::add);
        msg.setSenderName((targetActor instanceof Actor a ? a.getName() : "unknown") + " (ActorProxy)");
        targetActor.send(msg);
    }

    /**
     * Returns the next message received by the Actor.
     *
     * @return the next message received by the Actor.
     */
    public Message<?> receive() {
        while (true) {
            try {
                return receivedMessages.take();
            } catch (InterruptedException ignored) {
            }
        }
    }
}
