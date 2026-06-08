package me.sciguymjm.uberenchant.metrics;

import dev.faststats.ErrorTracker;
import dev.faststats.Metrics;
import dev.faststats.bukkit.BukkitContext;
import me.sciguymjm.uberenchant.UberEnchant;

public class FastStatsMetrics  {

    private static FastStatsMetrics instance;

    private final BukkitContext stats;

    private static final ErrorTracker error = ErrorTracker.contextAware();

    private FastStatsMetrics() {
        stats = new BukkitContext.Factory(UberEnchant.instance(), "db3f6be12da257245381e2c35dbf0050")
                .errorTrackerService(error)
                .metrics(Metrics.Factory::create)
                .create();
        stats.ready();
    }

    public static FastStatsMetrics getInstance() {
        if (instance == null)
            instance = new FastStatsMetrics();
        return instance;
    }

    public static void stop() {
        if (instance != null)
            instance.stats.shutdown();
    }
}
