package actors;

import java.util.HashMap;
import java.util.Set;

public class ActorContext {
    private static final HashMap<String, Actor> actors = new HashMap<>();

    protected ActorContext() {
    }

    public static ActorProxy spawnActor(String name, Actor actor) {
        actor.setName(name);
        actors.put(name, actor);
        new Thread(actor::start).start();
        return new ActorProxy(actor);
    }

    public static Actor lookup(String name) {
        return actors.get(name);
    }

    public static Set<String> getNames() {
        return actors.keySet();
    }
}
