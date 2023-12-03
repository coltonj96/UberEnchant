package me.sciguymjm.uberenchant.api.utils;

public enum Rarity {
    COMMON(10.0),
    UNCOMMON(5.0),
    RARE(2.0),
    VERY_RARE(1.0);

    private final double weight;

    private Rarity(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
}
