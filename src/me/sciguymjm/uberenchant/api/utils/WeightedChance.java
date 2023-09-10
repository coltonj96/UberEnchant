package me.sciguymjm.uberenchant.api.utils;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

/**
 * Simple utility class for weighted chances<br>
 * Example Usage:
 *
 * <pre>{@code
 * WeightedChance<Boolean> wc = new WeightedChance<Boolean>(); // Create new WeightedChance for booleans
 * wc.add(0.9, false); // False 90% of the time
 * wc.add(0.1, true); // True 10% of the time
 *
 * System.out.println(wc.next()); // Print the result
 * }</pre>
 *
 * @param <E> - The type of result (Can be anything)
 */
public class WeightedChance<E> {

    private NavigableMap<Double, WeightedEntry<E>> map = new TreeMap<Double, WeightedEntry<E>>();
    private double total = 0;


    /**
     * Add a new possible selection with a percent chance.
     *
     * @param weight The chance (Between 0.0 and 1.0, 1.0 being 100% chance)
     * @param result The result
     * @return True if it was successfully added
     */
    public boolean add(double weight, E result) {
        WeightedEntry<E> a = new WeightedEntry<E>(weight, result);
        if (weight <= 0 || contains(result))
            return false;
        total += weight;
        map.put(total, a);
        return true;
    }

    /**
     * Remove possible selections if the result exists.
     *
     * @param result The result to remove
     * @return True if any were removed
     */
    public boolean remove(E result) {
        NavigableMap<Double, WeightedEntry<E>> tempMap = new TreeMap<Double, WeightedEntry<E>>();
        total = 0;
        map.values().stream().filter(entry -> !entry.result.equals(result)).forEach(entry -> {
            total += entry.weight;
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
    public boolean remove(Predicate<WeightedEntry<E>> predicate) {
        NavigableMap<Double, WeightedEntry<E>> tempMap = new TreeMap<Double, WeightedEntry<E>>();
        total = 0;
        map.values().stream().filter(entry -> !predicate.test(entry)).forEach(entry -> {
            total += entry.weight;
            tempMap.put(total, entry);
        });
        boolean change = map.size() != tempMap.size();
        map = tempMap;
        return change;
    }

    /**
     * Gets the next possible result.
     *
     * @return The next result
     */
    public E next() {
        double value = ThreadLocalRandom.current().nextDouble() * total;
        return map.ceilingEntry(value).getValue().result();
    }

    /**
     * Gets the weight of the specified result.
     *
     * @param result The result to get the weight for
     * @return The weight or 0 if not found
     */
    public double getWeight(E result) {
        WeightedEntry<E> e = map.values().stream().filter(entry -> entry.result.equals(result)).findFirst().orElse(null);
        if (e != null)
            return e.weight;
        return 0.0;
    }

    /**
     * Checks if this WeightedChance contains the specified result or not.
     *
     * @param result the result to check for
     * @return True if this WeightedChance contains the result
     */
    public boolean contains(E result) {
        return map.values().stream().anyMatch(a -> a.result.equals(result));
    }

    /**
     * A Simple record for use it storing values.
     *
     * @param weight The weight of the result
     * @param result The result
     * @param <E> The type of result
     */
    public record WeightedEntry<E>(double weight, E result) {}
}