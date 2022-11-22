package actors;

/**
 * Represents a modifier that can be applied to an Actor.
 *
 * @param <T> the type of the message that can be modified.
 */
public interface Modifier<T> {

    /**
     * Modifies the message object.
     *
     * @param object the message object to modify.
     */
    T modify(T object);
}
