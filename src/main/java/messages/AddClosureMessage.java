package messages;

import java.util.function.Predicate;

/**
 * This class is a message that adds a closure to a firewall.
 *
 * @see decorators.LambdaFirewallDecorator
 */
public class AddClosureMessage extends Message<Predicate<Message<?>>> {

    /**
     * Creates a new closure message with the given closure (predicate).
     *
     * @param closure the closure to add
     */
    public AddClosureMessage(Predicate<Message<?>> closure) {
        super(closure);
    }

    /**
     * Gets the closure.
     *
     * @return the closure
     */
    public Predicate<Message<?>> getClosure() {
        return getBody();
    }
}
