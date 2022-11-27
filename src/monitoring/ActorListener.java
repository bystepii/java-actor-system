package monitoring;

import actors.ActorRef;

public interface ActorListener {
    void onEvent(ActorEvent event);
}
