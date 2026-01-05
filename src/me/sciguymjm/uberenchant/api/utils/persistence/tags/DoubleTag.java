package me.sciguymjm.uberenchant.api.utils.persistence.tags;

import org.bukkit.persistence.PersistentDataType;

public enum DoubleTag implements MetaTag<Double> {
    CHANCE;

    @Override
    public String getName() {
        return name().toLowerCase();
    }

    @Override
    public PersistentDataType<?, Double> getType() {
        return PersistentDataType.DOUBLE;
    }

    public static boolean matches(String tag) {
        return MetaTag.matches(values(), tag);
    }
}
