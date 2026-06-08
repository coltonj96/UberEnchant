package me.sciguymjm.uberenchant.metrics;

import me.sciguymjm.uberenchant.UberEnchant;
import org.bstats.bukkit.Metrics;

public class BStatsMetrics extends Metrics {

    private static BStatsMetrics instance;

    private BStatsMetrics() {
        super(UberEnchant.instance(), 1952);
    }

    public static BStatsMetrics getInstance() {
        if (instance == null)
            instance = new BStatsMetrics();
        return instance;
    }

    public static void stop() {
        if (instance != null)
            instance.shutdown();
    }
}
