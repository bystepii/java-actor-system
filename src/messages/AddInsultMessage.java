package messages;

import actors.Actor;

public class AddInsultMessage extends Message {
    public AddInsultMessage(Actor from, String text) {
        super(from, text);
    }

    public AddInsultMessage(String text) {
        super(text);
    }
}
