package messages;

import actors.Actor;

public class QuitMessage extends Message {
    public QuitMessage(Actor from, String text) {
        super(from, text);
    }

    public QuitMessage(String text) {
        super(text);
    }

    public QuitMessage(Actor from) {
        super(from);
    }

    public QuitMessage() {
        super();
    }
}
