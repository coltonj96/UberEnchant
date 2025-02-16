package me.sciguymjm.uberenchant.api.utils;

import me.sciguymjm.uberenchant.UberEnchant;
import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import me.sciguymjm.uberenchant.utils.ChatUtils;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * Custom enchantment related utility class
 */
public class UberUtils {

    private static final NamespacedKey uberEnchantment = new NamespacedKey(UberEnchant.instance(), "uberenchantment");
    private static final NamespacedKey storedUberEnchantment = new NamespacedKey(UberEnchant.instance(), "storeduberenchantment");

    private static boolean hasCustom(ItemStack item, NamespacedKey namespace) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return false;
        return meta.getPersistentDataContainer().has(namespace);
    }

    public static boolean hasData(ItemStack item) {
        return hasCustom(item, uberEnchantment);
    }

    public static boolean hasStoredData(ItemStack item) {
        return hasCustom(item, storedUberEnchantment);
    }

    private static boolean containsCustom(ItemStack item, UberEnchantment data, NamespacedKey namespace) {
        return hasCustom(item, namespace) && getData(item).has(data.getKey());
    }

    public static boolean containsData(ItemStack item, UberEnchantment data) {
        return containsCustom(item, data, uberEnchantment);
    }

    public static boolean containsStoredData(ItemStack item, UberEnchantment data) {
        return containsCustom(item, data, storedUberEnchantment);
    }

    private static PersistentDataContainer getCustom(ItemStack item, NamespacedKey namespace) {
        if (hasCustom(item, namespace))
            return item.getItemMeta().getPersistentDataContainer().get(namespace, PersistentDataType.TAG_CONTAINER);
        return null;
    }

    public static PersistentDataContainer getData(ItemStack item) {
        return getCustom(item, uberEnchantment);
    }

    public static PersistentDataContainer getStoredData(ItemStack item) {
        return getCustom(item, storedUberEnchantment);
    }

    private static Map<UberEnchantment, Integer> getCustomMap(ItemStack item, NamespacedKey namespace) {
        Map<UberEnchantment, Integer> map = new HashMap<>();
        if (hasCustom(item, namespace)) {
            PersistentDataContainer data = getCustom(item, namespace);
            if (data != null) {
                data.getKeys().forEach(key -> {
                    if (UberEnchantment.containsKey(key))
                        map.put(UberEnchantment.getByKey(key), data.get(key, PersistentDataType.INTEGER));
                });
            }
        }
        return map;
    }

    public static Map<UberEnchantment, Integer> getMap(ItemStack item) {
        return getCustomMap(item, uberEnchantment);
    }

    public static Map<UberEnchantment, Integer> getStoredMap(ItemStack item) {
        return getCustomMap(item, storedUberEnchantment);
    }

    public static Map<Enchantment, Integer> getAllMap(ItemStack item) {
        Map<Enchantment, Integer> map = new HashMap<>(item.getEnchantments());
        map.putAll(getMap(item));
        return map;
    }

    public static Map<Enchantment, Integer> getAllStoredMap(ItemStack item) {
        Map<Enchantment, Integer> map = new HashMap<>();
        if (item.getItemMeta() instanceof EnchantmentStorageMeta meta) {
            if (meta.hasStoredEnchants())
                map.putAll(meta.getStoredEnchants());
            map.putAll(getStoredMap(item));
        }
        return map;
    }

    private static void addCustom(ItemStack item, UberEnchantment enchantment, int level, NamespacedKey namespace) {
        removeEnchantmentLore(item);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data;
        if (!hasCustom(item, namespace)) {
            data = meta.getPersistentDataContainer();
            data.set(namespace, PersistentDataType.TAG_CONTAINER, data.getAdapterContext().newPersistentDataContainer());
            item.setItemMeta(meta);
        }
        data = getCustom(item, namespace);
        if (data == null) {
            addEnchantmentLore(item);
            return;
        }
        data.set(enchantment.getKey(), PersistentDataType.INTEGER, level);
        meta.getPersistentDataContainer().set(namespace, PersistentDataType.TAG_CONTAINER, data);
        if (!meta.hasEnchants())
            meta.setEnchantmentGlintOverride(true);
        item.setItemMeta(meta);
        addEnchantmentLore(item);
    }

    public static void addData(ItemStack item, UberEnchantment enchantment, int level) {
        addCustom(item, enchantment, level, uberEnchantment);
    }

    public static void addStoredData(ItemStack item, UberEnchantment enchantment, int level) {
        addCustom(item, enchantment, level, storedUberEnchantment);
    }

    private static int removeCustom(ItemStack item, UberEnchantment enchantment, NamespacedKey namespace) {
        int level = 0;
        if (hasCustom(item, namespace)) {
            removeEnchantmentLore(item);
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer data = getCustom(item, namespace);
            if (data == null) {
                addEnchantmentLore(item);
                return 0;
            }
            if (data.has(enchantment.getKey())) {
                level = data.get(enchantment.getKey(), PersistentDataType.INTEGER);
                //item.getItemMeta().getPersistentDataContainer().get(namespace, PersistentDataType.TAG_CONTAINER).remove(enchantment.getKey());
                data.remove(enchantment.getKey());
                meta.getPersistentDataContainer().set(namespace, PersistentDataType.TAG_CONTAINER, data);
            }
            if (data.isEmpty())
                meta.getPersistentDataContainer().remove(namespace);
            item.setItemMeta(meta);
            if (!hasCustom(item, namespace)) {
                meta.setEnchantmentGlintOverride(null);
                item.setItemMeta(meta);
            }
            addEnchantmentLore(item);
        }
        return level;
    }

    public static int removeData(ItemStack item, UberEnchantment enchantment) {
        return removeCustom(item, enchantment, uberEnchantment);
    }

    public static int removeStoredData(ItemStack item, UberEnchantment enchantment) {
        return removeCustom(item, enchantment, storedUberEnchantment);
    }

    /**
     * Adds the specified UberEnchantment to the item with specified level. Also
     * adds the lore for displaying the enchantment on the item.
     *
     * @param enchant - The enchantment to add
     * @param item    - The item
     * @param level   - The level
     */
    public static void addEnchantment(UberEnchantment enchant, ItemStack item, int level) {
        /* Minecraft 1.20.2
        UberUtils.removeEnchantmentLore(item);
        item.addUnsafeEnchantment(enchant, level);
        UberUtils.addEnchantmentLore(item);
        */
        addData(item, enchant, level);
    }

    /**
     * Adds the map of Enchantments to the item with specified level. Also
     * adds the lore for displaying the enchantment on the item.
     *
     * @param enchants - The enchantments to add
     * @param item    - The item
     */
    public static void addEnchantments(Map<? extends Enchantment, Integer> enchants, ItemStack item) {
        /* Minecraft 1.20.2
        removeEnchantmentLore(item);
        EnchantmentUtils.setEnchantments(enchants, item);
        addEnchantmentLore(item);
        */
        EnchantmentUtils.setEnchantments(enchants, item);
    }

    /**
     * Adds the specified UberEnchantment to the Enchanted Book with specified level. Also
     * adds the lore for displaying the enchantment on the book.
     *
     * @param enchant - The enchantment to add
     * @param book    - The Enchanted Book
     * @param level   - The level
     */
    public static void addStoredEnchantment(UberEnchantment enchant, ItemStack book, int level) {
        if (!book.getType().equals(Material.ENCHANTED_BOOK))
            return;
        addStoredData(book, enchant, level);
        /* Minecraft 1.20.2
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        UberUtils.removeEnchantmentLore(book);
        meta.addStoredEnchant(enchant, level, true);
        book.setItemMeta(meta);
        UberUtils.addEnchantmentLore(book);
        */
    }

    /**
     * Adds the map of Enchantments to the book. Also
     * adds the lore for displaying the enchantment on the book.
     *
     * @param enchants - The enchantments to add
     * @param item    - The item
     */
    public static void addStoredEnchantments(Map<? extends Enchantment, Integer> enchants, ItemStack item) {
        if (!item.getType().equals(Material.ENCHANTED_BOOK))
            return;
        //EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();

        //UberUtils.removeEnchantmentLore(item);
        EnchantmentUtils.setStoredEnchantments(enchants, item);
        //UberUtils.addEnchantmentLore(item);
    }

    /**
     * Removes the specified UberEnchantment from the item, returning the level
     * of the enchantment or 0.
     *
     * @param enchantment - The enchantment to remove
     * @param item        - The item
     * @return The enchantment level or 0
     */
    public static int removeEnchantment(UberEnchantment enchantment, ItemStack item) {
        /* Minecraft 1.20.2
        if (item.hasItemMeta() && enchantment.containsEnchantment(item)) {
            UberUtils.removeEnchantmentLore(item);
            int level = item.removeEnchantment(enchantment);
            UberUtils.addEnchantmentLore(item);
            return level;
        }
        */
        return removeData(item, enchantment);
    }

    /**
     * Removes the specified UberEnchantment from the Enchanted Book
     *
     * @param enchantment - The enchantment to remove
     * @param book        - The book
     */
    public static int removeStoredEnchantment(UberEnchantment enchantment, ItemStack book) {
        if (!book.getType().equals(Material.ENCHANTED_BOOK))
            return 0;
        return removeStoredData(book, enchantment);
    }

    /**
     * Extracts the specified UberEnchantment from the item and puts it as a
     * stored enchantment on an enchanted book.
     *
     * @param enchantment - The enchantment to extract
     * @param item        - The item
     * @return An enchanted book itemstack containing the extracted enchantment
     */
    public static ItemStack extractEnchantment(UberEnchantment enchantment, ItemStack item) {
        if (item.hasItemMeta() && enchantment.containsEnchantment(item)) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
            /* Minecraft 1.20.2
            UberUtils.removeEnchantmentLore(item);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            meta.addStoredEnchant(enchantment, UberEnchantment.getLevel(item, enchantment), true);
            book.setItemMeta(meta);
            UberUtils.addEnchantmentLore(item);
            UberUtils.addEnchantmentLore(book);
            */
            addStoredEnchantment(enchantment, book, enchantment.getLevel(item));
            return book;
        } else {
            return null;
        }
    }

    /**
     * Adds custom enchantment lore to the specified item if it contains any
     * custom enchantments.<br>
     * (An example would be "Sharpness V", if sharpness was custom)
     *
     * @param item - The item
     */
    public static void addEnchantmentLore(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (meta.hasLore())
            lore = meta.getLore();
        Map<UberEnchantment, Integer> enchantments = item.getType() == Material.ENCHANTED_BOOK ? getStoredMap(item) : getMap(item);
        /* Minecraft 1.20.2
        if (item.getItemMeta() instanceof EnchantmentStorageMeta)
            enchantments = UberEnchantment.getStoredEnchantments(item);
        */
        List<String> effects = enchantments.entrySet().stream().map(data -> displayName(data.getKey(), data.getValue())).toList();
        lore.addAll(0, effects);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    /**
     * Removes any custom enchantment lore on the specified item
     *
     * @param item - The item
     */
    public static void removeEnchantmentLore(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasLore() && offset(item) > 0) {
            List<String> lore = meta.getLore();
            if (offset(item) > 0) {
                lore.subList(0, offset(item)).clear();
            }
            /*
             * List<String> effects =
             * UberEnchantment.getEnchantments(item).entrySet().stream().map(
             * data -> displayName(data.getKey(),
             * data.getValue())).collect(Collectors.toList());
             * effects.forEach(effect -> { if (lore.contains(effect))
             * lore.remove(effect); }); lore.removeAll(effects);
             */
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    /**
     * Constructs the custom enchantment lore.<br>
     * (Formatted as {@code "<name> <level>"} ie "Sharpness V")
     *
     * @param enchantment - The enchantment
     * @param level       - ENchantment level
     * @return A string with the formatted display name
     */
    public static String displayName(UberEnchantment enchantment, int level) {
        if (enchantment instanceof EffectEnchantment e)
            return ChatUtils.color(format(enchantment.getDisplayName(), level, level * 20));
        return ChatUtils.color(enchantment.getDisplayName() + " " + toRomanNumeral(level));
    }

    /**
     * Gets the offset (if any) required for custom enchantment lore.
     *
     * @param item - The item
     * @return Amount of offset or 0 in case of no enchantments
     */
    public static int offset(ItemStack item) {
        /* Minecraft 1.20.2
        if (item.getItemMeta() instanceof EnchantmentStorageMeta)
            return UberEnchantment.getStoredEnchantments(item).size();
        */
        return item.getType() == Material.ENCHANTED_BOOK ? getStoredMap(item).size() : getMap(item).size();
    }

    /**
     * Simple Integer to Roman numeral function.
     *
     * @param number - The number to convert
     * @return the converted number as a String or an empty String if number is less than 1
     */
    public static String toRomanNumeral(int number) {
        if (number < 1) {
            return "";
        }
        TreeMap<Integer, String> map = new TreeMap<>();
        map.put(1000000, "(M)");
        map.put(900000, "(C)(M)");
        map.put(500000, "(D)");
        map.put(400000, "(C)(D)");
        map.put(100000, "(C)");
        map.put(90000, "(X)(C)");
        map.put(50000, "(L)");
        map.put(40000, "(X)(L)");
        map.put(10000, "(X)");
        map.put(9000, "M(X)");
        map.put(5000, "(V)");
        map.put(4000, "M(V)");
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");
        int l = map.floorKey(number);
        if (number == l) {
            return map.get(number);
        }
        return map.get(l) + toRomanNumeral(number - l);
    }

    public static String format(String name, int amplifier, int ticks) {
        double seconds = ticks / 20.0;
        int minutes = (int) (seconds / 60);
        int hours = minutes / 60;
        int days = hours / 24;
        String s = Double.toString(seconds % 60);
        String a = (seconds % 60.0 < 10 ? "0" : "") + s.substring(0, Math.min(s.length(), 5));
        String roman = toRomanNumeral(amplifier);
        if (days > 0)
            return String.format("%1$s %2$s (%3$sd %4$sh %5$sm %6$ss)", name, roman, days, hours % 24, minutes % 60, a);
        if (hours > 0)
            return String.format("%1$s %2$s (%3$sh %4$sm %5$ss)", name, roman, hours % 24, minutes % 60, a);
        if (minutes > 0)
            return String.format("%1$s %2$s (%3$sm %4$ss)", name, roman, minutes % 60, a);
        return String.format("%1$s %2$s (%3$ss)", name, roman, a);
    }
}
