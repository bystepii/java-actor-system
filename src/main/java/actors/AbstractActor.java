package actors;

import messages.DelayedMessage;
import messages.Message;
import messages.QuitMessage;
import messages.SleepMessage;
import monitoring.ActorEvent;
import monitoring.MessageEvent;
import monitoring.MonitorService;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

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

    /**
     * Indicates whether the Actor has stopped.
     */
    private boolean hasStopped = false;

    /**
     * Default constructor.
     */
    public AbstractActor() {

    }

    @Override
    public final void send(Message<?> msg) {
        if (msg == null)
            throw new IllegalArgumentException("Message cannot be null");

        // create send event for the actor that sent the message
        if (msg.getSenderName() != null)
            MonitorService.getInstance().notifyListeners(
                    new MessageEvent<>(msg.getSenderName(), ActorEvent.EventType.MESSAGE_SENT, msg)
            );

        // add the message to the queue
        messageQueue.add(msg);

        // create receive event for the actor that received the message
        MonitorService.getInstance().notifyListeners(
                new MessageEvent<>(name, ActorEvent.EventType.MESSAGE_RECEIVED, msg)
        );
    }

    @Override
    public final void start() {
        if (hasStarted)
            throw new IllegalStateException("Actor has already started!");
        hasStarted = true;

        // create the create event for the actor
        MonitorService.getInstance().notifyListeners(new ActorEvent(getName(), ActorEvent.EventType.CREATED));

        while (!hasStopped) {
            try {
                Message<?> m = messageQueue.poll(1000, TimeUnit.SECONDS);
                if (m instanceof QuitMessage)
                    hasStopped = true;
                else if (m instanceof DelayedMessage d)
                    Thread.sleep(d.getMillis());

                // Also forward the QuitMessage
                for (var modifier : modifiers)
                    m = modifier.modify(m);
                process(m);

                // create the process event for the actor
                if (m != null)
                    MonitorService.getInstance().notifyListeners(
                            new ActorEvent(name, ActorEvent.EventType.MESSAGE_PROCESSED)
                    );
            } catch (InterruptedException ignored) {
            }
        }

        MonitorService.getInstance().notifyListeners(
                new ActorEvent(getName(), ActorEvent.EventType.STOPPED)
        );
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
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be null or blank!");
        this.name = name;
    }

    @Override
    public void addModifier(Modifier<Message<?>> modifier) {
        if (modifier == null)
            throw new IllegalArgumentException("Modifier cannot be null!");
        modifiers.add(modifier);
    }

    @Override
    public void removeModifier(Modifier<Message<?>> modifier) {
        if (modifier == null)
            throw new IllegalArgumentException("Modifier cannot be null!");
        modifiers.remove(modifier);
    }
}
