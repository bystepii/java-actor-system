package actors;

import messages.Message;

/**
 * Hello world actor. Simply prints the message it receives.
 */
public class HelloWorldActor extends AbstractActor {

    /**
     * Default constructor.
     */
    public HelloWorldActor() {

    }

    @Override
    public void process(Message<?> msg) {
        System.out.println("HelloWorldActor " + name + " received " + msg.getBody() + " from " + (msg.getSender() == null ? "null" : msg.getSenderName()));
    }
}
