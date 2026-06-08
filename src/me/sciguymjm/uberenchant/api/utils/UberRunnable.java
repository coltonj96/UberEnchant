package me.sciguymjm.uberenchant.api.utils;

import me.sciguymjm.uberenchant.UberEnchant;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class UberRunnable extends BukkitRunnable {

    private static UberRunnable instance;

    private static final Map<String, UberTask> actions = new HashMap<>();

    private UberRunnable() {
        start();
    }

    public static synchronized UberRunnable getInstance() {
        if (instance == null)
            instance = new UberRunnable();
        return instance;
    }

    public synchronized static void addTask(String key, UberTask task) {
        actions.put(key, task);
    }

    private synchronized void check() {
        actions.entrySet().removeIf(action -> !action.getValue().update());
    }

    public void start() {
        runTaskTimerAsynchronously(UberEnchant.instance(), 0, 0L);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTasks(UberEnchant.instance());
    }

    @Override
    public void run() {
        if (!actions.isEmpty())
            new BukkitRunnable() {
                public void run() {
                    check();
                }
            }.runTask(UberEnchant.instance());
    }
}