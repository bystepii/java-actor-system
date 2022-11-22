package decorators;

import actors.Actor;
import actors.Modifier;
import messages.Message;

public abstract class AbstractDecorator implements Actor {
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
    public String getName() {
        return actor.getName();
    }

    @Override
    public void setName(String name) {
        actor.setName(name);
    }

    @Override
    public void end() {
        actor.end();
    }

    @Override
    public void pause() {
        actor.pause();
    }

    @Override
    public void addModifier(Modifier modifier) {
        actor.addModifier(modifier);
    }

    @Override
    public void removeModifier(Modifier modifier) {
        actor.removeModifier(modifier);
    }
}
