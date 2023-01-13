package remote;

import actors.Actor;
import actors.ActorContext;
import actors.ActorProxy;
import actors.ActorRef;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import messages.Message;
import monitoring.ActorEvent;
import monitoring.ActorListener;
import monitoring.MonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is a controller for both the JSON-RPC API and the XML-RPC API.
 */
public class RpcApiController {

    /**
     * The logger for this class.
     */
    private static final Logger logger = LoggerFactory.getLogger(RpcApiController.class);

    /**
     * The map with the subscriptions to the events of the actors.
     */
    private static final Map<Integer, ActorListener> subscriptions = new HashMap<>();

    /**
     * The instance of the WebSocket server that sends the events to the clients.
     */
    private static EventWebSocketServer server;

    /**
     * Default constructor.
     */
    public RpcApiController() {

    }

    /**
     * Returns a success response (status = "ok").
     *
     * @return a success response.
     */
    public static Map<String, Object> ok() {
        return Map.of("status", "ok");
    }

    /**
     * Returns a success response (status = "ok") with the specified result.
     *
     * @param result the result of the operation.
     * @return a success response.
     */
    public static Map<String, Object> ok(Object result) {
        return Map.of(
                "status", "ok",
                "result", result
        );
    }

    /**
     * Returns an error response (status = "error")
     *
     * @return an error response.
     */
    public static Map<String, Object> error() {
        return Map.of("status", "error");
    }

    /**
     * Returns an error response (status = "error") with the specified error message.
     *
     * @param message the error message.
     * @return an error response.
     */
    public static Map<String, Object> error(Object message) {
        return Map.of(
                "status", "error",
                "message", message
        );
    }

    /**
     * Echo the specified message. For testing purposes only.
     *
     * @param message the message to echo.
     * @return a success response with the message.
     */
    @JsonRpcMethod("echo")
    public Map<String, Object> echo(String message) {
        logger.info("echo(): message = {}", message);
        return ok(message);
    }

    /**
     * Ping the server. For testing purposes only.
     *
     * @return a success response.
     */
    @JsonRpcMethod("ping")
    public Map<String, Object> ping() {
        logger.info("ping()");
        return ok("pong");
    }

    /**
     * Spawn a new actor with the specified name and class.
     *
     * @param actorName  the name of the actor.
     * @param actorClass the class of the actor.
     * @return a success response if the actor was spawned successfully, an error response otherwise.
     * @apiNote The actor class must be a subclass of {@link actors.Actor}.
     * The name of the class can omit the package name.
     * @throws ClassNotFoundException if the class was not found.
     * @throws NoSuchMethodException if the class does not have a constructor with no parameters.
     * @throws IllegalAccessException if the class or its constructor is not accessible.
     * @throws InvocationTargetException if the constructor throws an exception.
     * @throws InstantiationException if the class is abstract.
     */
    @JsonRpcMethod("api.spawnActor")
    public Map<String, Object> spawnActor(String actorName, String actorClass) throws NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        logger.info("spawnActor(): actorName = {}, actorClass = {}", actorName, actorClass);

        Class<?> clazz;
        try {
            clazz = Class.forName(actorClass);
        } catch (ClassNotFoundException e) {
            clazz = Class.forName("actors." + actorClass);
        }
        ActorContext.spawnActor(actorName, (Actor) clazz.getConstructor().newInstance());
        logger.info("spawnActor(): Actor spawned: {}", actorName);
        return ok();
    }

    /**
     * Get the list of the actors.
     *
     * @return a success response with the list of the actors.
     */
    @JsonRpcMethod("api.getNames")
    public Map<String, Object> getNames() {
        logger.info("getNames()");
        return ok(ActorContext.getNames().stream().toList());
    }

    /**
     * Send a generic message to the specified actor.
     *
     * @param actorName   the name of the actor.
     * @param messageBody the body of the message.
     * @return a success response if the message was sent successfully, an error response otherwise.
     */
    @JsonRpcMethod("api.send")
    public Map<String, Object> send(String actorName, String messageBody) {
        logger.info("send(): actorName = {}, messageBody = {}", actorName, messageBody);
        ActorRef actorRef = ActorContext.lookupProxy(actorName);
        if (actorRef == null) {
            logger.error("send(): Actor not found: {}", actorName);
            return error("Actor not found: " + actorName);
        }
        actorRef.send(new Message<>(messageBody));
        logger.info("send(): Message sent");
        return ok();
    }

    /**
     * Send a specific message to the specified actor.
     *
     * @param actorName    the name of the actor.
     * @param messageClass the class of the message.
     * @param messageArgs  the arguments of the message.
     * @return a success response if the message was sent successfully, an error response otherwise.
     * @throws ClassNotFoundException if the class was not found.
     * @throws NoSuchMethodException if the class does not have a constructor with the specified arguments.
     * @throws IllegalAccessException if the class or its constructor is not accessible.
     * @throws InvocationTargetException if the constructor throws an exception.
     * @throws InstantiationException if the class is abstract.
     */
    @JsonRpcMethod("api.send")
    public Map<String, Object> send(String actorName, String messageClass, Object[] messageArgs)
            throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException, ClassNotFoundException {
        logger.info("send(): actorName = {}, messageClass = {}, messageArgs = {}",
                actorName, messageClass, Arrays.stream(messageArgs).reduce("", (a, b) -> a + ", " + b));

        String[] names = {
                messageClass,
                "messages." + messageClass,
                messageClass + "Message",
                "messages." + messageClass + "Message"
        };

        Class<?> clazz = null;
        ClassNotFoundException exception = null;
        for (String name : names) {
            try {
                clazz = Class.forName(name);
            } catch (ClassNotFoundException e) {
                exception = e;
            }
        }

        if (clazz == null) {
            logger.error("send(): {} message class not found: ", messageClass, exception);
            throw exception;
        }

        Class<?>[] types = Arrays.stream(messageArgs)
                .map(Object::getClass)
                .map((c) -> {
                    if (c.isPrimitive()) return c;
                    try {
                        Class<?> claz = Class.forName("java.lang." + c.getSimpleName());
                        return (Class<?>) claz.getField("TYPE").get(null);
                    } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                        return c;
                    }
                })
                .toArray(Class<?>[]::new);
        Constructor<?> constructor = clazz.getConstructor(types);
        Object msg = constructor.newInstance(messageArgs);
        ActorContext.lookupProxy(actorName).send((Message<?>) msg);
        logger.info("send(): Message sent");
        return ok();
    }

    /**
     * Receive a message from the specified actor using {@link ActorProxy#receive()} method.
     *
     * @param name the name of the actor.
     * @return a success response with the message body
     */
    @JsonRpcMethod("api.receive")
    public Map<String, Object> receive(String name) {
        logger.info("receive: name = {}", name);
        return ok(ActorContext.lookupProxy(name).receive().getBody());
    }

    /**
     * Receive a message from the specified actor using {@link ActorProxy#receive(long)} method,
     * with the specified timeout.
     *
     * @param name the name of the actor.
     * @param timeout the timeout in milliseconds.
     * @return a success response with the message body
     * @throws TimeoutException if the timeout expires.
     */
    @JsonRpcMethod("api.receive")
    public Map<String, Object> receive(String name, long timeout) throws TimeoutException {
        logger.info("receive: name = {}, timeout = {}", name, timeout);
        return ok(ActorContext.lookupProxy(name).receive(timeout).getBody());
    }

    /**
     * Subscribe to the specified events and actors.
     *
     * @param eventTypes the list of the event types.
     * @param actorNames the list of the actor names, if the list is empty, subscribe to all actors.
     * @return a success response if the subscription was successful with the subscription id and the
     * websocket url, an error response otherwise.
     */
    @JsonRpcMethod("api.subscribe")
    public Map<String, Object> subscribe(List<String> eventTypes, List<String> actorNames) {
        logger.info(
                "subscribe(): eventTypes = {}, actorNames = {}",
                eventTypes.stream().reduce((a, b) -> a + ", " + b).orElse(""),
                actorNames.stream().reduce((a, b) -> a + ", " + b).orElse("")
        );

        List<ActorEvent.EventType> eventTypesEnum = eventTypes.stream()
                .map(ActorEvent.EventType::valueOf)
                .toList();

        if (server == null) {
            logger.warn("subscribe(): Server not initialized");
            server = new EventWebSocketServer();
            server.start();
            logger.info("subscribe(): Server started");
        }

        int subscriptionId = UUID.randomUUID().hashCode();

        EventWebSocketServer.subscribe(subscriptionId);

        ActorListener listener = new ActorListenerImpl(subscriptionId, eventTypesEnum, actorNames);
        subscriptions.put(subscriptionId, listener);
        MonitorService.getInstance().attach(listener);

        Map<String, Object> result = Map.of(
                "subscriptionId", subscriptionId,
                "url", EventWebSocketServer.getUrl()
        );

        logger.info("subscribe(): result = {}", result);

        return ok(result);
    }

    /**
     * Unsubscribe from the specified events and actors.
     *
     * @param subscriptionId the subscription id.
     * @return a success response if the unsubscription was successful.
     */
    @JsonRpcMethod("api.unsubscribe")
    public Map<String, Object> unsubscribe(int subscriptionId) {
        logger.info("unsubscribe: subscriptionId = {}", subscriptionId);
        MonitorService.getInstance().detach(subscriptions.get(subscriptionId));
        EventWebSocketServer.unsubscribe(subscriptionId);
        return ok();
    }

    /**
     * Actor listener implementation to store the subscription id, the event types and the actor names.
     *
     * @param subscriptionId the subscription id.
     * @param eventTypes     the list of the event types.
     * @param actorNames     the list of the actor names.
     */
    private record ActorListenerImpl(
            int subscriptionId,
            List<ActorEvent.EventType> eventTypes,
            List<String> actorNames
    ) implements ActorListener {
        @Override
        public void onEvent(ActorEvent event) {
            if (eventTypes.contains(event.getEventType()) &&
                    (actorNames.isEmpty() || actorNames.contains(event.getSource())))
                EventWebSocketServer.sendEvent(subscriptionId, event);
        }
    }
}
