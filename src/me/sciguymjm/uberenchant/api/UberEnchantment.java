package me.sciguymjm.uberenchant.api;

import me.sciguymjm.uberenchant.UberEnchant;
import me.sciguymjm.uberenchant.api.utils.Rarity;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The UberEnchantment class to be extended for adding custom enchantments.
 */
public abstract class UberEnchantment extends Enchantment implements Listener {

    private static final List<UberEnchantment> enchantments = new ArrayList<>();

    // private boolean registered = false;

    /**
     * Constructs a new UberEnchantment using a unique custom NamespacedKey.
     *
     * @param key - The NamespacedKey to be used
     */
    public UberEnchantment(NamespacedKey key) {
        super(key);
        enchantments.add(this);
        UberEnchant.registerEvents(this);
    }

    /**
     * Constructs a new UberEnchantment using plugin and string to create a unique NamespacedKey.
     *
     * @param plugin - The plugin to register the key to
     * @param key    - The key as a string to be used
     */
    public UberEnchantment(Plugin plugin, String key) {
        this(new NamespacedKey(plugin, key));
    }

    /**
     * Constructs a new UberEnchantment using a unique key under UberEnchant.
     *
     * @param key - The key as a string to be used
     */
    public UberEnchantment(String key) {
        this(UberEnchant.instance(), key);
    }

    public final String getName() {
        return getKey().getKey();
    }

    /**
     * Gets the display name of the enchantment.<br>
     * (Different from using {@link #getName()} )
     *
     * @return The display name of this enchantment
     */
    public abstract String getDisplayName();

    public abstract Rarity getRarity();

    /**
     * Gets the permission for this enchantment.<br>
     * (Meant to be used within other plugins)
     *
     * @return The permission node
     */
    public abstract String getPermission();

    /**
     * Checks if the specified item contains this enchantment.
     *
     * @param item - The item to check
     * @return True if the item contains this enchantment
     * @see #containsEnchantment(ItemStack, UberEnchantment)
     */
    public final boolean containsEnchantment(ItemStack item) {
        return containsEnchantment(item, this);
    }

    /**
     * Checks if the specified item contains the specified enchantment.
     *
     * @param item        - The item to check
     * @param enchantment - The Enchantment to check for
     * @return True if the item contains the enchantment
     * @see #containsEnchantment(ItemStack)
     */
    public static boolean containsEnchantment(ItemStack item, UberEnchantment enchantment) {
        if (!item.hasItemMeta())
            return false;
        return item.getItemMeta().hasEnchant(enchantment);
    }

    /**
     * Gets the level of this Enchantment on the specified item.
     *
     * @param item - The item
     * @return The level or 0 if not found
     * @see #getLevel(ItemStack, UberEnchantment)
     */
    public final int getLevel(ItemStack item) {
        return getLevel(item, this);
    }

    /**
     * Gets the level of the specified enchantment on the specified item.
     *
     * @param item        - The item
     * @param enchantment - The enchantment
     * @return The level or 0 if not found
     * @see #getLevel(ItemStack)
     */
    public static int getLevel(ItemStack item, UberEnchantment enchantment) {
        if (containsEnchantment(item, enchantment))
            return getEnchantments(item).get(enchantment);
        return 0;
    }

    /**
     * Checks if specified item has any custom enchantments.
     *
     * @param item - The item to check
     * @return True if any custom enchantments were found
     */
    public static boolean hasEnchantments(ItemStack item) {
        return item.getEnchantments().keySet().stream().anyMatch(a -> a instanceof UberEnchantment);
    }

    /**
     * Gets a map containing all custom enchantments and their levels on this
     * item.
     *
     * @param item - The item
     * @return Map of custom enchantments (Can be empty)
     */
    public static Map<UberEnchantment, Integer> getEnchantments(ItemStack item) {
        if (!hasEnchantments(item))
            return Map.of();
        return item.getEnchantments().entrySet().stream().filter(a -> a.getKey() instanceof UberEnchantment).collect(Collectors.toMap(e -> (UberEnchantment) e.getKey(), Map.Entry::getValue));
    }

    /**
     * Gets a map containing the stored enchantments in this item.<br>
     * (Usually reserved for an enchanted book)
     *
     * @param item - The item
     * @return Map of stored custom enchantments (Can be empty)
     */
    public static Map<UberEnchantment, Integer> getStoredEnchantments(ItemStack item) {
        if (item.getItemMeta() instanceof EnchantmentStorageMeta meta) {
            if (!meta.hasStoredEnchants())
                return Map.of();
            return meta.getStoredEnchants().entrySet().stream().filter(a -> a.getKey() instanceof UberEnchantment).collect(Collectors.toMap(e -> (UberEnchantment) e.getKey(), Map.Entry::getValue));
        }
        return Map.of();
    }

    /**
     * Gets all registered custom enchantments.<br>
     * (Includes any plugins running on the server that implement UberEnchants
     * API)
     *
     * @return List of custom enchantments
     * @see #getRegisteredEnchantments(Plugin)
     * @see #getRegisteredEnchantments(String)
     */
    public static List<UberEnchantment> getRegisteredEnchantments() {
        return enchantments;
    }

    /**
     * Gets all custom enchantments registered to the specified plugin.<br>
     *
     * @param plugin - The plugin
     * @return List of custom enchantments
     * @see #getRegisteredEnchantments(String)
     */
    public static List<UberEnchantment> getRegisteredEnchantments(Plugin plugin) {
        return getRegisteredEnchantments(plugin.getName().toLowerCase(Locale.ROOT));
    }

    /**
     * Gets all custom enchantments registered to the specified plugin name.<br>
     *
     * @param plugin - The plugin name
     * @return List of custom enchantments
     * @see #getRegisteredEnchantments(String)
     */
    public static List<UberEnchantment> getRegisteredEnchantments(String plugin) {
        return enchantments.stream().filter(enchant -> enchant.getKey().getNamespace().equals(plugin)).collect(Collectors.toList());
    }

    /**
     * Registers this custom enchantment.
     *
     * @return True if successfully registered
     * @see #register(UberEnchantment)
     */
    public final boolean register() {
        return register(this);
    }

    /**
     * Registers the specified custom enchantment.
     *
     * @param enchantment - The enchantment to register
     * @return True if successfully registered
     * @see #register()
     */
    public static boolean register(UberEnchantment enchantment) {
        try {
            if (!Enchantment.isAcceptingRegistrations()) {
                Field field = Enchantment.class.getDeclaredField("acceptingNew");
                field.setAccessible(true);
                field.set(null, true);
            }
            Enchantment.registerEnchantment(enchantment);
            // enchantment.registered = true;
            return true;
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            return false;
        }
    }

    /**
     * Checks if this enchantment is registered.
     *
     * @return True if already registered
     * @see #isRegistered(UberEnchantment)
     */
    public final boolean isRegistered() {
        return isRegistered(this);
    }

    /**
     * Checks if the specified enchantment is registered.
     *
     * @param enchantment - The enchantment to check
     * @return True if already registered
     * @see #isRegistered()
     */
    public static boolean isRegistered(UberEnchantment enchantment) {
        return List.of(Enchantment.values()).contains(enchantment);
    }

    /**
     * Gets an array of all custom enchantments, including unregistered ones
     *
     * @return An array of every custom enchantment
     */
    public static UberEnchantment[] values() {
        return enchantments.toArray(UberEnchantment[]::new);
    }
}
