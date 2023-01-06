package actors;

/**
 * Service is an interface that represents a service offered by an Actor.
 */
public interface Service {

    /**
     * Stop the actor that offers this service.
     */
    void quit();
}
