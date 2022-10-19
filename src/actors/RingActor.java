package actors;

import messages.Message;
import messages.RingMessage;

public class RingActor extends AbstractActor {
    private Actor next;

    public RingActor(Actor next) {
        this.next = next;
    }

    public Actor getNext() {
        return next;
    }

    public void setNext(Actor next) {
        this.next = next;
    }

    @Override
    public void process(Message msg) {
        if (msg instanceof RingMessage m) {
            System.out.println("RingActor " + name + " received " + m.getText() + " from " + (msg.getFrom() == null ? "null" : msg.getFrom().getName()));
            m.setFrom(this);
            next.send(msg);
        } else
            System.out.println(msg.getText());
    }
}
