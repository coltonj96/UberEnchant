package me.sciguymjm.uberenchant.api.utils.random;

/**
 * An interface for use in the {@link me.sciguymjm.uberenchant.api.utils.random.WeightedChance} class<br>
 * Used for custom classes that require a weighted chance for random selection
 *
 * @param <E> The type of value
 */
public interface Weighted<E> {

    /**
     * Gets the value
     *
     * @return The value
     */
    public E value();

    /**
     * Gets the weight
     *
     * @return The weight
     */
    public double weight();
}
