package monitoring;

import actors.*;
import messages.Message;
import messages.QuitMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("MonitorService tests")
public class MonitorServiceTest {
    String name;
    Actor actor;
    MonitorService monitorService;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        name = UUID.randomUUID().toString();
        actor = new EchoActor();

        // Reset the singleton instance
        Field instance = MonitorService.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);

        monitorService = MonitorService.getInstance();
    }

    @Test
    @DisplayName("MonitorService should be able to monitor actors")
    public void testMonitorActor() {
        monitorService.monitorActor(name);
        ActorContext.spawnActor(name, actor);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(1, monitorService.getAllEvents().size());
        assertEquals(ActorEvent.EventType.CREATED, monitorService.getAllEvents().get(0).getEventType());
    }

    @Test
    @DisplayName("MonitorService should be able to capture creating and stopping actors")
    public void testMonitorActorStop() {
        monitorService.monitorActor(name);
        ActorProxy proxy = ActorContext.spawnActor(name, new HelloWorldActor());
        proxy.send(new QuitMessage());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<ActorEvent> events = monitorService.getAllEvents();
        List<ActorEvent.EventType> expectedEvents = List.of(
                ActorEvent.EventType.CREATED,
                ActorEvent.EventType.MESSAGE_RECEIVED,
                ActorEvent.EventType.MESSAGE_PROCESSED,
                ActorEvent.EventType.STOPPED
        );

        assertThat(events.stream().map(ActorEvent::getEventType).collect(Collectors.toList()))
                .containsExactlyInAnyOrderElementsOf(expectedEvents);
    }

    @Test
    @DisplayName("MonitorService should be able to capture sending and receiving messages")
    public void testMonitorActorSendReceive() {
        monitorService.monitorActor(name);
        ActorProxy proxy = ActorContext.spawnActor(name, new EchoActor());
        proxy.send(new Message<>("Hello World"));
        proxy.receive();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<ActorEvent> events = monitorService.getAllEvents();
        List<ActorEvent.EventType> expectedEvents = List.of(
                ActorEvent.EventType.CREATED,
                ActorEvent.EventType.MESSAGE_SENT,
                ActorEvent.EventType.MESSAGE_RECEIVED,
                ActorEvent.EventType.MESSAGE_PROCESSED
        );

        assertThat(events.stream().map(ActorEvent::getEventType).collect(Collectors.toList()))
                .containsExactlyInAnyOrderElementsOf(expectedEvents);
    }

    @Test
    @DisplayName("MonitorService should return correct traffic density")
    public void testTrafficDensity() {
        List<ActorProxy> proxies = new ArrayList<>();
        List<String> names = new ArrayList<>();

        monitorService.monitorActor(name);
        names.add(name);
        proxies.add(ActorContext.spawnActor(name, actor));

        for (int i = 0; i < 4; i++) {
            String name = UUID.randomUUID().toString();
            monitorService.monitorActor(name);
            names.add(name);
            proxies.add(ActorContext.spawnActor(name, new EchoActor()));
        }

        int[] count = {1, 7, 18, 100, 4};

        for (int i = 0; i < proxies.size(); i++)
            for (int j = 0; j < count[i]; j++)
                proxies.get(i).send(new Message<>("Hello World"));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<MonitorService.TrafficDensity, List<String>> trafficDensityExpected = Map.of(
                MonitorService.TrafficDensity.LOW, List.of(names.get(0)),
                MonitorService.TrafficDensity.MEDIUM, List.of(names.get(1), names.get(4)),
                MonitorService.TrafficDensity.HIGH, List.of(names.get(2), names.get(3))
        );
        Map<MonitorService.TrafficDensity, List<String>> trafficDensityActual = monitorService.getTraffic();

       for (MonitorService.TrafficDensity trafficDensity : trafficDensityExpected.keySet())
           assertThat(trafficDensityActual.get(trafficDensity))
                   .containsExactlyInAnyOrderElementsOf(trafficDensityExpected.get(trafficDensity));
    }

    @Test
    @DisplayName("MonitorService should return correct number of messages")
    public void testNumberOfMessages() {
        List<ActorProxy> proxies = new ArrayList<>();
        List<String> names = new ArrayList<>();

        names.add(name);
        proxies.add(ActorContext.spawnActor(name, actor));

        for (int i = 0; i < 4; i++) {
            String name = UUID.randomUUID().toString();
            names.add(name);
            proxies.add(ActorContext.spawnActor(name, new EchoActor()));
        }

        monitorService.monitorAllActors();

        int[] count = {1, 7, 18, 100, 4};

        for (int i = 0; i < proxies.size(); i++)
            for (int j = 0; j < count[i]; j++)
                proxies.get(i).send(new Message<>("Hello World"));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < proxies.size(); i++)
            // Multiply by 2 because sent and received messages are counted
            assertEquals(count[i] * 2, monitorService.getNumberOfMessages(names.get(i)));
    }

    @Test
    @DisplayName("MonitorService should return correct messages")
    public void testMessages() {
        List<ActorProxy> proxies = new ArrayList<>();
        List<String> names = new ArrayList<>();

        monitorService.monitorActor(name);
        names.add(name);
        proxies.add(ActorContext.spawnActor(name, new HelloWorldActor()));

        for (int i = 0; i < 4; i++) {
            String name = UUID.randomUUID().toString();
            monitorService.monitorActor(name);
            names.add(name);
            proxies.add(ActorContext.spawnActor(name, new HelloWorldActor()));
        }

        // actor -> messages
        Map<String, List<String>> messages = Map.of(
                name, List.of("Hello World"),
                names.get(1), List.of("Msg1", "Msg2", "Msg3", "Msg4", "Msg5", "Msg6", "Msg7"),
                names.get(2), List.of("msg1", "msg2", "msg3"),
                names.get(3), List.of("MSG1", "MSG2", "MSG3", "MSG4", "MSG5"),
                names.get(4), List.of("m1", "m2", "m3", "m4", "m5", "m6", "m7", "m8", "m9", "m10")
        );

        for (int i = 0; i < proxies.size(); i++)
            for (String msg : messages.get(names.get(i)))
                proxies.get(i).send(new Message<>(msg));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < proxies.size(); i++) {
            List<String> expected = messages.get(names.get(i));
            @SuppressWarnings("unchecked") // We know that the messages are strings
            List<String> actual = (List<String>) monitorService.getMessages(names.get(i))
                    .stream()
                    .map(Message::getBody)
                    .toList();
            assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
        }

        // Multiple actor names

        List<String> allExpected = messages.values().stream().flatMap(List::stream).toList();
        @SuppressWarnings("unchecked") // We know that the messages are strings
        List<String> allActual = (List<String>) monitorService.getMessages(names.toArray(String[]::new))
                .stream()
                .map(Message::getBody)
                .toList();
        assertThat(allActual).containsExactlyInAnyOrderElementsOf(allExpected);
    }

    @Test
    @DisplayName("MonitorService should return correct events")
    public void testEvents() {
        monitorService.monitorActor(name);
        ActorProxy proxy = ActorContext.spawnActor(name, actor);
        String name2 = UUID.randomUUID().toString();
        monitorService.monitorActor(name2);
        ActorProxy proxy2 = ActorContext.spawnActor(name2, new EchoActor());

        proxy.send(new Message<>("Hello World"));
        proxy2.send(new Message<>("Hello!"));
        proxy.send(new QuitMessage());
        proxy2.send(new QuitMessage());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<ActorEvent> events = monitorService.getEvents(name);
        List<ActorEvent> events2 = monitorService.getEvents(name2);

        List<ActorEvent.EventType> expected = new ArrayList<>(List.of(
                ActorEvent.EventType.CREATED,
                ActorEvent.EventType.MESSAGE_RECEIVED,
                ActorEvent.EventType.MESSAGE_RECEIVED,
                ActorEvent.EventType.MESSAGE_PROCESSED,
                ActorEvent.EventType.MESSAGE_PROCESSED,
                ActorEvent.EventType.MESSAGE_SENT,
                ActorEvent.EventType.MESSAGE_SENT,
                ActorEvent.EventType.STOPPED
        ));

        assertThat(events.stream().map(ActorEvent::getEventType).toList())
                .containsExactlyInAnyOrderElementsOf(expected);
        assertThat(events2.stream().map(ActorEvent::getEventType).toList())
                .containsExactlyInAnyOrderElementsOf(expected);

        // Multiple actor names
        List<ActorEvent> allEvents = monitorService.getEvents(name, name2);
        List<ActorEvent.EventType> expected2 = Stream.concat(expected.stream(), expected.stream()).toList();
        assertThat(allEvents.stream().map(ActorEvent::getEventType).toList())
                .containsExactlyInAnyOrderElementsOf(expected2);

        // All events
        allEvents = monitorService.getAllEvents();
        assertThat(allEvents.stream().map(ActorEvent::getEventType).toList())
                .containsExactlyInAnyOrderElementsOf(expected2);
    }

    @Test
    @DisplayName("MonitorService should catch aborted event")
    public void testAbortedEvent() {
        monitorService.monitorActor(name);
        ActorProxy proxy = ActorContext.spawnActor(name, new AbstractActor() {
            @Override
            protected void process(Message<?> msg) {
                throw new RuntimeException("Test");
            }
        });
        proxy.send(new Message<>("Hello World"));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<ActorEvent> events = monitorService.getEvents(name);
        assertThat(events.stream().map(ActorEvent::getEventType).toList())
                .containsExactlyInAnyOrderElementsOf(List.of(
                        ActorEvent.EventType.CREATED,
                        ActorEvent.EventType.MESSAGE_RECEIVED,
                        ActorEvent.EventType.ABORTED
                ));
    }

    @Test
    @DisplayName("MonitorService should return correct sent and received messages")
    public void testSentAndReceivedMessages() {
        monitorService.monitorActor(name);
        ActorProxy proxy = ActorContext.spawnActor(name, actor);
        String name2 = UUID.randomUUID().toString();
        monitorService.monitorActor(name2);
        ActorProxy proxy2 = ActorContext.spawnActor(name2, new EchoActor());

        proxy.send(new Message<>("Hello World"));
        proxy2.send(new Message<>("Hello!"));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<String, List<Message<?>>> sent = monitorService.getSentMessages();
        Map<String, List<Message<?>>> received = monitorService.getReceivedMessages();

        Map<String, List<String>> sentMessages = sent.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream()
                        .map(Message::getBody)
                        .map(Object::toString)
                        .toList()));
        Map<String, List<String>> receivedMessages = received.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream()
                        .map(Message::getBody)
                        .map(Object::toString)
                        .toList()));

        assertThat(sentMessages.get(name)).containsExactlyInAnyOrderElementsOf(List.of("Hello World"));
        assertThat(sentMessages.get(name2)).containsExactlyInAnyOrderElementsOf(List.of("Hello!"));

        assertThat(receivedMessages.get(name)).containsExactlyInAnyOrderElementsOf(List.of("Hello World"));
        assertThat(receivedMessages.get(name2)).containsExactlyInAnyOrderElementsOf(List.of("Hello!"));
    }

    @Test
    @DisplayName("MonitorService should return correct events grouped by type")
    public void testEventsGroupedByType() {
        monitorService.monitorActor(name);
        ActorProxy proxy = ActorContext.spawnActor(name, actor);
        String name2 = UUID.randomUUID().toString();
        monitorService.monitorActor(name2);
        ActorProxy proxy2 = ActorContext.spawnActor(name2, new AbstractActor() {
            @Override
            protected void process(Message<?> msg) {
                throw new RuntimeException("Test");
            }
        });

        proxy.send(new Message<>("Hello World"));
        proxy2.send(new Message<>("Hello!"));
        proxy.send(new QuitMessage());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<ActorEvent.EventType, List<ActorEvent>> events = monitorService.getEvents();

        assertThat(events.get(ActorEvent.EventType.CREATED)).hasSize(2);
        assertThat(events.get(ActorEvent.EventType.MESSAGE_RECEIVED)).hasSize(3);
        assertThat(events.get(ActorEvent.EventType.MESSAGE_SENT)).hasSize(2);
        assertThat(events.get(ActorEvent.EventType.STOPPED)).hasSize(1);
        assertThat(events.get(ActorEvent.EventType.ABORTED)).hasSize(1);
    }
}
