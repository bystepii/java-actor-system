package actors;

import messages.PingMessage;
import messages.PongMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("PingActor tests")
public class PingActorTest {
    String name;
    Actor actor;

    @BeforeEach
    void setUp() {
        name = UUID.randomUUID().toString();
        actor = new PingActor();
    }

    @Test
    @DisplayName("PingActor should be able to process PingMessage")
    void testPingActor() {
        ActorProxy proxy = ActorContext.spawnActor(name, actor);
        proxy.send(new PingMessage());
        assertEquals(PongMessage.class, proxy.receive().getClass());
    }

    @Test
    @DisplayName("PingActor should be able to process PongMessage")
    void testPingActor2() {
        ActorProxy proxy = ActorContext.spawnActor(name, actor);
        proxy.send(new PongMessage());
        assertEquals(PingMessage.class, proxy.receive().getClass());
    }
}
