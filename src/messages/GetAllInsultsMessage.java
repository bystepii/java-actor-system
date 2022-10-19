package messages;

import actors.Actor;

public class GetAllInsultsMessage extends Message {
    public GetAllInsultsMessage(Actor from) {
        super(from);
    }

    public GetAllInsultsMessage() {
        super();
    }
}
