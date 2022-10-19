package Decorators;

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

    public EncryptionDecorator(Actor actor, String password) {
        super(actor);
        this.password = password;
    }

    public EncryptionDecorator(Actor actor) {
        this(actor, UUID.randomUUID().toString());
    }

    @Override
    public void send(Message msg) {
        try {
            msg.setText(AESUtil.encrypt(msg.getText(), password));
            actor.send(msg);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | InvalidKeySpecException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void process(Message msg) {
        try {
            msg.setText(AESUtil.decrypt(msg.getText(), password));
            actor.process(msg);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException | InvalidKeySpecException e) {
            System.out.println(e.getMessage());
        }
    }
}
