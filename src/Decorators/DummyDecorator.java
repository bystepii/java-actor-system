package Decorators;

import actors.Actor;
import messages.Message;

public class DummyDecorator extends AbstractDecorator {

    private final String name;

    public DummyDecorator(Actor actor, String name) {
        super(actor);
        this.name = name;
    }

    @Override
    public void send(Message msg) {
        msg.setText(msg.getText() + " (send decorated by " + name + ")");
        actor.send(msg);
    }

    @Override
    public void process(Message msg) {
        msg.setText(msg.getText() + " (process decorated by " + name + ")");
        actor.process(msg);
    }
}