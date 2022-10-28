package actors;

import messages.Message;

public interface Actor extends ActorRef {
    void actorLoop();
    void process(Message msg);
    String getName();
    void setName(String name);
}
