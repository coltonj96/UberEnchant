package me.sciguymjm.uberenchant.utils.reflection;

import me.sciguymjm.uberenchant.api.utils.Rarity;
/*import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.Enchantment;*/

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

/**
 * UNUSED
 */
public class EnchantmentNMS {

    /*private static Registry<Enchantment> registry = BuiltInRegistries.ENCHANTMENT;

    public static void unlock() {
        setFieldValue("l", false);
        setFieldValue("m", new IdentityHashMap<>());
    }

    public static void lock() {
        registry.freeze();
    }

    public static void register(IEnchant enchant) {
        NMSEnchant enchantment = new NMSEnchant(enchant, getRarity(enchant.getRarity()));
        Registry.register(registry, enchant.getId(), enchantment);
    }

    public static net.minecraft.world.item.enchantment.Enchantment.Rarity getRarity(Rarity rarity) {
        return switch (rarity) {
            case RARE -> net.minecraft.world.item.enchantment.Enchantment.Rarity.RARE;
            case COMMON -> net.minecraft.world.item.enchantment.Enchantment.Rarity.COMMON;
            case UNCOMMON -> net.minecraft.world.item.enchantment.Enchantment.Rarity.UNCOMMON;
            case VERY_RARE -> net.minecraft.world.item.enchantment.Enchantment.Rarity.VERY_RARE;
        };
    }

    public static boolean setFieldValue(String name, Object value) {
        try {

            Field field = getField(name);
            if (field == null) return false;

            field.setAccessible(true);
            field.set(registry.getClass(), value);
            return true;
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Field getField(String field) {
        try {
            return registry.getClass().getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }*/
}
