package me.sciguymjm.uberenchant.api.utils;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.utils.ChatUtils;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Custom enchantment related utility class
 */
public class UberUtils {

    /**
     * Adds the specified UberEnchantment to the item with specified level. Also
     * adds the lore for displaying the enchantment on the item.
     *
     * @param enchant - The enchantment to add
     * @param item    - The item
     * @param level   - The level
     */
    public static void addEnchantment(UberEnchantment enchant, ItemStack item, int level) {
        UberUtils.removeEnchantmentLore(item);
        item.addUnsafeEnchantment(enchant, level);
        UberUtils.addEnchantmentLore(item);
    }

    /**
     * Adds the map of Enchantments to the item with specified level. Also
     * adds the lore for displaying the enchantment on the item.
     *
     * @param enchants - The enchantments to add
     * @param item    - The item
     */
    public static void addEnchantments(Map<? extends Enchantment, Integer> enchants, ItemStack item) {
        UberUtils.removeEnchantmentLore(item);
        EnchantmentUtils.setEnchantments(enchants, item);
        UberUtils.addEnchantmentLore(item);
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
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        UberUtils.removeEnchantmentLore(book);
        meta.addStoredEnchant(enchant, level, true);
        book.setItemMeta(meta);
        UberUtils.addEnchantmentLore(book);
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
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();

        UberUtils.removeEnchantmentLore(item);
        EnchantmentUtils.setStoredEnchantments(enchants, item);
        UberUtils.addEnchantmentLore(item);
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
        if (item.hasItemMeta() && enchantment.containsEnchantment(item)) {
            UberUtils.removeEnchantmentLore(item);
            int level = item.removeEnchantment(enchantment);
            UberUtils.addEnchantmentLore(item);
            return level;
        }
        return 0;
    }

    /**
     * Removes the specified UberEnchantment from the Enchanted Book
     *
     * @param enchantment - The enchantment to remove
     * @param book        - The book
     */
    public static void removeStoredEnchantment(UberEnchantment enchantment, ItemStack book) {
        if (!book.getType().equals(Material.ENCHANTED_BOOK))
            return;
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();

        if (book.hasItemMeta() && meta.hasEnchant(enchantment)) {
            UberUtils.removeEnchantmentLore(book);
            meta.removeStoredEnchant(enchantment);
            book.setItemMeta(meta);
            UberUtils.addEnchantmentLore(book);
        }
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
            UberUtils.removeEnchantmentLore(item);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            meta.addStoredEnchant(enchantment, UberEnchantment.getLevel(item, enchantment), true);
            book.setItemMeta(meta);
            UberUtils.addEnchantmentLore(item);
            UberUtils.addEnchantmentLore(book);
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
        Map<UberEnchantment, Integer> enchantments = UberEnchantment.getEnchantments(item);
        if (item.getItemMeta() instanceof EnchantmentStorageMeta)
            enchantments = UberEnchantment.getStoredEnchantments(item);
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
        return ChatUtils.color(enchantment.getDisplayName() + " " + toRomanNumeral(level));
    }

    /**
     * Gets the offset (if any) required for custom enchantment lore.
     *
     * @param item - The item
     * @return Amount of offset or 0 in case of no enchantments
     */
    public static int offset(ItemStack item) {
        if (item.getItemMeta() instanceof EnchantmentStorageMeta)
            return UberEnchantment.getStoredEnchantments(item).size();
        return UberEnchantment.getEnchantments(item).size();
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

    /*
     * public static String format(String name, int amplifier, int ticks) {
     * double seconds = ticks / 20.0; int minutes = (int) (seconds / 60); int
     * hours = minutes / 60; int days = hours / 24; String s =
     * Double.toString(seconds % 60); String a = (seconds % 60.0 < 10 ? "0" :
     * "") + s.substring(0, s.length() < 5 ? s.length() : 5); if (days > 0)
     * return String.format("%1$s %2$s (%3$sd %4$sh %5$sm %6$ss)", name,
     * amplifier, days, hours % 24, minutes % 60, a); if (hours > 0) return
     * String.format("%1$s %2$s (%3$sh %4$sm %5$ss)", name, amplifier, hours %
     * 24, minutes % 60, a); if (minutes > 0) return
     * String.format("%1$s %2$s (%3$sm %4$ss)", name, amplifier, minutes % 60,
     * a); return String.format("%1$s %2$s (%3$ss)", name,
     * NumberUtils.toRomanNumeral(amplifier), a); }
     */
}
