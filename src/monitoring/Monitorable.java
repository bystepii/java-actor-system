package monitoring;

public interface Monitorable {
    void attach(ActorListener listener);
    void detach(ActorListener listener);
    void notifyListeners(ActorEvent event);
}
