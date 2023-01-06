package actors;

import messages.AddInsultMessage;
import messages.GetAllInsultsMessage;
import messages.GetInsultMessage;
import messages.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Insult actor. Stores a list of insults and returns them when requested.
 *
 * @see GetInsultMessage
 * @see AddInsultMessage
 * @see GetAllInsultsMessage
 */
public class InsultActor extends AbstractActor {
    /**
     * The list of insults.
     */
    private final List<String> insults = new ArrayList<>();

    /**
     * Default constructor.
     */
    public InsultActor() {

    }

    @Override
    public void process(Message<?> msg) {
        switch (msg) {
            case GetInsultMessage ignored -> {
                if (msg.getSender() != null) {
                    if (insults.size() > 0)
                        msg.getSender().send(
                                new Message<>(
                                        this,
                                        name,
                                        insults.get((int) (Math.random() * insults.size()))
                                )
                        );
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
