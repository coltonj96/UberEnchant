package me.sciguymjm.uberenchant.utils.reflection;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.utils.VersionUtils;
/*import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;*/
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * UNUSED
 */
public class ReflectUtil {

    /*public static void register(Enchantment enchantment) {
        if (VersionUtils.isAtLeast("1.20.4")) {

        } else {
            setAcceptingRegistrations(true);
            registerEnchantment(enchantment);
            setAcceptingRegistrations(false);
        }
    }

    public static void load() {
        setAcceptingRegistrations(true);
        registerEnchantment(null);
        setAcceptingRegistrations(false);
    }

    private static void setAcceptingRegistrations(boolean value) {
        try {
            Class<Enchantment> enchantmentClass = Enchantment.class;
            Field field = enchantmentClass.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.set(null, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerEnchantment(Enchantment enchantment) {
        try {
            Class<Enchantment> enchantmentClass = Enchantment.class;
            Method registerEnchantment = enchantmentClass.getDeclaredMethod("registerEnchantment", Enchantment.class);
            registerEnchantment.invoke(null, enchantment);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void unlockRegistry() {
    }

    public static void unload() {
        try {
            Class<Enchantment> enchantmentClass = Enchantment.class;
            Field fieldByKey = enchantmentClass.getDeclaredField("byKey");
            Field fieldByName = enchantmentClass.getDeclaredField("byName");
            fieldByKey.setAccessible(true);
            fieldByName.setAccessible(true);
            HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) fieldByKey.get(null);
            HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) fieldByName.get(null);
            for (Enchantment enchantment : UberEnchantment.values()) {
                if (Registry.ENCHANTMENT.get(enchantment.getKey()) != null) {
                    byKey.remove(enchantment.getKey());
                    byName.remove(enchantment.getKey().getKey());
                }
            }
        } catch (Exception ignored) {}
    }*/
}
