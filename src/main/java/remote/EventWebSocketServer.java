package remote;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import monitoring.ActorEvent;
import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.util.*;

/**
 * This class is a WebSocket server that notifies the connected clients when an actor emits an event.
 */
public class EventWebSocketServer extends WebSocketServer {

    /**
     * Port number.
     */
    private static final int PORT = new Random().nextInt(10000) + 10000;

    /**
     * The set of subscriptions.
     */
    private static final Set<Integer> subscriptions = new HashSet<>();

    /**
     * The Map of subscriptions to connections
     */
    private static final Map<Integer, WebSocket> subscriptionIdToConnection = new HashMap<>();

    /**
     * Gson instance.
     */
    private static final Gson gson = new Gson();

    /**
     * The logger.
     */
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(EventWebSocketServer.class);

    /**
     * Default constructor.
     */
    public EventWebSocketServer() {
        super(new InetSocketAddress(PORT));
    }

    /**
     * Send an event to the specified subscription.
     *
     * @param subscriptionId the subscription id.
     * @param event          the event.
     */
    public static void sendEvent(int subscriptionId, ActorEvent event) {
        logger.info("sendEvent(): subscriptionId = {}, event = {}", subscriptionId, event);
        WebSocket conn = subscriptionIdToConnection.get(subscriptionId);
        if (conn != null) {
            String json = gson.toJson(event);
            try {
                conn.send(json);
            } catch (WebsocketNotConnectedException e) {
                logger.error("sendEvent(): Error sending event to subscriptionId = {}", subscriptionId, e);
                unsubscribe(subscriptionId);
            }
        }
    }

    /**
     * Store the subscription.
     *
     * @param subscriptionId the subscription id.
     */
    public static void subscribe(int subscriptionId) {
        if (subscriptions.contains(subscriptionId))
            throw new IllegalArgumentException("Subscription with id " + subscriptionId + " already exists");
        subscriptions.add(subscriptionId);
    }

    /**
     * Remove the subscription.
     *
     * @param subscriptionId the subscription id.
     */
    public static void unsubscribe(int subscriptionId) {
        subscriptionIdToConnection.remove(subscriptionId);
        subscriptions.remove(subscriptionId);
    }

    /**
     * Getter for the url of the server.
     *
     * @return the url.
     */
    public static String getUrl() {
        return "ws://localhost:" + PORT;
    }

    /**
     * OnOpen handler.
     *
     * @param conn      The {@link WebSocket} instance this event is occurring on.
     * @param handshake The handshake of the websocket instance
     */
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info("onOpen(): address: {}", conn.getRemoteSocketAddress());
    }

    /**
     * OnClose handler.
     *
     * @param conn   The {@link WebSocket} instance this event is occurring on.
     * @param code   The codes can be looked up here: {@link org.java_websocket.framing.CloseFrame}
     * @param reason Additional information string
     * @param remote Returns whether the closing of the connection was initiated by the remote
     *               host.
     */
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.info("onClose(): code = {}, reason = {}, remote = {}", code, reason, remote);
    }

    /**
     * OnMessage handler.
     */
    @Override
    public void onMessage(WebSocket conn, String message) {
        logger.info("onMessage(): message = {}", message);
        TypeToken<Map<String, Integer>> typeToken = new TypeToken<>() {
        };
        Map<String, Integer> map = gson.fromJson(message, typeToken.getType());
        Integer subscriptionId = map.get("subscriptionId");
        if (subscriptionId != null)
            subscriptionIdToConnection.put(subscriptionId, conn);
    }

    /**
     * OnError handler.
     *
     * @param conn Can be null if the error does not belong to one specific websocket. For example
     *             if the servers port could not be bound.
     * @param ex   The exception causing this error
     */
    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.error("onError():", ex);
    }

    /**
     * OnStart handler.
     */
    @Override
    public void onStart() {
        logger.info("onStart()");
    }
}
