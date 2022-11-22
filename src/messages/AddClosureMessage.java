package messages;

import java.util.function.Predicate;

public class AddClosureMessage extends Message<Predicate<Message<?>>> {

    public AddClosureMessage(Predicate<Message<?>> closure) {
        super(closure);
    }

    public Predicate<Message<?>> getClosure() {
        return getBody();
    }
}
