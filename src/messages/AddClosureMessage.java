package messages;

import java.util.function.Predicate;

public class AddClosureMessage extends Message {
    private final Predicate<Message> closure;

    public AddClosureMessage(Predicate<Message> closure) {
        this.closure = closure;
    }

    public Predicate<Message> getClosure() {
        return closure;
    }
}
