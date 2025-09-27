package me.sciguymjm.uberenchant.api.utils.persistence.tags;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public enum BoolTag implements MetaTag<Boolean> {
    //ON_EQUIP,
    ON_HELD,
    ON_HIT,
    ON_DROP,
    ON_PICKUP,
    ON_CONSUME,
    HAS_CHANCE,
    HIDDEN;

    @Override
    public String getName() {
        return name().toLowerCase();
    }

    @Override
    public PersistentDataType<?, Boolean> getType() {
        return PersistentDataType.BOOLEAN;
    }

    public static boolean matches(String tag) {
        return MetaTag.matches(values(), tag);
    }

    public static BoolTag fromString(String tag) {
        return Arrays.stream(values()).filter(v -> v.getName().equalsIgnoreCase(tag)).findFirst().orElse(null);
    }

    public boolean test(ItemStack item, UberEnchantment enchantment) {
        Boolean value = get(item, enchantment);
        return value != null && value;
    }

    public boolean testBool(ItemStack item, UberEnchantment enchantment, boolean def) {
        return test(item, enchantment) || def;
    }

    public static boolean test(ItemStack item, UberEnchantment enchantment, BoolTag... tags) {
        if (tags.length == 0)
            return false;
        for (BoolTag tag : tags) {
            if (!tag.test(item, enchantment))
                return false;
        }
        return true;
    }
}
