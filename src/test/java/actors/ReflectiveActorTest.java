package actors;

import messages.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReflectiveActor tests")
public class ReflectiveActorTest {
    String name;
    Actor actor;

    @BeforeEach
    public void setUp() {
        name = UUID.randomUUID().toString();
        actor = new ReflectiveActor(new Service() {
            public String echo(String message) {
                return message;
            }

            public String ping(String message) {
                return "Pong";
            }

            public List<Integer> methodWithList(Integer i) {
                return List.of(i, i + 1, i + 2);
            }

            public void methodWithVoid() {
                // do nothing
            }

            @Override
            public void quit() {

            }
        });
    }

    @Test
    @DisplayName("ReflectiveActor should work with existing message types")
    public void testReflectiveActor() {
        ActorProxy proxy = ActorContext.spawnActor(name, actor);

        // PingMessage will call the ping method
        proxy.send(new PingMessage("Ping"));
        assertEquals("Pong", proxy.receive().getBody());
    }

    @Test
    @DisplayName("ReflectiveActor should work with the MethodInvocationMessage")
    public void testReflectiveActorMethodInvocationMessage() {
        ActorProxy proxy = ActorContext.spawnActor(name, actor);

        // MethodInvocationMessage will call the echo method
        proxy.send(new MethodInvocationMessage("echo", new Object[]{"Hello"}));
        assertEquals("Hello", proxy.receive().getBody());
    }

    @Test
    @DisplayName("ReflectiveActor should work with the MethodInvocationMessage and a custom message type")
    public void testReflectiveActorMethodInvocationMessageCustomMessage() {
        ActorProxy proxy = ActorContext.spawnActor(name, actor);

        // MethodInvocationMessage will call the echo method
        proxy.send(new MethodInvocationMessage("methodWithList", new Object[]{1}));
        @SuppressWarnings("unchecked") // we know the type of the message
        List<Integer> result = (List<Integer>) proxy.receive().getBody();
        assertIterableEquals(List.of(1, 2, 3), result);
    }

    @Test
    @DisplayName("ReflectiveActor should work with the MethodInvocationMessage and a void method")
    public void testReflectiveActorMethodInvocationMessageVoidMethod() {
        ActorProxy proxy = ActorContext.spawnActor(name, actor);

        // MethodInvocationMessage will call the echo method
        proxy.send(new MethodInvocationMessage("methodWithVoid"));
        assertThrows(
                TimeoutException.class,
                () -> proxy.receive(1000)
        );
    }

    @Test
    @DisplayName("ReflectiveActor should stop on QuitMessage")
    public void testReflectiveActorQuitMessage() {
        ActorProxy proxy = ActorContext.spawnActor(name, actor);

        // MethodInvocationMessage will call the echo method
        proxy.send(new QuitMessage());
        proxy.send(new MethodInvocationMessage("echo", new Object[]{"Hello"}));
        assertThrows(
                TimeoutException.class,
                () -> proxy.receive(1000)
        );
    }

    @Test
    @DisplayName("ReflectiveActor should work with InsultService")
    public void testReflectiveActorInsultService() {
        ActorProxy proxy = ActorContext.spawnActor(name, new ReflectiveActor(new InsultServiceImpl()));

        List<String> insults = List.of(
                "You smell like a wet dog!",
                "You look like a monkey!",
                "You're a jerk!"
        );

        for (String insult : insults)
            proxy.send(new AddInsultMessage(insult));

        proxy.send(new MethodInvocationMessage("getInsult"));
        proxy.send(new MethodInvocationMessage("getInsult"));
        proxy.send(new GetInsultMessage());
        assertTrue(insults.contains(proxy.<String>receive().getBody()));
        assertTrue(insults.contains(proxy.<String>receive().getBody()));
        assertTrue(insults.contains(proxy.<String>receive().getBody()));

        proxy.send(new GetAllInsultsMessage());
        assertThat(proxy.<List<String>>receive().getBody()).containsExactlyInAnyOrderElementsOf(insults);
    }
}
