package remote;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import monitoring.ActorEvent;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventWebSocketServer extends WebSocketServer {

    private static final int PORT = 8888;

    private static final Map<Integer,
            AbstractMap.SimpleImmutableEntry<List<ActorEvent.EventType>,
                    List<String>>> subscriptions = new HashMap<>();

    private static final Map<Integer, Integer> subscriptionIdToConnectionId = new HashMap<>();

    private static final Map<Integer, WebSocket> connections = new HashMap<>();

    private static final Gson gson = new Gson();

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(EventWebSocketServer.class);

    public EventWebSocketServer() {
        super(new InetSocketAddress(PORT));
    }

    public static void sendEvent(int subscriptionId, ActorEvent event) {
        logger.info("sendEvent: {}", event);
        Integer connectionId = subscriptionIdToConnectionId.get(subscriptionId);
        if (connectionId == null)
            return;
        WebSocket conn = connections.get(connectionId);
        if (conn != null) {
            String json = gson.toJson(event);
            conn.send(json);
        }
    }

    public static void subscribe(int subscriptionId, List<ActorEvent.EventType> eventTypes, List<String> actorNames) {
        subscriptions.put(subscriptionId, new AbstractMap.SimpleImmutableEntry<>(eventTypes, actorNames));
    }

    public static void unsubscribe(int subscriptionId) {
        subscriptions.remove(subscriptionId);
    }

    public static String getUrl() {
        return "ws://localhost:" + PORT;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info("onOpen: {}", conn.getRemoteSocketAddress());
        connections.put(conn.hashCode(), conn);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.info("onClose: {}", conn.getRemoteSocketAddress());
        connections.remove(conn.hashCode());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        logger.info("onMessage: {}", message);
        TypeToken<Map<String, Integer>> typeToken = new TypeToken<>() {
        };
        Map<String, Integer> map = gson.fromJson(message, typeToken.getType());
        Integer subscriptionId = map.get("subscriptionId");
        if (subscriptionId != null)
            subscriptionIdToConnectionId.put(subscriptionId, conn.hashCode());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.error("onError: {}", ex.getMessage());
    }

    @Override
    public void onStart() {
        logger.info("onStart");
    }
}
