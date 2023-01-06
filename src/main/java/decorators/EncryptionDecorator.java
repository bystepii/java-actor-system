package decorators;

import actors.Actor;
import messages.EcnryptedMessage;
import messages.Message;
import messages.QuitMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.AESUtil;

import java.util.UUID;

/**
 * This class is a decorator that encrypts messages sent to an actor and decrypts them before they are processed.
 */
public class EncryptionDecorator extends AbstractDecorator {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(EncryptionDecorator.class);
    /**
     * The key used to encrypt and decrypt messages.
     */
    private final String password;
    /**
     * Instance of AESUtil class.
     */
    private final AESUtil aes;


    /**
     * Creates a new encryption decorator.
     *
     * @param actor    the actor to decorate
     * @param password the password used to encrypt and decrypt messages
     */
    public EncryptionDecorator(Actor actor, String password) {
        super(actor);
        this.password = password;
        aes = new AESUtil();

        // Add a modifier to decrypt messages before they are processed
        addModifier(msg -> {
            // Only decrypt messages that are non-empty non-null encrypted messages
            if (msg instanceof EcnryptedMessage emsg
                    && emsg.getBody() instanceof String body
                    && !body.isEmpty()) {
                try {
                    return new Message<>(
                            emsg.getSender(),
                            emsg.getSenderName(),
                            aes.decrypt(body, password)
                    );
                } catch (Exception e) {
                    logger.error("Error decrypting message: ", e);
                }
            }
            return msg;
        });
    }

    /**
     * Constructor with a random password.
     *
     * @param actor the actor to decorate
     */
    public EncryptionDecorator(Actor actor) {
        this(actor, UUID.randomUUID().toString());
    }

    /**
     * Encrypts the message before it is sent.
     *
     * @param msg the message to send
     */
    @Override
    public void send(Message<?> msg) {
        // Only encrypt the body of the message if it is a non-empty non-null string
        // and if it is not a QuitMessage
        if (msg.getBody() instanceof String body && !(msg instanceof QuitMessage)) {
            try {
                EcnryptedMessage emsg = new EcnryptedMessage(
                        msg.getSender(),
                        msg.getSenderName(),
                        aes.encrypt(body, password)
                );
                actor.send(emsg);
            } catch (Exception e) {
                logger.error("Error encrypting message: ", e);
            }
        }
        actor.send(msg);
    }
}
