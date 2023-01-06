package actors;

import messages.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("AbstractActor tests")
public class AbstractActorTest {

    String name;
    Actor actor;

    @BeforeEach
    public void setUp() {
        name = UUID.randomUUID().toString();
        actor = new HelloWorldActor();
    }

    @Test
    @DisplayName("Any actor should not allow to be started twice")
    public void testStartTwice() {
        ActorContext.spawnActor(name, actor);
        assertThrows(
                IllegalStateException.class,
                actor::start
        );
    }

    @Test
    @DisplayName("Actor modifiers should not allow null")
    public void testNullModifiers() {
        assertThrows(
                IllegalArgumentException.class,
                () -> actor.addModifier(null)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> actor.removeModifier(null)
        );
    }

    @Test
    @DisplayName("Actor name should not be null or empty")
    public void testNullEmptyName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> actor.setName(null)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> actor.setName("")
        );
    }

    @Test
    @DisplayName("Actor should not allow null messages")
    public void testNullMessages() {
        ActorContext.spawnActor(name, actor);
        assertThrows(
                IllegalArgumentException.class,
                () -> actor.send(null)
        );
    }

    @Test
    @DisplayName("Actor should stop when receiving a QuitMessage")
    public void testQuitMessage() {
        ActorProxy proxy = ActorContext.spawnActor(name, new PingActor());
        proxy.send(new PingMessage());
        assertEquals(PongMessage.class, proxy.receive().getClass());
        proxy.send(new QuitMessage());
        proxy.send(new PingMessage());
        assertThrows(
                TimeoutException.class,
                () -> proxy.receive(100)
        );
    }

    @Test
    @DisplayName("Modifiers should work")
    public void testModifiers() {
        Actor actor = new EchoActor();
        ActorProxy proxy = ActorContext.spawnActor(name, actor);

        Modifier<Message<?>> modifier = (msg) -> {
            @SuppressWarnings("unchecked")
            Message<String> message = (Message<String>) msg;
            message.setBody(message.getBody().toUpperCase());
            return message;
        };

        actor.addModifier(modifier);
        proxy.send(new Message<>("Hello World!"));

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("HELLO WORLD!", proxy.receive().getBody());
        actor.removeModifier(modifier);
        proxy.send(new Message<>("Hello World!"));

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals("Hello World!", proxy.receive().getBody());
    }

    @Test
    @DisplayName("Actor should sleep for the given amount of time when receiving a SleepMessage")
    public void testSleepMessage() {
        ActorProxy proxy = ActorContext.spawnActor(name, new EchoActor());
        proxy.send(new SleepMessage(5000));
        proxy.send(new Message<>("Hello World!"));
        long start = System.currentTimeMillis();
        proxy.receive();
        long end = System.currentTimeMillis();
        assertEquals(5000, end - start, 100);
    }
}
