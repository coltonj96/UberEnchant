package me.sciguymjm.uberenchant.api.utils.persistence.tags;

import me.sciguymjm.uberenchant.UberEnchant;
import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.api.utils.persistence.UberMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public interface MetaTag<T> {

    String getName();

    PersistentDataType<?, T> getType();

    default Plugin getPlugin() {
        return UberEnchant.instance();
    }

    default UberMeta<T> asMeta() {
        return new UberMeta<>(this);
    }

    default NamespacedKey getKey() {
        return new NamespacedKey(getPlugin(), getName());
    }

    static boolean matches(MetaTag<?>[] values, String tag) {
        return Arrays.stream(values).anyMatch(t -> t.getName().equalsIgnoreCase(tag));
    }

    static boolean exists(String tag) {
        return matches(Tags.tags.toArray(MetaTag<?>[]::new), tag);
    }

    static MetaTag<?> byName(String tag) {
        return Tags.get(tag);
    }

    static MetaTag<?> byKey(NamespacedKey key) {
        return Tags.get(key);
    }

    default T get(ItemStack item, UberEnchantment enchantment) {
        return UberUtils.getMetaTag(item, enchantment, asMeta());
    }

    default T get(ItemStack item, UberEnchantment enchantment, T def) {
        T value = get(item, enchantment);
        return value == null ? def : value;
    }

    default boolean has(ItemStack item, UberEnchantment enchantment) {
        return UberUtils.containsMeta(item, enchantment, asMeta());
    }

    default boolean testValue(ItemStack item, UberEnchantment enchantment, T value) {
        return has(item, enchantment) && get(item, enchantment).equals(value);
    }

    static <T> boolean testValues(ItemStack item, UberEnchantment enchantment, Map<? extends MetaTag<T>, T> map) {
        for (Map.Entry<? extends MetaTag<T>, T> entry: map.entrySet()) {
            if (!entry.getKey().testValue(item, enchantment, entry.getValue()))
                return false;
        }
        return true;
    }

    static <T> MetaTag<T> create(String name, PersistentDataType<?, T> type) {
        return create(name, type, UberEnchant.instance());
    }

    static <T> MetaTag<T> create(String name, PersistentDataType<?, T> type, Plugin plugin) {
        MetaTag<T> tag = new MetaTag<>() {

            @Override
            public Plugin getPlugin() {
                return plugin;
            }

            @Override
            public NamespacedKey getKey() {
                return new NamespacedKey(plugin, getName());
            }

            @Override
            public String getName() {
                return name.toLowerCase();
            }

            @Override
            public PersistentDataType<?, T> getType() {
                return type;
            }
        };
        Tags.add(tag);
        return tag;
    }

    final class Tags {
        private static final Set<MetaTag<?>> tags;

        static {
            tags = new HashSet<>();
            addAll(BoolTag.values());
            //addAll(ConditionalTag.values());
            addAll(DoubleTag.values());
            addAll(IntTag.values());
            addAll(UUIDTag.values());
        }

        public static Set<MetaTag<?>> getAll() {
            return tags;
        }

        public static <T> Set<MetaTag<T>> getAll(PersistentDataType<?, T> type) {
            return tags.stream().filter(tag -> tag.getType().equals(type)).map(tag ->
                    new MetaTag<T>() {

                        @Override
                        public NamespacedKey getKey() {
                            return tag.getKey();
                        }

                        @Override
                        public String getName() {
                            return tag.getName();
                        }

                        @Override
                        public PersistentDataType<?, T> getType() {
                            return type;
                        }
                    }).collect(Collectors.toSet());
        }

        static MetaTag<?> get(String name) {
            return tags.stream().filter(tag -> tag.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        }

        static MetaTag<?> get(NamespacedKey key) {
            return tags.stream().filter(tag -> tag.getKey().equals(key)).findFirst().orElse(null);
        }

        static void add(MetaTag<?> tag) {
            tags.add(tag);
        }

        static void addAll(MetaTag<?>[] tag) {
            tags.addAll(List.of(tag));
        }
    }
}
