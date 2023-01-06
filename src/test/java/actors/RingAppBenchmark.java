package actors;

import messages.RingMessage;
import messages.SpawnActorRingMessage;
import org.openjdk.jmh.annotations.*;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1)
@Measurement(iterations = 3)
@Fork(1)
public class RingAppBenchmark {

    @State(Scope.Thread)
    public static class BenchmarkState {
        String name;
        ActorProxy proxy;

        @Setup(Level.Trial)
        public void setUp() {
            name = UUID.randomUUID().toString();
            proxy = ActorContext.spawnActor(name, new RingAppActor());
            proxy.send(new SpawnActorRingMessage(100));
            proxy.receive();
        }
    }

    @Benchmark
    public void benchmarkRingAppPlatformThreads(BenchmarkState state) {
        // 100 rounds
        state.proxy.send(new RingMessage("Hello World", 100 * 100));
        state.proxy.receive();
    }

    @State(Scope.Thread)
    public static class BenchmarkStateVirtualThreads {
        String name;
        ActorProxy proxy;

        @Setup(Level.Trial)
        public void setUp() {
            name = UUID.randomUUID().toString();
            proxy = ActorContext.spawnActor(name, new RingAppActor(), Thread.ofVirtual().factory());
            proxy.send(new SpawnActorRingMessage(100));
            proxy.receive();
        }
    }

    @Benchmark
    public void benchmarkRingAppVirtualThreads(BenchmarkStateVirtualThreads state) {
        // 100 rounds
        state.proxy.send(new RingMessage("Hello World", 100 * 100));
        state.proxy.receive();
    }
}
