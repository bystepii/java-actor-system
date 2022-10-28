package Decorators;

import actors.AbstractActor;
import actors.Actor;
import messages.Message;
import messages.QuitMessage;

public abstract class AbstractDecorator extends AbstractActor {
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
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message m = messageQueue.take();
                if (m instanceof QuitMessage)
                    Thread.currentThread().interrupt();
                else
                    process(m);
            } catch (InterruptedException ignored) {
            }
        }
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
