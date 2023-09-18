package me.sciguymjm.uberenchant.api.utils.random;

/**
 * A basic class implementing the {@link me.sciguymjm.uberenchant.api.utils.random.Weighted} interface for ease of use.
 *
 * @param <E> The type of entry
 */
public class WeightedEntry<E> implements Weighted<E> {

    protected double weight;
    protected E value;

    /**
     * Constructor for a WeightedEntry
     *
     * @param value The value for when selected
     * @param weight The Weight the result has
     */
    public WeightedEntry(E value, double weight) {
        this.weight = weight;
        this.value = value;
    }

    public E value() {
        return value;
    }

    public double weight() {
        return weight;
    }
}
