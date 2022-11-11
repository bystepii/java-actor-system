package actors;

import messages.Message;

public interface Actor extends ActorRef {
    void start();

    void end();

    void pause();

    void process(Message msg);

    String getName();

    void setName(String name);
}
