package me.sciguymjm.uberenchant.utils;

import me.sciguymjm.uberenchant.UberEnchant;

import java.io.*;
import java.util.PropertyResourceBundle;
import java.util.logging.Level;

/**
 * Utility class for internal use.
 */
public class UberLocale {

    private static PropertyResourceBundle bundle;
    private static PropertyResourceBundle defaults;
    private static String locale;

    static {
        try {
            defaults = new PropertyResourceBundle(UberEnchant.instance().getResource("locale/en_us.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Utility method for internal use.
     *
     * @param file File
     * @hidden
     */
    public static void load(File file) {
        try {
            bundle = new PropertyResourceBundle(new FileInputStream(file));
            locale = file.getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Utility method for internal use.
     *
     * @param key Key
     * @return String
     * @hidden
     */
    public static String get(String key) {
        if (bundle.containsKey(key))
            return bundle.getString(key);
        UberEnchant.instance().getLogger().log(Level.WARNING, "!!!WARNING!!! Translation for " + key + " not found in " + locale + ", using default translation.");
        return defaults.getString(key);
    }

    /**
     * Utility method for internal use.
     *
     * @param key  String
     * @param args Object...
     * @return String
     * @hidden
     */
    public static String getF(String key, Object... args) {
        String value = get(key);
        if (args != null && args.length > 0) {
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
    public static String getCF(String color, String key, Object... args) {
        return color + getF(key, args);
    }

    /**
     * Utility method for internal use.
     *
     * @param color Color
     * @param key  Key
     * @return String
     * @hidden
     */
    public static String getC(String color, String key) {
        return color + get(key);
    }
}
