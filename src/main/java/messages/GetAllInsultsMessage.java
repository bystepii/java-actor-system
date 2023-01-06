package messages;

import actors.ActorRef;

import java.util.List;

/**
 * A message that requests all insults from {@link actors.InsultActor}.
 *
 * @see actors.InsultActor
 */
public class GetAllInsultsMessage extends Message<List<String>> {

    /**
     * Creates a new insult message.
     *
     * @param sender     the sender of the message
     * @param senderName the name of the sender
     */
    public GetAllInsultsMessage(ActorRef sender, String senderName) {
        super(sender, senderName);
    }

    /**
     * Creates an empty insult message.
     */
    public GetAllInsultsMessage() {
        super();
    }
}
