package actors;

import java.util.List;

public interface InsultService extends Service {
    void addInsult(String insult);

    List<String> getAllInsults();

    String getInsult();
}
