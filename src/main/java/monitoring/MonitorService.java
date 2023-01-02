package monitoring;

import actors.ActorContext;
import messages.Message;

import java.util.*;

public class MonitorService implements Publisher {

    private static MonitorService instance = null;
    private final List<ActorListener> listeners = new LinkedList<>();
    private final List<ActorEvent> events = new LinkedList<>();
    private final Map<String, List<ActorEvent>> actorEvents = new HashMap<>();
    private final Map<String, Integer> messageCount = new HashMap<>();
    private final Map<ActorEvent.EventType, List<ActorEvent>> eventTypesMap = new HashMap<>();

    protected MonitorService() {

    }

    public static MonitorService getInstance() {
        if (instance == null)
            instance = new MonitorService();

        return instance;
    }

    public void attach(ActorListener listener) {
        listeners.add(listener);
    }

    @Override
    public void detach(ActorListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void notifyListeners(ActorEvent event) {
        listeners.forEach(listener -> listener.onEvent(event));
    }

    public void monitorActor(String actorName) {
        attach(event -> {
            if (!event.getSource().equals(actorName))
                return;

            // Log the event to the global event list
            events.add(event);

            // Log the event to the actor's event list
            actorEvents.computeIfAbsent(event.getSource(), k -> new LinkedList<>()).add(event);

            // Log the message event to the message map for the actor
            if (event instanceof MessageEvent<?>)
                messageCount.merge(event.getSource(), 1, Integer::sum);

            // Log the event type to the event type map
            eventTypesMap.put(
                    event.getEventType(),
                    eventTypesMap.getOrDefault(event.getEventType(), new LinkedList<>())
            );
        });
    }

    public void monitorAllActors() {
        ActorContext.getNames().forEach(this::monitorActor);
    }

    public Map<TrafficDensity, List<String>> getTraffic() {
        Map<TrafficDensity, List<String>> traffic = new HashMap<>();

        messageCount.forEach(
                (actorName, count) ->
                        traffic.computeIfAbsent(TrafficDensity.getDensity(count), k -> new LinkedList<>())
                                .add(actorName)
        );

        return traffic;
    }

    public int getNumberOfMessages(String actorName) {
        return messageCount.getOrDefault(actorName, 0);
    }

    public List<Message<?>> getMessages(String... actorNames) {
        List<Message<?>> messages = new LinkedList<>();
        getEvents(actorNames).forEach(
                event -> {
                    if (event instanceof MessageEvent<?> messageEvent)
                        messages.add(messageEvent.getMessage());
                }
        );
        return messages;
    }

    public List<ActorEvent> getEvents(String... actorNames) {
        List<ActorEvent> filteredEvents = new LinkedList<>();
        Arrays.stream(actorNames).forEach(actorName -> {
            if (actorEvents.containsKey(actorName))
                filteredEvents.addAll(actorEvents.get(actorName));
        });
        return filteredEvents;
    }

    public List<ActorEvent> getAllEvents() {
        return events;
    }

    public Map<String, List<Message<?>>> getSentMessages() {
        return filterMessages(ActorEvent.EventType.MESSAGE_SENT);
    }

    public Map<String, List<Message<?>>> getReceivedMessages() {
        return filterMessages(ActorEvent.EventType.MESSAGE_RECEIVED);
    }

    private Map<String, List<Message<?>>> filterMessages(ActorEvent.EventType eventType) {
        Map<String, List<Message<?>>> result = new HashMap<>();
        events.stream()
                .filter(event -> event.getEventType() == eventType)
                .map(event -> (MessageEvent<?>) event)
                .forEach(event -> result.computeIfAbsent(event.getSource(), k -> new LinkedList<>())
                        .add(event.getMessage()));
        return result;
    }

    public Map<ActorEvent.EventType, List<ActorEvent>> getEvents() {
        return eventTypesMap;
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
