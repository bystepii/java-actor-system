package actors;

import messages.Message;

public class HelloWorldActor extends AbstractActor {

    @Override
    public void process(Message msg) {
        System.out.println("HelloWorldActor " + name + " received " + msg.getText() + " from " + (msg.getFrom() == null ? "null" : msg.getFrom().getName()));
    }
}