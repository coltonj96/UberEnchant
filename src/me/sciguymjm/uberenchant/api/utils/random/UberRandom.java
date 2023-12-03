package me.sciguymjm.uberenchant.api.utils.random;

import java.util.Random;

public class UberRandom extends Random {

    private long seed;

    public UberRandom() {
        super();
        seed = nextLong();
        setSeed(seed);
    }

    public UberRandom(long seed) {
        super(seed);
        this.seed = seed;
    }

    public long getSeed() {
        return seed;
    }
}
