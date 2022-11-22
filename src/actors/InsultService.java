package actors;

import java.util.List;

public interface InsultService {
    void addInsult(String insult);

    List<String> getAllInsults();

    String getInsult();

    void end();
}
