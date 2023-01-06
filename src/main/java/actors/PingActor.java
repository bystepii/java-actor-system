package actors;

import messages.Message;
import messages.PingMessage;
import messages.PongMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple actor that responds to {@link messages.PingMessage} with a {@link messages.PongMessage}.
 */
public class PingActor extends AbstractActor {

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(PingActor.class);

    /**
     * Default constructor.
     */
    public PingActor() {

    }

    @Override
    public void process(Message<?> msg) {
        if (msg instanceof PingMessage m) {
            logger.info("PingActor {} received {} from {}", name, m.getBody(), msg.getSenderName());
            ActorRef from = m.getSender();
            from.send(new PongMessage(this, name, "Pong"));
        } else if (msg instanceof PongMessage m) {
            logger.info("PingActor {} received {} from {}", name, m.getBody(), msg.getSenderName());
            ActorRef from = m.getSender();
            from.send(new PingMessage(this, name, "Ping"));
        }
    }
}
