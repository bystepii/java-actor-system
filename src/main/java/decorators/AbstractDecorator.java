package decorators;

import actors.Actor;
import actors.Modifier;
import messages.Message;
import monitoring.ActorEvent;
import monitoring.ActorListener;

/**
 * Abstract base class for Actor decorators.
 */
public abstract class AbstractDecorator implements Actor {

    /**
     * The Actor to decorate.
     */
    protected final Actor actor;

    /**
     * Creates a new AbstractDecorator for the given Actor.
     *
     * @param actor the Actor to decorate.
     */
    public AbstractDecorator(Actor actor) {
        this.actor = actor;
    }

    @Override
    public void start() {
        actor.start();
    }

    @Override
    public abstract void send(Message<?> msg);

    @Override
    public String getName() {
        return actor.getName();
    }

    @Override
    public void setName(String name) {
        actor.setName(name);
    }

    @Override
    public void addModifier(Modifier<Message<?>> modifier) {
        actor.addModifier(modifier);
    }

    @Override
    public void removeModifier(Modifier<Message<?>> modifier) {
        actor.removeModifier(modifier);
    }

    @Override
    public void attach(ActorListener listener) {
        actor.attach(listener);
    }

    @Override
    public void detach(ActorListener listener) {
        actor.detach(listener);
    }

    @Override
    public void notifyListeners(ActorEvent event) {
        actor.notifyListeners(event);
    }
}
