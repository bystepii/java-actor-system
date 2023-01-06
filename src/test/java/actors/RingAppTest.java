package actors;

import messages.RingMessage;
import messages.SpawnActorRingMessage;
import monitoring.ActorEvent;
import monitoring.MessageEvent;
import monitoring.MonitorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("RingAppActor tests")
public class RingAppTest {

    String name;
    Actor actor;

    @BeforeEach
    public void setUp() {
        name = UUID.randomUUID().toString();
        actor = new RingAppActor();
    }

    @Test
    @DisplayName("RingAppActor should spawn a ring of actors")
    public void testRingApp() {
        ActorProxy proxy = ActorContext.spawnActor(name, actor);
        proxy.send(new SpawnActorRingMessage(10));
        List<String> actorNames = proxy.<List<String>>receive().getBody();
        for (String actorName : actorNames)
            assertEquals(ActorProxy.class, ActorContext.lookupProxy(actorName).getClass());

    }

    @Test
    @DisplayName("Sending a RingMessage should cause the ring to send a message")
    public void testRingMessage() {
        ActorProxy proxy = ActorContext.spawnActor(name, actor);
        proxy.send(new SpawnActorRingMessage(10));
        proxy.<List<String>>receive();

        List<RingMessage> messages = new ArrayList<>();

        MonitorService.getInstance().attach(event -> {
            if (event.getEventType() == ActorEvent.EventType.MESSAGE_RECEIVED && event instanceof MessageEvent) {
                if (((MessageEvent<?>) event).getMessage() instanceof RingMessage) {
                    @SuppressWarnings("unchecked") // We know it's a RingMessage
                    MessageEvent<RingMessage> messageEvent = (MessageEvent<RingMessage>) event;
                    messages.add(messageEvent.getMessage());
                }
            }
        });

        proxy.send(new RingMessage("Hello World", 10));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 11 because the first message is sent by the RingAppActor to the first actor in the ring
        assertEquals(11, messages.size());
    }
}
