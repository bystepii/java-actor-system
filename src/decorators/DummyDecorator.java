package decorators;

import actors.Actor;
import messages.Message;

public class DummyDecorator extends AbstractDecorator {

    private final String name;

    public DummyDecorator(Actor actor, String name) {
        super(actor);
        this.name = name;

        addModifier(msg -> new Message<>(msg.getBody() + " (process decorated by " + name + ")"));
    }

    @Override
    public void send(Message<?> msg) {
        actor.send(new Message<>(this, name, msg.getBody() + " (send decorated by " + name + ")"));
    }
}
