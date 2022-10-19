package messages;

import actors.Actor;

public class GetInsultMessage extends Message {
    public GetInsultMessage(Actor from) {
        super(from);
    }

    public GetInsultMessage() {
        super();
    }
}
