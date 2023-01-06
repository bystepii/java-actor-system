package actors;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the {@link InsultService} interface. Used to test the {@link ReflectiveActor}.
 */
public class InsultServiceImpl implements InsultService {

    /**
     * The list of insults.
     */
    private final List<String> insults = new ArrayList<>();

    /**
     * Default constructor.
     */
    public InsultServiceImpl() {

    }

    @Override
    public void addInsult(String insult) {
        insults.add(insult);
    }

    @Override
    public List<String> getAllInsults() {
        return insults;
    }

    @Override
    public String getInsult() {
        return insults.get((int) (Math.random() * insults.size()));
    }

    @Override
    public void quit() {
    }
}
