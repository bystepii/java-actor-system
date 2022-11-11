package Decorators;

import actors.Actor;
import messages.AddClosureMessage;
import messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class LambdaFirewallDecorator extends AbstractDecorator {
    private final List<Predicate<Message>> filters = new ArrayList<>();

    public LambdaFirewallDecorator(Actor actor) {
        super(actor);
    }

    @Override
    public void send(Message msg) {
        if (msg instanceof AddClosureMessage)
            filters.add(((AddClosureMessage) msg).getClosure());
        else if (filters.stream().allMatch(f -> f.test(msg)))
            actor.send(msg);
    }

    @Override
    public void process(Message msg) {
        actor.process(msg);
    }
}
