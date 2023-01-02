package actors;

import messages.Message;
import messages.QuitMessage;
import monitoring.ActorEvent;
import monitoring.ActorListener;
import monitoring.MessageEvent;
import monitoring.MonitorService;

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
     * The list of listeners to notify when an event occurs.
     */
    private final List<ActorListener> listeners = new LinkedList<>();
    /**
     * The name of the Actor.
     */
    protected String name;
    /**
     * Indicates whether the Actor has started.
     */
    private boolean hasStarted = false;

    @Override
    public final void send(Message<?> msg) {
        MonitorService.getInstance().notifyListeners(
                new MessageEvent<>(msg.getSenderName(), ActorEvent.EventType.MESSAGE_SENT, msg)
        );
        messageQueue.add(msg);
    }

    @Override
    public final void start() {
        if (hasStarted)
            throw new IllegalStateException("Actor has already started!");
        hasStarted = true;
        MonitorService.getInstance().notifyListeners(new ActorEvent(getName(), ActorEvent.EventType.CREATED));

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message<?> m = messageQueue.take();
                MonitorService.getInstance().notifyListeners(
                        new MessageEvent<>(getName(), ActorEvent.EventType.MESSAGE_RECEIVED, m)
                );
                if (m instanceof QuitMessage) {
                    Thread.currentThread().interrupt();
                    MonitorService.getInstance().notifyListeners(
                            new ActorEvent(getName(), ActorEvent.EventType.STOPPED)
                    );
                } else {
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