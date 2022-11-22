package actors;

import messages.Message;
import messages.QuitMessage;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Abstract base class for all Actors.
 */
public abstract class AbstractActor implements Actor {

    /**
     * The queue of messages received by the Actor.
     */
    protected final BlockingQueue<Message<?>> messageQueue = new LinkedBlockingQueue<>();
    /**
     * The list of modifiers to apply to the messages received by the Actor.
     */
    private final List<Modifier<Message<?>>> modifiers = new LinkedList<>();
    /**
     * The name of the Actor.
     */
    protected String name;
    /**
     * Indicates whether the Actor has started.
     */
    private boolean hasStarted = false;

    @Override
    public void send(Message<?> msg) {
        messageQueue.add(msg);
    }

    @Override
    public void start() {
        if (hasStarted)
            throw new IllegalStateException("Actor has already started!");
        hasStarted = true;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message<?> m = messageQueue.take();
                if (m instanceof QuitMessage)
                    Thread.currentThread().interrupt();
                else {
                    for (var modifier : modifiers)
                        m = modifier.modify(m);
                    process(m);
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * Processes the given message.
     *
     * @param msg the message to process
     */
    protected abstract void process(Message<?> msg);

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void addModifier(Modifier<Message<?>> modifier) {
        modifiers.add(modifier);
    }

    @Override
    public void removeModifier(Modifier<Message<?>> modifier) {
        modifiers.remove(modifier);
    }
}
