package decorators;

import actors.Actor;
import messages.AddClosureMessage;
import messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is a decorator that adds a firewall to an actor.
 * <p>
 * This firewall accepts closures to filter messages.
 *
 * @see AddClosureMessage
 */
public class LambdaFirewallDecorator extends AbstractDecorator {

    /**
     * The list of closures that filter messages.
     */
    private final List<Predicate<Message<?>>> filters = new ArrayList<>();

    /**
     * Creates a new firewall decorator.
     *
     * @param actor the actor to decorate
     */
    public LambdaFirewallDecorator(Actor actor) {
        super(actor);
    }

    @Override
    public void send(Message<?> msg) {
        if (msg instanceof AddClosureMessage)
            filters.add(((AddClosureMessage) msg).getClosure());
        else if (filters.stream().allMatch(f -> f.test(msg)))
            actor.send(msg);
    }
}
