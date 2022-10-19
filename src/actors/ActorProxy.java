package actors;

import messages.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ActorProxy implements Actor {

    private final Actor actor;
    private static final BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<>();

    public ActorProxy(Actor actor) {
        this.actor = actor;
    }

    @Override
    public void send(Message msg) {
        if (msg.getFrom() == actor)
            receivedMessages.add(msg);
        else {
            msg.setFrom(this);
            actor.send(msg);
        }
    }

    @Override
    public Message receive() {
        while (true) {
            try {
                return receivedMessages.take();
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public void actorLoop() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void process(Message msg) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getName() {
        return actor.getName() + "Proxy";
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
