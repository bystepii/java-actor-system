package messages;

import actors.ActorRef;

/**
 * This class represents an encrypted message.
 *
 * @see decorators.EncryptionDecorator
 */
public class EcnryptedMessage extends Message<String> {
    /**
     * Creates a new encrypted message.
     * @param sender the sender of the message
     * @param senderName the name of the sender
     * @param body the body of the message
     */
    public EcnryptedMessage(ActorRef sender, String senderName, String body) {
        super(sender, senderName, body);
    }
}
