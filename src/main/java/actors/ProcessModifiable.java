package actors;

/**
 * This interface represents an entity whose processing can be modified, for example, an Actor.
 *
 * @param <T> the type of the object that can be modified.
 */
public interface ProcessModifiable<T> {

    /**
     * Adds a modifier to the object.
     *
     * @param modifier the modifier to add.
     */
    void addModifier(Modifier<T> modifier);

    /**
     * Removes a modifier from the object.
     *
     * @param modifier the modifier to remove.
     */
    void removeModifier(Modifier<T> modifier);
}
