package actors;

import messages.Message;

public interface Actor {
    void send(Message msg);

    Message receive();

    void actorLoop();

    void process(Message msg);
    String getName();
    void setName(String name);
}
