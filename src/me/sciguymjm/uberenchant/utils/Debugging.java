package me.sciguymjm.uberenchant.utils;

/**
 * Utility method for internal use.
 */
public class Debugging {

    private static boolean debugging = false;

    public static void enable() {
        debugging = true;
    }

    public static void disable() {
        debugging = false;
    }

    public static <T> T debug(T t) {
        if (!debugging)
            return t;
        StackTraceElement trace = Thread.currentThread().getStackTrace()[2];
        System.out.printf("[%1$s] %2$s %3$s = %4$s%n", trace.getFileName(), trace.getMethodName(), trace.getLineNumber(), t);
        return t;
    }
}
