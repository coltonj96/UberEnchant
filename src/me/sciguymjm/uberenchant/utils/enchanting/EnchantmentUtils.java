package me.sciguymjm.uberenchant.utils.enchanting;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.UberConfiguration.UberRecord;
import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.utils.ChatUtils;
import me.sciguymjm.uberenchant.utils.UberLocale;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for enchantments
 */
public class EnchantmentUtils {

    /**
     * Utility method for internal use.
     *
     * @return String[]
     * @hidden
     */
    public static String[] listEnchants() {
        List<String> list = new ArrayList<>();
        list.add("&6Enchantments:");
        list.addAll(UberRecord.values().stream().filter(value -> value.getEnchant() != null).map(value -> String.format("        &6&l%1$s", value.getName())).toList());
        return list.toArray(String[]::new);
    }

    /**
     * Gets an enchantment by name from UberRecords.
     *
     * @param name - The enchantment name to get (can be a partial string like
     *             "sharp" gives "Sharpness")
     * @return The enchantment or null
     */
    public static Enchantment getEnchantment(String name) {
        if (name.isEmpty())
            return null;
        Pattern pattern = Pattern.compile(name.toLowerCase());
        return UberRecord.values().stream().filter(enchant -> pattern.matcher(enchant.getName().toLowerCase()).lookingAt() || enchant.getAliases().stream().anyMatch(alias -> pattern.matcher(alias.toLowerCase()).lookingAt())).findFirst().map(UberRecord::enchantment).orElse(null);
    }

    /**
     * Utility method for internal use.
     *
     * @param name String
     * @return String List
     * @hidden
     */
    public static List<String> matchEnchants(String name) {
        List<String> list = new ArrayList<>();
        if (name.isEmpty()) {
            UberRecord.values().forEach(value -> {
                if (!list.contains(value.getName().toLowerCase()))
                    list.add(value.getName().toLowerCase());
                if (!value.getAliases().isEmpty()) {
                    value.getAliases().forEach((alias) -> {
                        if (!list.contains(alias.toLowerCase()))
                            list.add(alias.toLowerCase());
                    });
                }
            });
            return list;
        }
        UberRecord.values().forEach(value -> {
            if (!value.getAliases().isEmpty()) {
                value.getAliases().forEach((alias) -> {
                    if (alias.toLowerCase().startsWith(name.toLowerCase()))
                        list.add(alias.toLowerCase());
                });
            }
        });
        return list;
    }

    /**
     * Utility method for internal use.<br>
     * Plugins should use
     * {@link UberUtils#addEnchantment(UberEnchantment, ItemStack, int)}
     *
     * @param enchant Enchantment
     * @param item    ItemStack
     * @param level   int
     * @hidden
     */
    public static <T extends Enchantment> void setEnchantment(T enchant, ItemStack item, int level) {
        if (enchant instanceof UberEnchantment enchantment)
            UberUtils.addEnchantment(enchantment, item, level);
        else
            item.addUnsafeEnchantment(enchant, level);
    }

    /**
     * Utility method for internal use.<br>
     * Plugins should use
     * {@link UberUtils#addStoredEnchantment(UberEnchantment, ItemStack, int)}
     *
     * @param enchant Enchantment
     * @param item    ItemStack
     * @param level   int
     * @hidden
     */
    public static <T extends Enchantment> void setStoredEnchantment(T enchant, ItemStack item, int level) {
        if (!item.getType().equals(Material.ENCHANTED_BOOK))
            return;
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        if (enchant instanceof UberEnchantment enchantment) {
            UberUtils.addStoredEnchantment(enchantment, item, level);
        } else {
            meta.addStoredEnchant(enchant, level, true);
            item.setItemMeta(meta);
        }
    }

    /**
     * Utility method for internal use.<br>
     *
     * @param enchants Enchantments
     * @param item    ItemStack
     * @hidden
     */
    public static void setEnchantments(Map<? extends Enchantment, Integer> enchants, ItemStack item) {
        enchants.forEach(item::addUnsafeEnchantment);
    }

    /**
     * Utility method for internal use.<br>
     *
     * @param enchants Enchantments
     * @param item    ItemStack
     * @hidden
     */
    public static void setStoredEnchantments(Map<? extends Enchantment, Integer> enchants, ItemStack item) {
        if (!item.getType().equals(Material.ENCHANTED_BOOK))
            return;
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        enchants.forEach((k, v) -> meta.addStoredEnchant(k, v, true));
        /*for (Map.Entry<? extends Enchantment, Integer> entry : enchants.entrySet()) {
            meta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
        }*/
        item.setItemMeta(meta);
    }

    /**
     * Utility method for internal use.<br>
     * Plugins should use
     * {@link UberUtils#extractEnchantment(UberEnchantment, ItemStack)}
     *
     * @param enchant Enchantment
     * @param item    ItemStack
     * @return ItemStack
     * @hidden
     */
    public static ItemStack extractEnchantment(Enchantment enchant, ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasEnchant(enchant)) {
            if (enchant instanceof UberEnchantment enchantment)
                return UberUtils.extractEnchantment(enchantment, item);
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
            UberUtils.removeEnchantmentLore(item);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            if (meta == null)
                return null;
            meta.addStoredEnchant(enchant, item.getEnchantmentLevel(enchant), true);
            book.setItemMeta(meta);
            UberUtils.addEnchantmentLore(item);
            return book;
        } else {
            return null;
        }
    }

    /**
     * Utility method for internal use.<br>
     * Plugins should use
     * {@link UberUtils#removeEnchantment(UberEnchantment, ItemStack)}
     *
     * @param enchant Enchantment
     * @param item    ItemStack
     * @return Boolean
     * @hidden
     */
    public static boolean removeEnchantment(Enchantment enchant, ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasEnchant(enchant)) {
            if (enchant instanceof UberEnchantment enchantment)
                UberUtils.removeEnchantment(enchantment, item);
            else
                item.removeEnchantment(enchant);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Utility method for internal use.
     *
     * @param player Player
     * @hidden
     */
    public static void help(Player player, String command) {
        Map<String, String> map = new HashMap<>();
        map.put("ulist", "\n    &6&l/ulist &7effects | enchants");
        map.put("uadd", "\n    &6&l/uadd &8enchant <enchantment | id> <level>" +
                "\n    &6&l/uadd &7enchant all <level>" +
                "\n    &6&l/uadd &8effect <effect | id> <duration> <level>" +
                "\n    &6&l/uadd &7name <string...>" +
                "\n    &6&l/uadd &8lore <string...>");
        map.put("ucost", "\n    &6&l/ucost &7<add | del | extract> enchant <enchantment | id> <level>");
        map.put("udel", "\n    &6&l/udel &8enchant <enchantment | id>" +
                "\n    &6&l/udel &7effect <effect | id>" +
                "\n    &6&l/udel &8lore <line#>" +
                "\n    &6&l/udel &7name");
        map.put("uextract", "\n    &6&l/uextract &8<enchantment | id>");
        map.put("uset", "\n    &6&l/uset &7effect <effect | id> <duration> <level>" +
                "\n    &6&l/uset &8hidden <true | false>" +
                "\n    &6&l/uset &7lore <line#> <string...>" +
                "\n    &6&l/uset &8name <string...>");
        map.put("uinsert", "\n    &6&l/uinsert &7lore <line#> <string...>");
        map.put("uclear", "\n    &6&l/uclear &8enchant | effect | lore");
        map.put("ureload", "\n    &6&l/ureload");

        if (!map.containsKey(command)) {
            List<String> list = new ArrayList<>(map.values());
            list.add(0, "&6Command Help:");
            ChatUtils.response(player, list.toArray(String[]::new));
        } else {
            ChatUtils.response(player, "&6Command Help:" + map.get(command));
        }
    }

    /**
     * Utility method for internal use.
     *
     * @param item ItemStack
     * @param hide Boolean
     * @return String
     * @hidden
     */
    public static String setHideEnchants(ItemStack item, boolean hide) {
        String msg;

        if (!item.hasItemMeta())
            return "";
        ItemMeta meta = item.getItemMeta();
        if (hide) {
            if (!meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
                if (UberEnchantment.hasEnchantments(item))
                    UberUtils.removeEnchantmentLore(item);
                msg = "&a" + UberLocale.get("utils.enchantments.hidden_success");
            } else {
                msg = "&c" + UberLocale.get("utils.enchantments.already_hidden");
            }
        } else {
            if (meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
                if (UberEnchantment.hasEnchantments(item))
                    UberUtils.addEnchantmentLore(item);
                msg = "&a" + UberLocale.get("utils.enchantments.shown_success");
            } else {
                msg = "&c" + UberLocale.get("utils.enchantments.already_shown");
            }
        }
        return msg;
    }

    /**
     * Utility method for internal use.
     *
     * @param player Player
     * @param name   String
     * @return List of Strings
     * @hidden
     */
    public static List<String> find(Player player, String name) {
        List<String> temp = new ArrayList<>();
        UberRecord.values().forEach((a) -> {
            if (name.isBlank() || a.getName().toLowerCase().contains(name.toLowerCase())) {
                if (player.hasPermission(String.format("uber.add.enchant.%1$s", a.getName().toLowerCase())))
                    temp.add(a.getName().toLowerCase());
            }
            a.getAliases().forEach(b-> {
                if (b.toLowerCase().contains(name.toLowerCase())) {
                    if (player.hasPermission(String.format("uber.add.enchant.%1$s", a.getName().toLowerCase())))
                        temp.add(b.toLowerCase());
                }
            });
        });
        return temp;
    }

    public static double getRarity(Enchantment enchantment) {
        switch (enchantment.getKey().getKey()) {
            case "protection",
                    "sharpness",
                    "efficiency",
                    "power",
                    "piercing" -> {
                return 10.0;
            }
            case "fire_protection",
                    "feather_falling",
                    "projectile_protection",
                    "smite",
                    "bane_of_arthropods",
                    "knockback",
                    "unbreaking",
                    "loyalty",
                    "quick_charge" -> {
                return 5.0;
            }
            case "blast_protection",
                    "respiration",
                    "aqua_affinity",
                    "depth_strider",
                    "frost_walker",
                    "fire_aspect",
                    "looting",
                    "sweeping",
                    "fortune",
                    "punch",
                    "flame",
                    "lure",
                    "impaling",
                    "riptide",
                    "multishot",
                    "mending" -> {
                return 2.0;
            }
            case "thorns",
                    "binding_curse",
                    "soul_speed",
                    "swift_sneak",
                    "silk_touch",
                    "infinity",
                    "channeling",
                    "vanishing_curse" -> {
                return 1.0;
            }
            default -> {
                if (enchantment instanceof UberEnchantment uber) {
                    return uber.getRarity().getWeight();
                } else {
                    return 0.0;
                }
            }
        }
    }
}
