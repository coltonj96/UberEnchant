package me.sciguymjm.uberenchant.api.utils.persistence.tags;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public enum UUIDTag implements MetaTag<UUID> {
    PLAYER;

    @Override
    public String getName() {
        return name().toLowerCase();
    }

    @Override
    public PersistentDataType<?, UUID> getType() {
        return new UUIDType();
    }

    public static boolean matches(String tag) {
        return MetaTag.matches(values(), tag);
    }

    protected static class UUIDType implements PersistentDataType<String, UUID> {

        @Override
        public Class<String> getPrimitiveType() {
            return String.class;
        }

        @Override
        public Class<UUID> getComplexType() {
            return UUID.class;
        }

        @Override
        public String toPrimitive(UUID complex, PersistentDataAdapterContext context) {
            return complex.toString();
        }

        @Override
        public UUID fromPrimitive(String primitive, PersistentDataAdapterContext context) {
            return UUID.fromString(primitive);
        }
    }
}
