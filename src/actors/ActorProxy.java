package actors;

import messages.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ActorProxy implements ActorRef {

    private final ActorRef actor;
    private final Actor self = new AbstractActor() {
        @Override
        public void process(Message msg) {
            receivedMessages.add(msg);
        }
    };
    private static final BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<>();

    public ActorProxy(ActorRef actor) {
        this.actor = actor;
    }

    public void send(Message msg) {
        msg.setFrom(self);
        actor.send(msg);
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
