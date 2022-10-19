package actors;

import messages.Message;
import messages.QuitMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class AbstractActor implements Actor {
    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    protected String name;

    @Override
    public void send(Message msg) {
        messageQueue.add(msg);
    }

    @Override
    public Message receive() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void actorLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message m = messageQueue.take();
                if (m instanceof QuitMessage)
                    Thread.currentThread().interrupt();
                else
                    process(m);
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public abstract void process(Message msg);

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
