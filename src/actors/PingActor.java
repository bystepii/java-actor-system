package actors;

import messages.Message;
import messages.PingMessage;

public class PingActor extends AbstractActor {
    @Override
    public void process(Message<?> msg) {
        if (msg instanceof PingMessage m) {
            System.out.println("PingActor " + name + " received " + m.getBody() + " from " + (msg.getSender() == null ? "null" : msg.getSenderName()));
            ActorRef from = m.getSender();
            m.setSender(this);
            m.setBody("Pong");
            from.send(msg);
        } else
            System.out.println(msg.getBody());
    }
}
