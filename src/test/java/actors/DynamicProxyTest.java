package actors;

import messages.GetInsultMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DynamicProxy tests")
public class DynamicProxyTest {

    String name;
    Actor actor;

    @BeforeEach
    public void setUp() {
        name = UUID.randomUUID().toString();
        actor = new InsultActor();
    }

    @Test
    @DisplayName("DynamicProxy should work")
    public void testDynamicProxy() {
        ActorProxy proxy = ActorContext.spawnActor(name, actor);
        InsultService service = (InsultService) DynamicProxy.intercept(InsultService.class, proxy);

        List<String> insults = List.of(
                "You smell like a wet dog!",
                "You look like a monkey!",
                "You're a jerk!"
        );

        for (String insult : insults)
            service.addInsult(insult);

        assertTrue(insults.contains(service.getInsult()));
        assertTrue(insults.contains(service.getInsult()));
        assertTrue(insults.contains(service.getInsult()));
        assertIterableEquals(insults, service.getAllInsults());
    }

    @Test
    @DisplayName("end() should stop the actor")
    public void testEnd() {
        ActorProxy proxy = ActorContext.spawnActor(name, actor);
        InsultService service = (InsultService) DynamicProxy.intercept(InsultService.class, proxy);
        service.quit();
        service.addInsult("test");
        proxy.send(new GetInsultMessage());
        assertThrows(
                TimeoutException.class,
                () -> proxy.receive(1000)
        );
    }
}
