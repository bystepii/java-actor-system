package actors;

import messages.Message;
import messages.PingMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ActorProxy tests")
public class ActorProxyTest {

    String name;
    Actor actor;

    @BeforeEach
    public void setUp() {
        name = UUID.randomUUID().toString();
        actor = new PingActor();
    }

    @Test
    @DisplayName("ActorProxy should allow sending and receiving messages")
    public void testSendReceive() {
        ActorProxy proxy = ActorContext.spawnActor(name, actor);
        proxy.send(new PingMessage("Ping"));
        assertEquals("Pong", proxy.receive().getBody());
    }

    @Test
    @DisplayName("ActorProxy should allow sending and receiving messages with a timeout")
    public void testSendReceiveTimeout() {
        ActorProxy proxy = ActorContext.spawnActor(name, actor);
        proxy.send(new PingMessage("Ping"));
        try {
            assertEquals("Pong", proxy.receive(100).getBody());
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("ActorProxy receive with timeout should throw exception if no message is received")
    public void testReceiveTimeoutException() {
        ActorProxy proxy = ActorContext.spawnActor(name, actor);
        assertThrows(
                TimeoutException.class,
                () -> proxy.receive(100)
        );
    }

    @Test
    @DisplayName("ActorProxy should allow sending and receiving messages with custom body type")
    public void testSendReceiveCustomType() throws TimeoutException {
        ActorProxy proxy = ActorContext.spawnActor(name, new AbstractActor() {
            @Override
            protected void process(Message<?> msg) {
                if (msg.getSender() != null)
                    msg.getSender().send(new Message<>(List.of("Hello", "World")));
            }
        });

        proxy.send(new Message<>());
        List<String> body = proxy.<List<String>>receive().getBody();
        assertIterableEquals(List.of("Hello", "World"), body);

        proxy.send(new Message<>());
        List<String> body2 = proxy.<List<String>>receive(100).getBody();
        assertIterableEquals(List.of("Hello", "World"), body2);
    }
}
