package actors;

import messages.Message;
import monitoring.ActorEvent;
import monitoring.MessageEvent;
import monitoring.MonitorService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
     * The queue of messages received from the Actor.
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
    @Override
    public void send(Message<?> msg) {
        if (msg.getSender() == null)
            msg.setSender(msg1 -> {
                // Notify the listeners about the message sent by the actor
                if (msg1.getSenderName() != null)
                    MonitorService.getInstance().notifyListeners(
                            new MessageEvent<>(msg1.getSenderName(), ActorEvent.EventType.MESSAGE_SENT, msg1)
                    );
                receivedMessages.add(msg1);
            });
        msg.setSenderName((targetActor instanceof Actor a ? a.getName() : "unknown") + " (ActorProxy)");
        targetActor.send(msg);
    }

    /**
     * Returns the next message received by the Actor.
     *
     * @return the next message received by the Actor.
     * @throws ClassCastException if the message cannot be cast to the given type.
     * @param <T> the type of the message.
     */
    public <T> Message<T> receive() throws ClassCastException {
        while (true) {
            try {
                @SuppressWarnings("unchecked") // Cast the message to the expected type.
                Message<T> msg = (Message<T>) receivedMessages.take();
                return msg;
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * Returns the next message received by the Actor.
     *
     * @param timeout the timeout in milliseconds. A timeout of 0 means wait indefinitely.
     * @return the next message received by the Actor.
     * @throws TimeoutException   if the timeout is reached.
     * @throws ClassCastException if the message cannot be cast to the given type.
     * @param <T> the type of the message.
     */
    public <T> Message<T> receive(long timeout) throws TimeoutException, ClassCastException {
        if (timeout < 0)
            throw new IllegalArgumentException("Timeout cannot be negative.");

        if (timeout == 0)
            return receive();

        while (true) {
            try {
                @SuppressWarnings("unchecked") // Cast the message to the expected type.
                Message<T> msg = (Message<T>) receivedMessages.poll(timeout, TimeUnit.MILLISECONDS);
                if (msg == null)
                    throw new TimeoutException("Timeout reached while waiting for a message.");
                return msg;
            } catch (InterruptedException ignored) {
            }
        }
    }
}
