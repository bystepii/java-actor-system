package decorators;

import actors.*;
import messages.AddClosureMessage;
import messages.Message;
import messages.QuitMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Decorators tests")
public class DecoratorsTest {
    String name;
    Actor actor;

    @BeforeEach
    public void setUp() {
        name = UUID.randomUUID().toString();
        actor = new EchoActor();
    }

    @Test
    @DisplayName("Decorators should be able to decorate sent messages")
    public void testDecoratorSend() {
        Actor decoratedActor = new AbstractDecorator(actor) {
            @Override
            public void send(Message<?> msg) {
                actor.send(new Message<>(msg.getSender(), msg.getSenderName(), "Decorated: " + msg.getBody()));
            }
        };

        ActorProxy proxy = ActorContext.spawnActor(name, decoratedActor);
        proxy.send(new Message<>("Hello World"));
        assertEquals("Decorated: Hello World", proxy.receive().getBody());
    }

    @Test
    @DisplayName("Decorators should be able to decorate processing of messages")
    public void testDecoratorReceive() {
        Actor decoratedActor = new AbstractDecorator(actor) {
            @Override
            public void send(Message<?> msg) {
                actor.send(msg);
            }
        };

        decoratedActor.addModifier(msg -> new Message<>(msg.getSender(), msg.getSenderName(), "Decorated: " + msg.getBody()));

        ActorProxy proxy = ActorContext.spawnActor(name, decoratedActor);
        proxy.send(new Message<>("Hello World"));
        proxy.send(new QuitMessage());
        assertEquals("Decorated: Hello World", proxy.receive().getBody());
    }

    @Test
    @DisplayName("EncryptionDecorator should encrypt and decrypt messages transparently")
    public void testEncryptionDecorator() {
        actor = new EncryptionDecorator(actor);
        ActorProxy proxy = ActorContext.spawnActor(name, actor);
        proxy.send(new Message<>("Hello World"));
        assertEquals("Hello World", proxy.receive().getBody());
    }

    @Test
    @DisplayName("FirewallDecorator should block messages coming from a proxy")
    public void testFirewallDecorator() {
        actor = new FirewallDecorator(actor);
        ActorProxy proxy = ActorContext.spawnActor(name, actor);
        proxy.send(new Message<>("Hello World"));
        assertThrows(TimeoutException.class, () -> proxy.receive(100));
    }

    private static class ForwardActor extends AbstractActor {
        private final ActorRef decoratedActor, targetActor;
        private ActorRef selfProxy;

        private ForwardActor(ActorRef decoratedActor, ActorRef targetActor) {
            this.decoratedActor = decoratedActor;
            this.targetActor = targetActor;
        }

        @Override
        protected void process(Message<?> msg) {
            if (msg.getSender() == targetActor) {
                msg.setSender(this);
                msg.setSenderName(name);
                selfProxy.send(msg);
            } else if (msg.getSender() != decoratedActor) {
                selfProxy = msg.getSender();
                msg.setSender(this);
                msg.setSenderName(name);
                decoratedActor.send(msg);
            }
        }
    }

    @Test
    @DisplayName("FirewallDecorator should allow messages coming from a valid actor")
    public void testFirewallDecoratorValid() {
        Actor decoratedActor = new FirewallDecorator(actor);
        ActorContext.spawnActor(name, actor);
        ForwardActor forwardActor = new ForwardActor(decoratedActor, ActorContext.lookupActorRef(name));
        ActorProxy validActorProxy = ActorContext.spawnActor(
                UUID.randomUUID().toString(),
                forwardActor
        );
        validActorProxy.send(new Message<>("Hello World"));
        assertEquals("Hello World", validActorProxy.receive().getBody());
    }

    @Test
    @DisplayName("LambdaFirewallDecorator should work as expected")
    public void testLambdaFirewallDecorator() {
        actor = new LambdaFirewallDecorator(actor);
        ActorProxy proxy = ActorContext.spawnActor(name, actor);
        proxy.send(new Message<>("Hello World"));
        assertEquals("Hello World", proxy.receive().getBody());

        proxy.send(new AddClosureMessage(msg -> (msg).getBody().toString().contains("Hello")));
        proxy.send(new Message<>("Hello World"));
        assertEquals("Hello World", proxy.receive().getBody());

        proxy.send(new Message<>("Goodbye World"));
        assertThrows(TimeoutException.class, () -> proxy.receive(100));
    }
}
