package actors;

import java.util.ArrayList;
import java.util.List;

public class InsultServiceImpl implements InsultService {
    private final List<String> insults = new ArrayList<>();

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
    public void end() {
        throw new UnsupportedOperationException();
    }
}
