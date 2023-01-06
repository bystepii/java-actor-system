package actors;

import messages.Message;
import messages.QuitMessage;
import messages.RingMessage;
import messages.TimeExceededMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A RingActor is an Actor that sends a message to the next Actor in a ring.
 */
public class RingActor extends AbstractActor {

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(RingActor.class);

    /**
     * The next Actor in the ring.
     */
    private ActorRef next;

    /**
     * Parent actor to notify when the ring is complete.
     */
    private final ActorRef parent;

    /**
     * Create a new RingActor.
     *
     * @param next the next Actor in the ring.
     * @param parent the parent actor to notify when the ring is complete.
     */
    public RingActor(ActorRef next, ActorRef parent) {
        this.next = next;
        this.parent = parent;
    }

    /**
     * Sets the next Actor in the ring.
     * @param next the next Actor in the ring.
     */
    public void setNext(ActorRef next) {
        this.next = next;
    }

    @Override
    public void process(Message<?> msg) {
        logger.debug(
                "RingActor {} received message {} from {}",
                name,
                msg.getClass().getSimpleName(),
                msg.getSenderName()
        );
        if (msg instanceof RingMessage m) {
            m.setSender(this);
            m.setSenderName(name);
            m.decrementRemainingHops();
            if (m.getRemainingHops() > 0)
                next.send(m);
            else {
                logger.info("RingActor {} finished", name);
                parent.send(new TimeExceededMessage(this, name));
            }
        } else if (msg instanceof QuitMessage m) {
            m.setSender(this);
            m.setSenderName(name);
            next.send(msg);
        }
    }
}
