package actors;

import monitoring.ActorEvent;
import monitoring.MonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

/**
 * An ActorContext is a container for all the Actors in the system.
 */
public class ActorContext {

    /**
     * The map of all the Actors in the system.
     */
    private static final HashMap<String, Actor> actors = new HashMap<>();

    /**
     * The map of all the ActorProxies for all the Actors in the system.
     */
    private static final HashMap<String, ActorProxy> actorProxies = new HashMap<>();

    /**
     * Default ThreadFactory for the Actors: platform threads.
     */
    private static final ThreadFactory defaultThreadFactory = Thread.ofPlatform().factory();

    /**
     * The logger for the ActorContext.
     */
    private static final Logger logger = LoggerFactory.getLogger(ActorContext.class);

    static {
        // Create a listener for finished actors
        MonitorService.getInstance().attach(event -> {
            if (event.getEventType() == ActorEvent.EventType.STOPPED) {
                actorProxies.remove(event.getSource());
                actors.remove(event.getSource());
            }
        });
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private ActorContext() {

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
        return spawnActor(name, actor, defaultThreadFactory);
    }

    /**
     * Spawn an Actor in the system, register it with the given name, and return an
     * ActorProxy for it. Use the given ThreadFactory to create the thread for the
     * Actor.
     *
     * @param name          the name of the Actor. Must be unique.
     * @param actor         the Actor to spawn.
     * @param threadFactory the ThreadFactory to use to create the thread for the Actor.
     * @return an ActorProxy for the Actor.
     */
    public static ActorProxy spawnActor(String name, Actor actor, ThreadFactory threadFactory) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Actor name cannot be null or empty");
        if (actors.containsKey(name))
            throw new IllegalArgumentException("Actor with name " + name + " already exists!");

        logger.info("spawnActor: {}", name);

        actor.setName(name);
        actors.put(name, actor);

        Thread t = threadFactory.newThread(actor::start);
        t.setUncaughtExceptionHandler((t1, e) -> {
            logger.error("Uncaught exception in actor thread", e);
            MonitorService.getInstance().notifyListeners(new ActorEvent(name, ActorEvent.EventType.ABORTED));
            actors.remove(name);
            actorProxies.remove(name);
        });
        t.start();

        ActorProxy actorProxy = new ActorProxy(actor);
        actorProxies.put(name, actorProxy);

        return actorProxy;
    }

    /**
     * Returns the ActorProxy for the actor with the given name.
     *
     * @param name the name of the Actor.
     * @return the ActorProxy for the Actor.
     */
    public static ActorProxy lookupProxy(String name) {
        return actorProxies.get(name);
    }

    /**
     * Returns the Actor with the given name.
     *
     * @param name the name of the Actor.
     * @return the Actor.
     */
    public static Actor lookupActor(String name) {
        return actors.get(name);
    }

    /**
     * Returns an ActorRef for the actor with the given name.
     *
     * @param name the name of the Actor.
     * @return the ActorRef.
     */
    public static ActorRef lookupActorRef(String name) {
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

