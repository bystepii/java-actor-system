package actors;

import messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * A RingAppActor is an Actor that simplifies the creation of a ring of Actors.
 */
public class RingAppActor extends AbstractActor {

    /**
     * Reference to the first Actor in the ring.
     */
    private ActorRef first;

    /**
     * State of the ring.
     */
    private enum State {
        /**
         * The ring is not yet created.
         */
        NOT_CREATED,
        /**
         * The ring is created.
         */
        CREATED
    }

    /**
     * The current state of the ring.
     */
    private State state = State.NOT_CREATED;

    /**
     * Reference to the parent actor to notify when the ring is finished.
     */
    private ActorRef parent;

    /**
     * Logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(RingAppActor.class);

    /**
     * Default constructor
     */
    public RingAppActor() {

    }

    @Override
    protected void process(Message<?> msg) {
        logger.debug(
                "RingAppActor {} received message {} from {}",
                name,
                msg.getClass().getSimpleName(),
                msg.getSenderName()
        );

        if (msg instanceof SpawnActorRingMessage m) {
            if (state == State.CREATED) {
                if (msg.getSender() != null)
                    msg.getSender().send(new Message<>(this, name, "Ring already created"));
                return;
            }

            int numActors = m.getNumActors();
            if (numActors < 1 && m.getSender() != null) {
                m.getSender().send(new Message<>(this, name, "Invalid number of actors"));
                return;
            }

            logger.info("RingAppActor {} creating ring of {} actors", name, numActors);

            RingActor[] actors = new RingActor[numActors];
            actors[actors.length - 1] = new RingActor(null, this);
            ActorContext.spawnActor(name + "RingActor" + (actors.length - 1), actors[actors.length - 1]);
            for (int i = actors.length - 2; i >= 0; i--) {
                actors[i] = new RingActor(actors[i + 1], this);
                ActorContext.spawnActor(name + "RingActor" + i, actors[i]);
            }
            actors[actors.length - 1].setNext(actors[0]);
            first = actors[0];

            if (m.getSender() != null) {
                List<String> actorNames = Arrays.stream(actors).map(Actor::getName).toList();
                m.getSender().send(new Message<>(this, name, actorNames));
            }

            state = State.CREATED;
            parent = m.getSender();
        } else if (msg instanceof RingMessage || msg instanceof QuitMessage) {
            if (first == null) {
                if (msg.getSender() != null)
                    msg.getSender().send(new Message<>(this, name, "No actors have been spawned"));
                return;
            }
            msg.setSender(this);
            msg.setSenderName(name);
            first.send(msg);
            if (msg instanceof QuitMessage) {
                first = null;
                state = State.NOT_CREATED;
                logger.info("RingAppActor {} finished", name);
            }
            logger.info("RingAppActor {} sent message to first actor", name);
        } else if (msg instanceof TimeExceededMessage) {
            msg.setSender(this);
            msg.setSenderName(name);
            if (parent != null)
                parent.send(msg);
        }
    }
}
