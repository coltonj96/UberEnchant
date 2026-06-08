package me.sciguymjm.uberenchant.utils;

import org.bukkit.plugin.Plugin;

/**
 * Utility method for internal use.
 */
public class Debugging {

    private final Plugin plugin;

    private static Debugging instance;

    private boolean debugging = false;

    private Debugging(Plugin plugin) {
        this.plugin = plugin;
    }

    public static Debugging get(Plugin plugin) {
        if (instance == null)
            instance = new Debugging(plugin);
        return instance;
    }

    public void enable() {
        debugging = true;
    }

    public void disable() {
        debugging = false;
    }

    public <T> T debugMsg(T t) {
        if (!debugging)
            return t;
        StackTraceElement trace = Thread.currentThread().getStackTrace()[2];
        System.out.printf("[%1$s][%2$s] %3$s %4$s = %5$s%n", plugin.getName(), trace.getFileName(), trace.getMethodName(), trace.getLineNumber(), t);
        return t;
    }

    public static boolean isEnabled() {
        return instance != null && instance.debugging;
    }

    public static <T> T debug(T t) {
        if (instance != null)
            return instance.debugMsg(t);
        return t;
    }

}
