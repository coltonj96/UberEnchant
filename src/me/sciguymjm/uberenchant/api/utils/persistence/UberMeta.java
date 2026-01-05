package me.sciguymjm.uberenchant.api.utils.persistence;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.*;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.Set;

public class UberMeta<T> {

    protected static Set<UberMeta<?>> values = new HashSet<>();

    //public static UberMeta<BoolTagMap> CONDITIONS = ConditionalTag.CONDITIONS.asMeta();

    public static UberMeta<Boolean> ON_HELD = BoolTag.ON_HELD.asMeta();
    public static UberMeta<Boolean> ON_HIT = BoolTag.ON_HIT.asMeta();
    public static UberMeta<Boolean> ON_DROP = BoolTag.ON_DROP.asMeta();
    public static UberMeta<Boolean> ON_PICKUP = BoolTag.ON_PICKUP.asMeta();
    public static UberMeta<Boolean> ON_CONSUME = BoolTag.ON_CONSUME.asMeta();
    public static UberMeta<Boolean> HAS_CHANCE = BoolTag.HAS_CHANCE.asMeta();
    public static UberMeta<Boolean> HIDDEN = BoolTag.HIDDEN.asMeta();

    public static UberMeta<Integer> DURATION = IntTag.DURATION.asMeta();
    public static UberMeta<Integer> LEVEL = IntTag.LEVEL.asMeta();

    public static UberMeta<Double> CHANCE = DoubleTag.CHANCE.asMeta();

    private final MetaTag<T> tag;

    public UberMeta(MetaTag<T> tag) {
        this.tag = tag;
        values.add(this);
    }

    public static <T> UberMeta<T> fromTag(MetaTag<T> tag) {
        return new UberMeta<>(tag);
    }

    public MetaTag<T> getTag() {
        return tag;
    }

    public String getName()  {
        return tag.getName();
    }

    public PersistentDataType<?,T> getType() {
        return tag.getType();
    }

    public NamespacedKey getKey() {
        return tag.getKey();
    }

    public static UberMeta<?> getByName(String name) {
        return values.stream().filter(meta -> meta.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static UberMeta<?> getByKey(NamespacedKey key) {
        return values.stream().filter(meta -> meta.tag.getKey().equals(key)).findFirst().orElse(null);
    }

    public static boolean contains(String name) {
        return values.stream().anyMatch(meta -> meta.getName().equalsIgnoreCase(name));
    }

    public static Set<UberMeta<?>> values() {
        return values;
    }

    public T get(ItemStack item, UberEnchantment enchantment) {
        return UberUtils.getMetaTag(item, enchantment,  this);
    }
}
