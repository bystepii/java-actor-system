package decorators;

import actors.Actor;
import actors.ActorContext;
import messages.Message;

/**
 * This class is a decorator that adds a firewall to an actor.
 * <p>
 * The firewall only allows messages sent from a valid actor registered in the {@link ActorContext}.
 * It will silently drop messages coming from a proxy.
 */
public class FirewallDecorator extends AbstractDecorator {

    /**
     * Creates a new firewall decorator.
     *
     * @param actor the actor to decorate
     */
    public FirewallDecorator(Actor actor) {
        super(actor);
    }

    @Override
    public void send(Message<?> msg) {
        if (msg.getSender() != null && ActorContext.lookupActorRef(msg.getSenderName()) == msg.getSender())
            actor.send(msg);
    }
}
