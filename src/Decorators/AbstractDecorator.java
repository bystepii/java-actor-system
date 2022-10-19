package Decorators;

import actors.Actor;
import messages.Message;

public abstract class AbstractDecorator implements Actor {
    protected final Actor actor;

    public AbstractDecorator(Actor actor) {
        this.actor = actor;
    }

    @Override
    public abstract void send(Message msg);

    @Override
    public Message receive() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void actorLoop() {
        actor.actorLoop();
    }

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
