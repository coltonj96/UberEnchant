package me.sciguymjm.uberenchant.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class for internal use.
 */
public class UberLocale {

    private final static Properties data = new Properties();

    /**
     * Utility method for internal use.
     *
     * @param file File
     * @hidden
     */
    public static void load(File file) {
        try {
            data.load(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Utility method for internal use.
     *
     * @param key  String
     * @param args Object...
     * @return String
     * @hidden
     */
    public static String get(String key, Object... args) {
        String value = data.getProperty(key);
        if (args.length > 0) {
            for (int n = 0; n < args.length; n++) {
                value = value.replace("{" + n + "}", String.valueOf(args[n]));
            }
        }
        return value;
    }

    /**
     * Utility method for internal use.
     *
     * @param color String
     * @param key  String
     * @param args Object...
     * @return String
     * @hidden
     */
    public static String get(String color, String key, Object... args) {
        return color + get(key, args);
    }
}
