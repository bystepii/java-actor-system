package actors;

public interface Actor extends ActorRef, ProcessModifiable {
    void start();

    void end();

    void pause();

    String getName();

    void setName(String name);
}
