package decorators;

import actors.Actor;
import messages.Message;
import util.AESUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

public class EncryptionDecorator extends AbstractDecorator {

    private final String password;
    private final AESUtil aes;

    public EncryptionDecorator(Actor actor, String password) {
        super(actor);
        this.password = password;
        aes = new AESUtil();

        addModifier(msg -> {
            if (msg.getBody() != null && !msg.getBody().equals("")) {
                try {
                    return new Message<>(aes.decrypt((String) msg.getBody(), password));
                } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                         NoSuchAlgorithmException | BadPaddingException | InvalidKeyException |
                         InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            }
            return msg;
        });
    }

    public EncryptionDecorator(Actor actor) {
        this(actor, UUID.randomUUID().toString());
    }

    @Override
    public void send(Message<?> msg) {
        if (msg.getBody() != null && !msg.getBody().equals("")) {
            try {
                actor.send(new Message<>(aes.encrypt(msg.getBody(), password)));
            } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                     NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        }
        actor.send(msg);
    }
}
