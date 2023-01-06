package messages;

import actors.ActorRef;

/**
 * A message tha adds an insult to {@link actors.InsultActor}.
 *
 * @see actors.InsultActor
 */
public class AddInsultMessage extends Message<String> {

    /**
     * Creates a new insult message.
     *
     * @param sender     the sender of the message
     * @param senderName the name of the sender
     * @param text       the insult
     */
    public AddInsultMessage(ActorRef sender, String senderName, String text) {
        super(sender, senderName, text);
    }

    /**
     * Creates a new insult message.
     *
     * @param text the insult
     */
    public AddInsultMessage(String text) {
        super(text);
    }
}
