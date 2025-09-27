package me.sciguymjm.uberenchant.api.utils.persistence;

import me.sciguymjm.uberenchant.UberEnchant;
import me.sciguymjm.uberenchant.utils.VersionUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PDCUtils {

    protected static PersistentDataType<PersistentDataContainer, PersistentDataContainer> TAG = PersistentDataType.TAG_CONTAINER;

    protected static boolean notNull(Object... objects) {
        if (objects == null)
            return false;
        for (Object object : objects)
            if (object == null)
                return false;
        return true;
    }

    protected static NamespacedKey generateKey(String key) {
        return new NamespacedKey(UberEnchant.instance(), key);
    }

    public static boolean has(PersistentDataContainer pdc, NamespacedKey key) {
        if (VersionUtils.isAtLeast("1.20.4"))
            return notNull(pdc, key) && pdc.has(key);
        return notNull(pdc, key) && pdc.getKeys().contains(key);
    }

    public static boolean has(PersistentDataContainer pdc, NamespacedKey key, PersistentDataType<?,?> type) {
        return notNull(pdc, key, type) && pdc.has(key, type);
    }

    public static <T> T get(PersistentDataContainer pdc, NamespacedKey key, PersistentDataType<?,T> type) {
        return has(pdc, key, type) ? pdc.get(key, type) : null;
    }

    public static PersistentDataContainer getPDC(PersistentDataContainer pdc, NamespacedKey key) {
        return has(pdc, key, TAG) ? get(pdc, key, TAG) : null;
    }

    public static <T> PersistentDataContainer set(PersistentDataContainer pdc, NamespacedKey key, PersistentDataType<?,T> type, T value) {
        if (notNull(pdc, key, type, value))
            pdc.set(key, type, value);
        return pdc;
    }

    public static void remove(PersistentDataContainer pdc, NamespacedKey key) {
        if (notNull(pdc, key))
            pdc.remove(key);
    }

    public static PersistentDataContainer createPDC(PersistentDataContainer pdc) {
        if (notNull(pdc))
            return pdc.getAdapterContext().newPersistentDataContainer();
        return null;
    }

    public static PersistentDataContainer createPDC(PersistentDataContainer pdc, NamespacedKey key) {
        return set(pdc, key, TAG, createPDC(pdc));
    }
}