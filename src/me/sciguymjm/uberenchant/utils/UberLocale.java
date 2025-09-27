package me.sciguymjm.uberenchant.utils;

import me.sciguymjm.uberenchant.UberEnchant;
import me.sciguymjm.uberenchant.api.UberEnchantment;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Utility class for internal use.
 */
public class UberLocale {

    private static PropertyResourceBundle bundle;
    private static PropertyResourceBundle defaults;
    private static String locale;
    private static final Map<String, PropertyResourceBundle> bundles;

    static {
        bundles = new HashMap<>();
        try {
            defaults = new PropertyResourceBundle(UberEnchant.instance().getResource("locale/en_us.properties"));
            bundles.put("defaults", defaults);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void update() {
        Properties def = new Properties();
        Properties prop = new Properties();
        try {
            FileInputStream input = new FileInputStream(FileUtils.getFile("/locale/en_us.properties"));
            def.load(UberEnchant.instance().getResource("locale/en_us.properties"));
            prop.load(input);
            input.close();
            def.forEach((k, v) -> {
                if (!prop.containsKey(k))
                    prop.put(k, v);
            });
            FileOutputStream output = new FileOutputStream(FileUtils.getFile("/locale/en_us.properties"));
            prop.store(output, "");
            output.close();
            UberEnchant.log(Level.INFO, "Updated translations in locale/en_us.properties!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateEnchantments() {
        List<Enchantment> temp;
        if (VersionUtils.isAtLeast("1.20.4"))
            temp = new ArrayList<>(Registry.ENCHANTMENT.stream().toList());
        else
            temp = new ArrayList<>(Arrays.stream(Enchantment.values()).toList());
        Collections.addAll(temp, UberEnchantment.values());
        List<String> keys = temp.stream().map(enchant -> VersionUtils.getKey(enchant).toString().replace(":", ".")).toList();
        File file = FileUtils.getFile("/locale/enchantments.properties");
        Properties prop = new Properties();
        //Properties def = new Properties();
        try {
            file.createNewFile();
            FileInputStream input = new FileInputStream(file);
            //def.load(UberEnchant.instance().getResource("locale/en_us.properties"));
            prop.load(input);
            input.close();
            /*def.forEach((k, v) -> {
                if (!prop.containsKey(k))
                    prop.put(k, v);
            });*/
            keys.forEach(key -> {
                if (!prop.containsKey(key))
                    prop.put(key, key.split("\\.")[1]);
            });
            temp.forEach(e -> {
                prop.remove(VersionUtils.getKey(e).toString().replace(":", "."));
            });
            FileOutputStream output = new FileOutputStream(file);
            prop.store(output, "");
            output.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            bundles.put("loaded", bundle);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void add(String name, File file) {
        try {
            PropertyResourceBundle prb = new PropertyResourceBundle(new FileInputStream(file));
            bundles.put(name, prb);
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
        if (defaults.containsKey(key)) {
            UberEnchant.log(Level.WARNING, "Translation for " + key + " not found in " + locale + ", using default translation.");
            return defaults.getString(key);
        }
        PropertyResourceBundle enchants = bundles.get("enchantments");
        if (enchants.containsKey(key))
            return enchants.getString(key);
        UberEnchant.log(Level.WARNING, "No translation found for " + key + "!");
        return key;
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
