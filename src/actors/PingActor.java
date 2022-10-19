package actors;

import messages.Message;
import messages.PingMessage;

public class PingActor extends AbstractActor {
    @Override
    public void process(Message msg) {
        if (msg instanceof PingMessage m) {
            System.out.println("PingActor " + name + " received " + m.getText() + " from " + (msg.getFrom() == null ? "null" : msg.getFrom().getName()));
            Actor from = m.getFrom();
            m.setFrom(this);
            m.setText("Pong");
            from.send(msg);
        } else
            System.out.println(msg.getText());
    }
}
