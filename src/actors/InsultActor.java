package actors;

import messages.AddInsultMessage;
import messages.GetAllInsultsMessage;
import messages.GetInsultMessage;
import messages.Message;

import java.util.ArrayList;

public class InsultActor extends AbstractActor {

    private final ArrayList<String> insults = new ArrayList<>();

    @Override
    public void process(Message msg) {
        switch (msg) {
            case GetInsultMessage ignored -> {
                if (msg.getFrom() != null) {
                    if (insults.size() > 0)
                        msg.getFrom().send(new Message(this, insults.get((int) (Math.random() * insults.size()))));
                    else
                        msg.getFrom().send(new Message(this, "I have no insults"));
                }
            }
            case AddInsultMessage ignored -> insults.add(msg.getText());
            case GetAllInsultsMessage ignored -> {
                if (msg.getFrom() != null)
                    msg.getFrom().send(new Message(this, insults.toString()));
            }
            default -> {
            }
        }
    }
}
