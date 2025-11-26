package me.sciguymjm.uberenchant.utils;

import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Set;

public class ItemFileUtils extends FileUtils {

    private static final String items = "/other/items.yml";

    public static void save(ItemStack item, String key) {
        set(items, key.toLowerCase(Locale.ROOT), item);
    }

    public static ItemStack load(String key) {
        return get(items, key.toLowerCase(Locale.ROOT), ItemStack.class);
    }

    public static boolean delete(String key) {
        if (!contains(key))
            return false;
        set(items, key, null);
        return true;
    }

    public static boolean contains(String key) {
        return contains(items, key);
    }

    public static Set<String> getNames() {
        return loadConfig(items).getKeys(false);
    }
}
