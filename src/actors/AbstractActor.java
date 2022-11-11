package actors;

import messages.Message;
import messages.QuitMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class AbstractActor implements Actor {
    protected final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    protected String name;
    private boolean hasStarted = false;

    @Override
    public void send(Message msg) {
        messageQueue.add(msg);
    }

    @Override
    public void start() {
        if (hasStarted)
            throw new IllegalStateException("Actor has already started!");
        hasStarted = true;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Message m = messageQueue.take();
                if (m instanceof QuitMessage)
                    Thread.currentThread().interrupt();
                else {
                    process(m);
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public void end() {
        send(new QuitMessage());
    }

    @Override
    public void pause() {

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
