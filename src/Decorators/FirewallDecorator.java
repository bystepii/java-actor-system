package Decorators;

import actors.Actor;
import actors.ActorContext;
import messages.Message;

public class FirewallDecorator extends AbstractDecorator {

    public FirewallDecorator(Actor actor) {
        super(actor);
    }

    @Override
    public void send(Message msg) {
        if (msg.getFrom() != null && ActorContext.lookup(msg.getFrom().getName()) == msg.getFrom())
            actor.send(msg);
    }

    @Override
    public void process(Message msg) {
        actor.process(msg);
    }
}
