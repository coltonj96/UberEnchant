package me.sciguymjm.uberenchant.api.utils.persistence;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PDCItemUtils extends PDCUtils {

    public static boolean has(ItemStack item, NamespacedKey key) {
        if (!notNull(item, key))
            return false;
        ItemMeta meta = item.getItemMeta();
        return notNull(meta) && has(meta.getPersistentDataContainer(), key);
    }

    public static boolean has(ItemStack item, NamespacedKey key, PersistentDataType<?,?> type) {
        return has(item, key) && has(item.getItemMeta().getPersistentDataContainer(), key, type);
    }

    public static <T> T get(ItemStack item, NamespacedKey key, PersistentDataType<?,T> type) {
        return has(item, key, type) ? get(item.getItemMeta().getPersistentDataContainer(), key, type) : null;
    }

    public static PersistentDataContainer getPDC(ItemStack item) {
        return notNull(item, item.getItemMeta()) ? item.getItemMeta().getPersistentDataContainer() : null;
    }

    public static PersistentDataContainer getPDC(ItemStack item, NamespacedKey key) {
        return has(item, key, TAG) ? getPDC(item.getItemMeta().getPersistentDataContainer(), key) : null;
    }

    public static PersistentDataContainer getOrCreatePDC(ItemStack item, NamespacedKey key) {
        return has(item, key, TAG) ? getPDC(item, key) : createPDC(item, key);
    }

    public static <T> PersistentDataContainer set(ItemStack item, NamespacedKey key, PersistentDataType<?,T> type, T value) {
        ItemMeta meta = item.getItemMeta();
        if (!notNull(meta))
            return null;
        PersistentDataContainer pdc = set(meta.getPersistentDataContainer(), key, type, value);
        item.setItemMeta(meta);
        return pdc;
    }

    public static void remove(ItemStack item, NamespacedKey key) {
        if (!notNull(item, item.getItemMeta()))
            return;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (notNull(pdc))
            remove(pdc, key);
        item.setItemMeta(meta);
    }

    public static PersistentDataContainer createPDC(ItemStack item) {
        PersistentDataContainer pdc = getPDC(item);
        if (notNull(pdc))
            return pdc.getAdapterContext().newPersistentDataContainer();
        return null;
    }

    public static PersistentDataContainer createPDC(ItemStack item, NamespacedKey key) {
        return set(item, key, TAG, createPDC(item));
    }
}
