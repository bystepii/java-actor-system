package actors;

import java.util.List;

/**
 * Insult service interface used to test the {@link DynamicProxy}.
 */
public interface InsultService extends Service {

    /**
     * Adds an insult to the list of insults.
     *
     * @param insult the insult to add.
     */
    void addInsult(String insult);

    /**
     * Returns a random insult from the list of insults.
     *
     * @return a random insult from the list of insults.
     */
    String getInsult();

    /**
     * Returns the list of insults.
     *
     * @return the list of insults.
     */
    List<String> getAllInsults();
}
