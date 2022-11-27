package monitoring;

import actors.Actor;
import actors.ActorContext;
import messages.Message;

import java.util.*;

public class MonitorService {

    private static MonitorService instance = null;
    private final List<ActorEvent> events = new LinkedList<>();

    protected MonitorService() {

    }

    public static MonitorService getInstance() {
        if (instance == null)
            instance = new MonitorService();

        return instance;
    }

    public void monitorActor(String actorName) {
        ActorContext.lookup(actorName).attach(events::add);
    }

    public void monitorAllActors() {
        ActorContext.getNames().forEach(this::monitorActor);
    }

    public Map<TrafficDensity, List<Actor>> getTraffic() {
        Map<Actor, Integer> messageCount = new HashMap<>();
        Map<TrafficDensity, List<Actor>> traffic = new HashMap<>();

        Arrays.stream(TrafficDensity.values()).forEach(density -> traffic.put(density, new LinkedList<>()));

        events.stream()
                .filter(event -> event instanceof MessageEvent)
                .forEach(event -> {
                    Actor actor = event.getSource();
                    messageCount.putIfAbsent(actor, 0);
                    messageCount.put(actor, messageCount.get(actor) + 1);
                });

        messageCount.forEach((actor, count) -> {
            TrafficDensity density = TrafficDensity.getDensity(count);
            traffic.get(density).add(actor);
        });

        return traffic;
    }

    public Map<Actor, List<Message<?>>> getSentMessages() {
        Map<Actor, List<Message<?>>> result = new HashMap<>();
        events.stream()
                .filter(event -> event.getEventType() == ActorEvent.EventType.MESSAGE_SENT)
                .map(event -> (MessageEvent<?>) event)
                .forEach(event -> {
                    if (!result.containsKey(event.getSource()))
                        result.put(event.getSource(), new LinkedList<>());
                    result.get(event.getSource()).add(event.getMessage());
                });
        return result;
    }

    public Map<Actor, List<Message<?>>> getReceivedMessages() {
        Map<Actor, List<Message<?>>> result = new HashMap<>();
        events.stream()
                .filter(event -> event.getEventType() == ActorEvent.EventType.MESSAGE_RECEIVED)
                .map(event -> (MessageEvent<?>) event)
                .forEach(event -> {
                    if (!result.containsKey(event.getSource()))
                        result.put(event.getSource(), new LinkedList<>());
                    result.get(event.getSource()).add(event.getMessage());
                });
        return result;
    }

    public Map<ActorEvent.EventType, ActorEvent> getEvents() {
        Map<ActorEvent.EventType, ActorEvent> result = new HashMap<>();
        events.forEach(event -> result.put(event.getEventType(), event));
        return result;
    }

    public enum TrafficDensity {
        LOW,
        MEDIUM,
        HIGH;

        public static TrafficDensity getDensity(int messageCount) {
            if (messageCount < 5)
                return LOW;
            else if (messageCount < 15)
                return MEDIUM;
            else
                return HIGH;
        }
    }
}
