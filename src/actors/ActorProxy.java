package actors;

import messages.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ActorProxy implements ActorRef {

    private final ActorRef actor;
    private final BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<>();

    private final ActorRef self = new ActorRef() {
        @Override
        public void send(Message msg) {
            receivedMessages.add(msg);
        }

        @Override
        public String getName() {
            return ActorProxy.this.getName();
        }
    };

    public ActorProxy(ActorRef actor) {
        this.actor = actor;
    }

    public void send(Message msg) {
        msg.setFrom(self);
        actor.send(msg);
    }

    @Override
    public String getName() {
        return actor.getName() + " (proxy)";
    }

    public Message receive() {
        while (true) {
            try {
                return receivedMessages.take();
            } catch (InterruptedException ignored) {
            }
        }
    }
}
