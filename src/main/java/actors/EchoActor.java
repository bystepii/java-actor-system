package actors;

import messages.Message;

/**
 * EchoActor is an actor that echoes back the message it receives.
 */
public class EchoActor extends AbstractActor {

    /**
     * Default constructor.
     */
    public EchoActor() {

    }

    @Override
    protected void process(Message<?> msg) {
        ActorRef from = msg.getSender();
        if (from != null) {
            msg.setSender(this);
            msg.setSenderName(name);
            from.send(msg);
        }
    }
}
