package decorators;

import actors.Actor;
import actors.ActorContext;
import messages.Message;

public class FirewallDecorator extends AbstractDecorator {

    public FirewallDecorator(Actor actor) {
        super(actor);
    }

    @Override
    public void send(Message<?> msg) {
        if (msg.getSender() != null && ActorContext.lookup(msg.getSenderName()) == msg.getSender())
            actor.send(msg);
    }
}
