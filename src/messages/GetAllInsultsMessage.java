package messages;

import actors.ActorRef;

import java.util.List;

public class GetAllInsultsMessage extends Message<List<String>> {
    public GetAllInsultsMessage(ActorRef sender, String senderName) {
        super(sender, senderName);
    }
}
