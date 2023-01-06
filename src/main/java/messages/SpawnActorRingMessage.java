package messages;

import actors.ActorRef;

/**
 * A message that spawns a new ring of RingActors.
 *
 * @see actors.RingAppActor
 * @see actors.RingActor
 */
public class SpawnActorRingMessage extends Message<Integer> {

    /**
     * Default constructor.
     * @param sender the sender of the message.
     * @param senderName the name of the sender.
     * @param numberOfActors the number of actors to spawn.
     */
    public SpawnActorRingMessage(ActorRef sender, String senderName, Integer numberOfActors) {
        super(sender, senderName, numberOfActors);
    }

    /**
     * Empty constructor.
     * @param numActors the number of actors to spawn.
     */
    public SpawnActorRingMessage(int numActors) {
        setBody(numActors);
    }

    /**
     * Get the number of actors to spawn.
     * @return the number of actors to spawn.
     */
    public int getNumActors() {
        return getBody();
    }
}
