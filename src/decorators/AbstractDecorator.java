package decorators;

import actors.AbstractActor;
import actors.Actor;
import messages.Message;

public abstract class AbstractDecorator extends AbstractActor {
    protected final Actor actor;

    public AbstractDecorator(Actor actor) {
        this.actor = actor;
    }

    @Override
    public void start() {
        actor.start();
    }

    @Override
    public abstract void send(Message msg);

    @Override
    public abstract void process(Message msg);

    @Override
    public String getName() {
        return actor.getName();
    }

    @Override
    public void setName(String name) {
        actor.setName(name);
    }
}
