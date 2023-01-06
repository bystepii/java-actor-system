package messages;

import actors.ActorRef;

/**
 * A message that requests an insult from {@link actors.InsultActor}.
 *
 * @see actors.InsultActor
 */
public class GetInsultMessage extends Message<String> {
    /**
     * Creates a new insult message.
     *
     * @param sender     the sender of the message
     * @param senderName the name of the sender
     */
    public GetInsultMessage(ActorRef sender, String senderName) {
        super(sender, senderName);
    }

    /**
     * Creates empty insult message.
     */
    public GetInsultMessage() {
        super();
    }
}
