package monitoring;

import actors.ActorContext;
import messages.Message;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class represents a service that monitors the execution of the actors.
 * <p>
 * This class uses the Singleton pattern to ensure that there is only one instance of the service,
 * as all the actors must use the same instance.
 * <p>
 * It uses an Observer pattern to notify the observers when an actor emits an event.
 * <p>
 * This class also provides some methods to get statistics about the actor events.
 *
 * @see ActorEvent
 * @see ActorListener
 * @see Publisher
 */
public class MonitorService implements Publisher {

    /**
     * The instance of the service.
     */
    private static MonitorService instance = null;

    /**
     * The set of monitored actors. This set is used to avoid monitoring the same actor twice.
     * Must be thread-safe.
     */
    private final Set<String> monitoredActors = ConcurrentHashMap.newKeySet();

    /**
     * The list of listeners. Must be thread-safe.
     */
    private final Queue<ActorListener> listeners = new ConcurrentLinkedQueue<>();

    /**
     * The list of all events. Must be thread-safe.
     */
    private final Queue<ActorEvent> events = new ConcurrentLinkedQueue<>();

    /**
     * The list events grouped by actor. Must be thread-safe.
     */
    private final Map<String, List<ActorEvent>> actorEvents = new ConcurrentHashMap<>();

    /**
     * The number of messages sent or received by each actor. Must be thread-safe.
     */
    private final Map<String, Integer> messageCount = new ConcurrentHashMap<>();

    /**
     * The events grouped by type. Must be thread-safe.
     */
    private final Map<ActorEvent.EventType, List<ActorEvent>> eventTypesMap = new ConcurrentHashMap<>();

    /**
     * Private constructor to ensure that the class cannot be instantiated.
     */
    private MonitorService() {

    }

    /**
     * Returns the instance of the service.
     *
     * @return the instance of the service.
     */
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

    /**
     * Monitor a specific actor.
     * <p>
     * If the actor is already monitored, this method does nothing.
     *
     * @param actorName the name of the actor to monitor.
     */
    public void monitorActor(String actorName) {
        if (actorName == null)
            throw new IllegalArgumentException("The actor name cannot be null.");

        if (monitoredActors.contains(actorName))
            return;

        // Attach a listener to collect the statistics.
        attach(event -> {
            if (!event.getSource().equals(actorName))
                return;

            // Log the event to the global event list
            events.add(event);

            // Log the event to the actor's event list
            actorEvents.computeIfAbsent(event.getSource(), k -> new LinkedList<>()).add(event);

            // Add the event to the event count map
            if (event instanceof MessageEvent<?>)
                messageCount.merge(event.getSource(), 1, Integer::sum);

            // Log the event type to the event type map
            eventTypesMap.computeIfAbsent(event.getEventType(), k -> new LinkedList<>()).add(event);
        });

        monitoredActors.add(actorName);
    }

    /**
     * Monitor all the actors in the system.
     */
    public void monitorAllActors() {
        ActorContext.getNames().forEach(this::monitorActor);
    }

    /**
     * Get the information about the message traffic of the monitored actors.
     *
     * @return {@link Map} with the {@link TrafficDensity} as key and the list of actors as value.
     */
    public Map<TrafficDensity, List<String>> getTraffic() {
        Map<TrafficDensity, List<String>> traffic = new HashMap<>();

        messageCount.forEach(
                (actorName, count) ->
                        traffic.computeIfAbsent(TrafficDensity.getDensity(count), k -> new LinkedList<>())
                                .add(actorName)
        );

        return traffic;
    }

    /**
     * Get the number of messages sent or received by the specified actor.
     *
     * @param actorName the name of the actor.
     * @return the number of messages sent or received by the actor.
     * @apiNote This method returns 0 if the actor is not monitored.
     */
    public int getNumberOfMessages(String actorName) {
        return messageCount.getOrDefault(actorName, 0);
    }

    /**
     * Get the list of messages sent or received by the specified actor.
     *
     * @param actorNames the name of the actor.
     * @return the list of messages sent or received by the actor.
     * @apiNote This method returns an empty list if the actor is not monitored.
     */
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

    /**
     * Get the list of all events for the specified actors.
     *
     * @param actorNames the names of the actors.
     * @return the list of events.
     * @apiNote This method returns an empty list if the actor is not monitored.
     */
    public List<ActorEvent> getEvents(String... actorNames) {
        List<ActorEvent> filteredEvents = new LinkedList<>();
        Arrays.stream(actorNames).forEach(actorName -> {
            if (actorEvents.containsKey(actorName))
                filteredEvents.addAll(actorEvents.get(actorName));
        });
        return filteredEvents;
    }

    /**
     * Get the list of all events of the monitored actors.
     *
     * @return the list of events.
     */
    public List<ActorEvent> getAllEvents() {
        return events.stream().toList();
    }

    /**
     * Get the list of sent messages grouped by actor.
     *
     * @return the {@link Map} with the actor name as key and the list of sent messages as value.
     */
    public Map<String, List<Message<?>>> getSentMessages() {
        return filterMessages(ActorEvent.EventType.MESSAGE_SENT);
    }

    /**
     * Get the list of received messages grouped by actor.
     *
     * @return the {@link Map} with the actor name as key and the list of received messages as value.
     */
    public Map<String, List<Message<?>>> getReceivedMessages() {
        return filterMessages(ActorEvent.EventType.MESSAGE_RECEIVED);
    }

    /**
     * Filter the messages by the specified event type.
     *
     * @param eventType the event type.
     * @return the {@link Map} with the actor name as key and the list of messages as value.
     */
    private Map<String, List<Message<?>>> filterMessages(ActorEvent.EventType eventType) {
        Map<String, List<Message<?>>> result = new HashMap<>();
        events.stream()
                .filter(event -> event.getEventType() == eventType)
                .map(event -> (MessageEvent<?>) event)
                .forEach(event -> result.computeIfAbsent(event.getSource(), k -> new LinkedList<>())
                        .add(event.getMessage()));
        return result;
    }

    /**
     * Get the list of events grouped by event type.
     *
     * @return the {@link Map} with the event type as key and the list of events as value.
     */
    public Map<ActorEvent.EventType, List<ActorEvent>> getEvents() {
        return eventTypesMap;
    }

    /**
     * This enum represents the traffic density of an actor.
     */
    public enum TrafficDensity {
        /**
         * Low traffic density. (0 - 5 messages)
         */
        LOW,
        /**
         * Medium traffic density. (6 - 15 messages)
         */
        MEDIUM,
        /**
         * High traffic density. (16+ messages)
         */
        HIGH;

        /**
         * Get the traffic density for the specified number of messages.
         *
         * @param messageCount the number of messages.
         * @return the traffic density.
         */
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
