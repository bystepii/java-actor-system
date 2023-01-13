package actors;

import messages.Message;
import messages.QuitMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ActorContext tests")
public class ActorContextTest {

    String name;
    Actor actor;

    @BeforeEach
    public void setUp() {
        name = UUID.randomUUID().toString();
        actor = new HelloWorldActor();
    }

    @Test
    @DisplayName("Spawning an actor should return an ActorProxy")
    public void testSpawn() {
        assertEquals(
                ActorContext.spawnActor(name, actor).getClass(),
                ActorProxy.class
        );
    }

    @Test
    @DisplayName("ActorContext should not allow null or empty names")
    public void testNullEmptyName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ActorContext.spawnActor(null, actor)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> ActorContext.spawnActor("", actor)
        );
    }

    @Test
    @DisplayName("ActorContext should not allow duplicate actor names")
    public void testDuplicateName() {
        ActorContext.spawnActor("test", new HelloWorldActor());
        assertThrowsExactly(
                IllegalArgumentException.class,
                () -> ActorContext.spawnActor("test", new HelloWorldActor())
        );
    }

    @Test
    @DisplayName("lookupProxy should return the correct ActorProxy")
    public void testLookupProxy() {
        ActorProxy proxy = ActorContext.spawnActor(name, actor);
        assertEquals(proxy, ActorContext.lookupProxy(name));
    }

    @Test
    @DisplayName("lookupActor should return the correct Actor")
    public void testLookupActor() {
        ActorContext.spawnActor(name, actor);
        assertEquals(actor, ActorContext.lookupActor(name));
    }

    @Test
    @DisplayName("lookupActorRef should return the correct ActorRef")
    public void testLookupActorRef() {
        ActorContext.spawnActor(name, actor);
        assertEquals(actor, ActorContext.lookupActorRef(name));
    }

    @Test
    @DisplayName("getNames should return the correct set of names")
    public void testGetNames() {
        String name1 = UUID.randomUUID().toString();
        String name2 = UUID.randomUUID().toString();
        String name3 = UUID.randomUUID().toString();
        ActorContext.spawnActor(name1, new HelloWorldActor());
        ActorContext.spawnActor(name2, new HelloWorldActor());
        ActorContext.spawnActor(name3, new HelloWorldActor());
        assertTrue(ActorContext.getNames().contains(name1));
        assertTrue(ActorContext.getNames().contains(name2));
        assertTrue(ActorContext.getNames().contains(name3));
    }

    @Test
    @DisplayName("Uncaught exceptions should remove the actor from the system")
    public void testUncaughtException() {
        ActorContext.spawnActor(name, new AbstractActor() {
            @Override
            protected void process(Message<?> msg) {
                throw new RuntimeException("test");
            }
        });

        ActorContext.lookupProxy(name).send(new Message<>("test"));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNull(ActorContext.lookupActor(name));
        assertNull(ActorContext.lookupActorRef(name));
        assertNull(ActorContext.lookupProxy(name));
    }

    @Test
    @DisplayName("ActorContext should remove actors when they are stopped")
    public void testStop() {
        ActorProxy proxy = ActorContext.spawnActor(name, actor);
        proxy.send(new QuitMessage());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertNull(ActorContext.lookupActor(name));
        assertNull(ActorContext.lookupActorRef(name));
        assertNull(ActorContext.lookupProxy(name));
    }
}
