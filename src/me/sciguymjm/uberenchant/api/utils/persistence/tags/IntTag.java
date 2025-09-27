package me.sciguymjm.uberenchant.api.utils.persistence.tags;

import org.bukkit.persistence.PersistentDataType;

public enum IntTag implements MetaTag<Integer> {
    DURATION,
    LEVEL;

    @Override
    public String getName() {
        return name().toLowerCase();
    }

    @Override
    public PersistentDataType<?, Integer> getType() {
        return PersistentDataType.INTEGER;
    }

    public static boolean matches(String tag) {
        return MetaTag.matches(values(), tag);
    }
}
