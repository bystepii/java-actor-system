package actors;

import messages.Message;

public interface ActorRef {
    void send(Message msg);

    String getName();
}
