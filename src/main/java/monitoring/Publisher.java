package monitoring;

public interface Publisher {
    void attach(ActorListener listener);

    void detach(ActorListener listener);

    void notifyListeners(ActorEvent event);
}
