package remote.api;

import actors.Actor;
import actors.ActorContext;
import actors.ActorRef;
import com.googlecode.jsonrpc4j.JsonRpcMethod;
import messages.Message;
import monitoring.ActorEvent;
import monitoring.ActorListener;
import monitoring.MonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import remote.EventWebSocketServer;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RpcApiController {

    private static final Logger logger = LoggerFactory.getLogger(RpcApiController.class);
    private static final Map<Integer, ActorListener> subscriptions = new HashMap<>();
    private static EventWebSocketServer server;
    private static int subscriptionId = 0;

    public static Map<String, Object> ok() {
        return Map.of("status", "ok");
    }

    public static Map<String, Object> ok(Object result) {
        return Map.of(
                "status", "ok",
                "result", result
        );
    }

    public static Map<String, Object> error() {
        return Map.of("status", "error");
    }

    public static Map<String, Object> error(Object message) {
        return Map.of(
                "status", "error",
                "message", message
        );
    }

    @JsonRpcMethod("echo")
    public Map<String, Object> echo(String message) {
        logger.info("echo: {}", message);
        return ok(message);
    }

    @JsonRpcMethod("ping")
    public Map<String, Object> ping() {
        logger.info("ping");
        return ok();
    }

    @JsonRpcMethod("api.spawnActor")
    public Map<String, Object> spawnActor(String actorName, String actorClass) {
        logger.info("spawnActor: {} {}", actorName, actorClass);

        Class<?> clazz;
        try {
            clazz = Class.forName(actorClass);
        } catch (ClassNotFoundException e) {
            try {
                clazz = Class.forName("actors." + actorClass);
            } catch (ClassNotFoundException e1) {
                return error("Class not found: " + actorClass);
            }
        }
        try {
            ActorContext.spawnActor(actorName, (Actor) clazz.getConstructor().newInstance());
            return ok();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            e.printStackTrace();
            return error(e.getMessage());
        }
    }

    @JsonRpcMethod("api.getNames")
    public Map<String, Object> getNames() {
        logger.info("getNames");
        return ok(ActorContext.getNames().stream().toList());
    }

    @JsonRpcMethod("api.send")
    public Map<String, Object> send(String actorName, String messageBody) {
        logger.info("send: {} {}", actorName, messageBody);
        ActorRef actorRef = ActorContext.lookupProxy(actorName);
        if (actorRef == null)
            return error("Actor not found: " + actorName);
        actorRef.send(new Message<>(messageBody));
        return ok();
    }

    @JsonRpcMethod("api.sendSpecial")
    public Map<String, Object> sendSpecial(String actorName, String messageClass, String messageBody) {
        logger.info("sendSpecial: {} {} {}", actorName, messageClass, messageBody);

        Class<?> clazz;
        try {
            clazz = Class.forName(messageClass);
        } catch (ClassNotFoundException e) {
            try {
                clazz = Class.forName("messages." + messageClass);
            } catch (ClassNotFoundException e1) {
                return error("Class not found: " + messageClass);
            }
        }
        try {
            ActorContext.lookupProxy(actorName).send((Message<?>) clazz.getConstructor().newInstance(messageBody));
            return ok();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            e.printStackTrace();
            return error(e.getMessage());
        }
    }

    @JsonRpcMethod("api.receive")
    public Map<String, Object> receive(String name) {
        logger.info("receive: {}", name);
        return ok(ActorContext.lookupProxy(name).receive().getBody());
    }

    @JsonRpcMethod("api.subscribe")
    public Map<String, Object> subscribe(List<String> eventTypes, List<String> actorNames) {
        logger.info(
                "subscribe: {} {}",
                eventTypes.stream().reduce((a, b) -> a + ", " + b).orElse(""),
                actorNames.stream().reduce((a, b) -> a + ", " + b).orElse("")
        );

        List<ActorEvent.EventType> eventTypesEnum = eventTypes.stream()
                .map(ActorEvent.EventType::valueOf)
                .toList();

        if (server == null) {
            server = new EventWebSocketServer();
            server.start();
        }

        EventWebSocketServer.subscribe(subscriptionId, eventTypesEnum, actorNames);

        ActorListener listener = new ActorListenerImpl(subscriptionId, eventTypesEnum, actorNames);
        subscriptions.put(subscriptionId, listener);
        MonitorService.getInstance().attach(listener);

        Map<String, Object> result = Map.of(
                "subscriptionId", subscriptionId++,
                "url", EventWebSocketServer.getUrl()
        );

        return ok(result);
    }

    @JsonRpcMethod("api.unsubscribe")
    public Map<String, Object> unsubscribe(int subscriptionId) {
        logger.info("unsubscribe: {}", subscriptionId);
        MonitorService.getInstance().detach(subscriptions.get(subscriptionId));
        EventWebSocketServer.unsubscribe(subscriptionId);
        return ok();
    }

    private record ActorListenerImpl(
            int subscriptionId,
            List<ActorEvent.EventType> eventTypes,
            List<String> actorNames
    ) implements ActorListener {
        @Override
        public void onEvent(ActorEvent event) {
            if (eventTypes.contains(event.getEventType()) && actorNames.contains(event.getSource()))
                EventWebSocketServer.sendEvent(subscriptionId, event);
        }
    }
}
