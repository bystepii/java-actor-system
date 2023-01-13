package messages;

import actors.ActorRef;

/**
 * A message that requests an insult from {@link actors.InsultActor}.
 *
 * @see actors.InsultActor
 */
public class GetInsultMessage extends Message<String> {
    /**
     * Message to get an insult.
     *
     *
     * @param sender     the sender of the message
     * @param senderName the name of the sender
     */
    public GetInsultMessage(ActorRef sender, String senderName) {
        super(sender, senderName);
    }

    /**
     * Creates a new GetInsultMessage with no sender.
     * @param body the body of the message
     */
    public GetInsultMessage(String body) {
        super(body);
    }

    /**
     * Creates empty insult message.
     */
    public GetInsultMessage() {
        super();
    }
}
