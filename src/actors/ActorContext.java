package actors;

import monitoring.ActorEvent;

import java.util.HashMap;
import java.util.Set;

/**
 * An ActorContext is a container for all the Actors in the system.
 */
public class ActorContext {

    /**
     * The map of all the Actors in the system.
     */
    private static final HashMap<String, Actor> actors = new HashMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    protected ActorContext() {
    }

    /**
     * Spawn an Actor in the system, register it with the given name, and return an
     * ActorProxy for it.
     *
     * @param name  the name of the Actor. Must be unique.
     * @param actor the Actor to spawn.
     * @return an ActorProxy for the Actor.
     */
    public static ActorProxy spawnActor(String name, Actor actor) {
        actor.setName(name);
        actors.put(name, actor);
        Thread t = new Thread(actor::start);
        t.setUncaughtExceptionHandler((t1, e) -> {
            System.err.println("Thread aborted with an uncaught exception: " + e);
            e.printStackTrace();
            actor.notifyListeners(new ActorEvent(actor, ActorEvent.EventType.ABORTED));
        });
        t.start();
        return new ActorProxy(actor);
    }

    /**
     * Returns the Actor with the given name.
     *
     * @param name the name of the Actor.
     * @return the Actor with the given name.
     */
    public static Actor lookup(String name) {
        return actors.get(name);
    }

    /**
     * Returns the set of all the names of the Actors in the system.
     *
     * @return the set of all the names of the Actors in the system.
     */
    public static Set<String> getNames() {
        return actors.keySet();
    }
}

