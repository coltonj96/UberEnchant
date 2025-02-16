package me.sciguymjm.uberenchant.api.utils;

import me.sciguymjm.uberenchant.UberEnchant;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class UberRunnable extends BukkitRunnable {

    private static UberRunnable instance;

    private static final List<UberTask> actions = new ArrayList<>();

    private UberRunnable() {
        start();
    }

    public static UberRunnable getInstance() {
        if (instance == null)
            instance = new UberRunnable();
        return instance;
    }

    public synchronized static void addTask(UberTask task) {
        actions.add(task);
    }

    public boolean isRunning() {
        return instance != null && !isCancelled();
    }

    public void start() {
        if (!isRunning())
            runTaskTimer(UberEnchant.instance(), 0, 0L);
    }

    public void stop() {
        if (isRunning())
            cancel();
    }

    @Override
    public void run() {
        synchronized (this) {
            if (!actions.isEmpty())
                actions.removeIf(action -> !action.update());
        }
    }
}
