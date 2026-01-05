package me.sciguymjm.uberenchant.api.utils.persistence.tags;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.utils.BoolTagMap;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.Set;

public enum ConditionalTag implements MetaTag<BoolTagMap> {
    CONDITIONS;

    @Override
    public String getName() {
        return name().toLowerCase();
    }

    @Override
    public PersistentDataType<?, BoolTagMap> getType() {
        return new ConditionalType();
    }

    public static boolean matches(String tag) {
        return MetaTag.matches(values(), tag);
    }

    public boolean test(ItemStack item, UberEnchantment enchantment) {
        BoolTagMap boolTagMap = get(item, enchantment);
        return boolTagMap != null && test(boolTagMap, item, enchantment);
    }

    private boolean test(BoolTagMap boolTagMap, ItemStack item, UberEnchantment enchantment) {
        Map<BoolTag, Boolean> map = boolTagMap.getMap();
        Set<Map.Entry<BoolTag, Boolean>> entries = map.entrySet();
        return !map.isEmpty() && entries.stream().allMatch(entry -> entry.getKey().testValue(item, enchantment, entry.getValue()));
    }

    protected static class ConditionalType implements PersistentDataType<String, BoolTagMap> {

        @Override
        public Class<String> getPrimitiveType() {
            return String.class;
        }

        @Override
        public Class<BoolTagMap> getComplexType() {
            return BoolTagMap.class;
        }

        @Override
        public String toPrimitive(BoolTagMap complex, PersistentDataAdapterContext context) {
            return complex.toString();
        }

        @Override
        public BoolTagMap fromPrimitive(String primitive, PersistentDataAdapterContext context) {
            return new BoolTagMap(primitive);
        }
    }
}