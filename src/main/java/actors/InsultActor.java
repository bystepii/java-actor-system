package actors;

import messages.AddInsultMessage;
import messages.GetAllInsultsMessage;
import messages.GetInsultMessage;
import messages.Message;

import java.util.ArrayList;
import java.util.List;

public class InsultActor extends AbstractActor {

    private final List<String> insults = new ArrayList<>();

    @Override
    public void process(Message<?> msg) {
        switch (msg) {
            case GetInsultMessage ignored -> {
                if (msg.getSender() != null) {
                    if (insults.size() > 0)
                        msg.getSender().send(new Message<>(this, name, insults.get((int) (Math.random() * insults.size()))));
                    else
                        msg.getSender().send(new Message<>(this, name, "I have no insults"));
                }
            }
            case AddInsultMessage ignored -> insults.add((String) msg.getBody());
            case GetAllInsultsMessage ignored -> {
                if (msg.getSender() != null)
                    msg.getSender().send(new Message<>(this, name, insults));
            }
            default -> {
            }
        }
    }
}
