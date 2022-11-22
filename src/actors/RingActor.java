package actors;

import messages.Message;
import messages.RingMessage;

public class RingActor extends AbstractActor {
    private ActorRef next;

    public RingActor(ActorRef next) {
        this.next = next;
    }

    public ActorRef getNext() {
        return next;
    }

    public void setNext(ActorRef next) {
        this.next = next;
    }

    @Override
    public void process(Message<?> msg) {
        if (msg instanceof RingMessage m) {
            System.out.println("RingActor " + name + " received " + m.getBody() + " from " + (msg.getSender() == null ? "null" : msg.getSenderName()));
            m.setSender(this);
            m.setSenderName(name);
            next.send(msg);
        } else
            System.out.println(msg.getBody());
    }
}
