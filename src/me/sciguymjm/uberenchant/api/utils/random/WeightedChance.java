package me.sciguymjm.uberenchant.api.utils.random;

import java.util.*;
import java.util.function.Predicate;

/**
 * Simple utility class for weighted chances<br>
 * Example Usage:
 *
 * <pre>{@code
 * WeightedChance<Boolean> wc = new WeightedChance<Boolean>(); // Create new WeightedChance for booleans
 * wc.add(9.0, false); // False 90% of the time (a 9 out of 10 chance)
 * wc.add(1.0, true); // True 10% of the time (a 1 out of 10 chance)
 *
 * System.out.println(wc.next()); // Print the value
 * }</pre>
 *
 * @param <E> The type of value (Can be anything)
 */
public class WeightedChance<E> {

    private NavigableMap<Double, Weighted<E>> map = new TreeMap<>();
    private UberRandom random = new UberRandom();;
    private double total = 0;

    /**
     * Constructor for a WeightedChance
     */
    public WeightedChance() {}

    /**
     * Constructor for a WeightedChance
     */
    public WeightedChance(UberRandom random) {
        this.random = random;
    }

    /**
     * Constructs a WeightedChance using the specified collection
     *
     * @param collection A collection of items that implement the {@link me.sciguymjm.uberenchant.api.utils.random.Weighted} interface
     */
    public WeightedChance(Collection<? extends Weighted<E>> collection) {
        addAll(collection);
    }

    /**
     * Constructs a WeightedChance using the specified collection
     *
     * @param array An array of items that implement the {@link me.sciguymjm.uberenchant.api.utils.random.Weighted} interface
     */
    @SafeVarargs
    public WeightedChance(Weighted<E>... array) {
        addAll(Arrays.asList(array));
    }

    /**
     * Returns a new instance of a WeightedChance using specified collection<br>
     * Same as using {@link #WeightedChance(Collection)}
     *
     * @param collection An array of items that implement the {@link me.sciguymjm.uberenchant.api.utils.random.Weighted} interface
     * @return A new WeightedChance using data from the specified collection
     * @param <E> The type of value (Can be anything)
     */
    public static <E> WeightedChance<E> fromCollection(Collection<? extends Weighted<E>> collection) {
        return new WeightedChance<>(collection);
    }

    /**
     * Returns a new instance of a WeightedChance using specified array<br>
     * Same as using {@link #WeightedChance(Weighted[])}
     *
     * @param array A collection of items that implement the {@link me.sciguymjm.uberenchant.api.utils.random.Weighted} interface
     * @return A new WeightedChance using data from the specified array
     * @param <E> The type of value (Can be anything)
     */
    @SafeVarargs
    public static <E> WeightedChance<E> fromArray(Weighted<E>... array) {
        return new WeightedChance<>(array);
    }

    /**
     * Static method to select a random weighted item from a collection
     *
     * @param <E> The type of value
     * @param collection The collection
     * @return A randomly selected entry based on weight
     */
    public static <E> E select(Collection<? extends Weighted<E>> collection) {
        return select(new UberRandom(), collection);
    }

    /**
     * Static method to select a random weighted item from a collection using a random
     *
     * @param <E> The type of value
     * @param random The random to use (Good for setting a seed)
     * @param collection The collection
     * @return A randomly selected entry based on weight
     */
    public static <E> E select(UberRandom random, Collection<? extends Weighted<E>> collection) {
        return WeightedChance.fromCollection(collection).select(random);
    }

    /**
     * Add a new possible selection with a weight.
     *
     * @param value The value
     * @param weight The weight
     * @return True if it was successfully added
     */
    public boolean add(E value, double weight) {
        Weighted<E> entry = new WeightedEntry<>(value, weight);
        if (weight <= 0 || contains(value))
            return false;
        total += weight;
        map.put(total, entry);
        return true;
    }

    /**
     * Add a new possible selection.
     *
     * @param entry The selection (Implements the {@link me.sciguymjm.uberenchant.api.utils.random.Weighted} interface)
     * @return True if it was successfully added
     */
    public boolean add(Weighted<E> entry) {
        if (entry.weight() <= 0 || contains(entry))
            return false;
        total += entry.weight();
        map.put(total, entry);
        return true;
    }

    /**
     * Adds all the items in the specified collection to this WeightedChance
     *
     * @param collection The collection to add
     */
    public void addAll(Collection<? extends Weighted<E>> collection) {
        collection.forEach(this::add);
    }

    /**
     * Remove possible selections if the value exists.
     *
     * @param value The value to remove
     * @return True if any were removed
     */
    public boolean remove(E value) {
        NavigableMap<Double, Weighted<E>> tempMap = new TreeMap<>();
        total = 0;
        map.values().stream().filter(entry -> !entry.value().equals(value)).forEach(entry -> {
            total += entry.weight();
            tempMap.put(total, entry);
        });
        boolean change = map.size() != tempMap.size();
        map = tempMap;
        return change;
    }

    /**
     * Remove possible selections based on a predicate.
     *
     * @param predicate The predicate to use
     * @return True if any were removed
     */
    public boolean remove(Predicate<Weighted<E>> predicate) {
        NavigableMap<Double, Weighted<E>> tempMap = new TreeMap<>();
        total = 0;
        map.values().stream().filter(entry -> !predicate.test(entry)).forEach(entry -> {
            total += entry.weight();
            tempMap.put(total, entry);
        });
        boolean change = map.size() != tempMap.size();
        map = tempMap;
        return change;
    }

    /**
     * Selects the next possible value.
     *
     * @return The next value
     */
    public E select() {
        return select(random);
    }

    /**
     * Selects the next possible value using specified random.
     *
     * @param random The Random to use
     * @return The next value
     */
    public E select(UberRandom random) {
        double next = random.nextDouble() * total;
        return map.ceilingEntry(next).getValue().value();
    }

    /**
     * Gets the weight of the specified value.
     *
     * @param value The value to get the weight for
     * @return The weight or 0 if not found
     */
    public double getWeight(E value) {
        Weighted<E> e = map.values().stream().filter(entry -> entry.value().equals(value)).findFirst().orElse(null);
        if (e != null)
            return e.weight();
        return 0;
    }

    /**
     * Gets the total weight of all the values
     *
     * @return Total weight
     */
    public double getTotal() {
        return total;
    }

    /**
     * Checks if this WeightedChance contains the specified value or not.
     *
     * @param value the value to check for
     * @return True if this WeightedChance contains the value
     */
    public boolean contains(E value) {
        return map.values().stream().anyMatch(entry -> entry.value().equals(value));
    }

    /**
     * Checks if this WeightedChance contains the specified entry or not.
     *
     * @param weighted the entry to check for
     * @return True if this WeightedChance contains the value
     */
    public boolean contains(Weighted<E> weighted) {
        return map.values().stream().anyMatch(entry -> entry.value().equals(weighted));
    }
}