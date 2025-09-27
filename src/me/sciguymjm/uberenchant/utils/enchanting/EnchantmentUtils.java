package me.sciguymjm.uberenchant.utils.enchanting;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.ExcellentEnchantsRecord;
import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.api.utils.UberRecord;
import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.utils.ChatUtils;
import me.sciguymjm.uberenchant.utils.UberLocale;
import me.sciguymjm.uberenchant.utils.VersionUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Predicate;
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
    public static String[] listEnchants(int page) {
        List<String> records = UberConfiguration.getRecords().stream().filter(value -> value.getEnchant() != null).map(value -> String.format("    &6&l%1$s&8 (&5%2$s&8)", value.getName(), value.getKey().getNamespace())).distinct().sorted().toList();
        int t = records.size();
        List<String> sub = new ArrayList<>(records.subList(Math.max(Math.min(page * 10 - 10, t - 10), 0), Math.min(page * 10, t)));
        sub.add(0, UberLocale.getCF("&6", "utils.enchantments.list", page, sub.size() / 10));
        sub.add(sub.size(), UberLocale.getCF("&6", "utils.enchantments.list", page));
        return sub.toArray(String[]::new);
    }

    public static String listPage(int page) {
        List<String> records = UberConfiguration.getRecords().stream().filter(value -> value.getEnchant() != null).map(value -> String.format("    &6&l%1$s&8 (&5%2$s&8)", value.getName(), value.getKey().getNamespace())).distinct().sorted().toList();
        int t = records.size();
        int pages = (int) Math.ceil(t / 10.0);
        page = clamp(1, pages, page);
        int from = from(page, 10, t);
        int to = to(page, 10, t);
        List<String> sub = new ArrayList<>(records.subList(from , to));
        sub.add(0, UberLocale.getCF("&6", "utils.enchantments.list", page, pages));
        return ChatUtils.color(String.join("\n", sub));
    }

    public static int getPages() {
        int t = (int) UberConfiguration.getRecords().stream().filter(value -> value.getEnchant() != null).distinct().count();
        return  (int) Math.ceil(t / 10.0);
    }

    private static int from(int value, int range, int total) {
        int a = value * range - range;
        int f = a + (Math.max(Math.min(a, total - range), 0) % range);
        return f - f % range;
    }

    private static int to(int value, int range, int total) {
        return Math.min(value * range, total);
    }

    private static int clamp(int min, int max, int value) {
        return Math.min(Math.max(min, value), max);
    }

    /**
     * Gets an enchantment by name from UberRecords.
     *
     * @param name The enchantment name to get (can be a partial string like
     *             "sharp" gives "Sharpness")
     * @return The enchantment or null
     */
    public static Enchantment getEnchantment(String name) {
        if (name.isEmpty())
            return null;
        Pattern pattern = Pattern.compile(name.toLowerCase());
        Predicate<String> filter = string -> pattern.matcher(string.toLowerCase()).lookingAt();
        return UberRecord.values().stream().filter(
                enchant -> filter.test(enchant.getName()) ||
                        filter.test(enchant.getKey().toString()) ||
                        enchant.getAliases().stream().anyMatch(filter))
                .findFirst().map(UberRecord::getEnchant).orElse(null);
    }

    /**
     * Gets a set of matching enchantments by name/alias from UberRecords
     *
     * @param name The partial name/alias of an enchantment
     * @return A set of all matching enchantments
     */
    public static Set<Enchantment> getMatches(String name) {
        return UberRecord.values().stream().filter(record ->
                record.getName().equalsIgnoreCase(name) ||
                        record.getName().contains(name.toLowerCase()) ||
                        record.getKey().toString().equalsIgnoreCase(name) ||
                        record.getKey().toString().contains(name.toLowerCase()) ||
                        record.getAliases().contains(name.toLowerCase()) ||
                        record.getAliases().stream().anyMatch(alias ->
                                alias.equalsIgnoreCase(name) ||
                                        alias.contains(name.toLowerCase()))
        ).distinct().map(UberRecord::getEnchant).collect(Collectors.toSet());
        /*if (name.isEmpty())
            return null;
        BiFunction<String, String, Boolean> lookingAt = (String a, String b) -> Pattern.compile(a.toLowerCase()).matcher(b.toLowerCase()).lookingAt();
        BiFunction<String, String, Boolean> matches = (String a, String b) -> Pattern.compile(a.toLowerCase()).matcher(b.toLowerCase()).matches();
        Predicate<UberRecord> filterPartial = enchant ->
                lookingAt.apply(name, enchant.getName()) ||
                enchant.getAliases().stream().anyMatch(n -> lookingAt.apply(name, n));
        Predicate<UberRecord> filterMatches = enchant ->
                matches.apply(name, enchant.getName()) ||
                        enchant.getAliases().stream().anyMatch(n -> matches.apply(name, n));
        Stream<UberRecord> stream = UberRecord.values().stream().filter(filterPartial);
        if (UberRecord.values().stream().anyMatch(filterMatches))
            return stream.filter(filterMatches).map(UberRecord::enchantment).collect(Collectors.toSet());
        return stream.map(UberRecord::enchantment).collect(Collectors.toSet());*/
    }

    /**
     * Gets a set of matching enchantments by name/alias from UberRecords
     *
     * @param name The partial name/alias of an enchantment
     * @return A set of all matching enchantments
     */
    public static Set<UberEnchantment> getMatches(ItemStack item, String name) {
        Set<UberEnchantment> enchants;
        if (!item.getType().equals(Material.AIR) && !UberUtils.getMap(item).isEmpty())
            enchants = new HashSet<>(UberUtils.getMap(item).keySet());
        else
            return null;
        if (name.isEmpty())
            return enchants;
        return enchants.stream().filter(enchant ->
                enchant.getName().equalsIgnoreCase(name) ||
                        enchant.getName().contains(name.toLowerCase()) ||
                        enchant.getKey().toString().equalsIgnoreCase(name) ||
                        enchant.getKey().toString().contains(name.toLowerCase()) ||
                        enchant.getAliases().contains(name.toLowerCase()) ||
                        enchant.getAliases().stream().anyMatch(alias ->
                                alias.equalsIgnoreCase(name) ||
                                        alias.contains(name.toLowerCase()))
        ).collect(Collectors.toSet());
        /*return UberRecord.values().stream().filter(record ->
                record.getEnchant() instanceof UberEnchantment &&
                record.getName().equalsIgnoreCase(name) ||
                        record.getName().contains(name.toLowerCase()) ||
                        record.getKey().toString().equalsIgnoreCase(name) ||
                        record.getKey().toString().contains(name.toLowerCase()) ||
                        record.getAliases().contains(name.toLowerCase()) ||
                        record.getAliases().stream().anyMatch(alias ->
                                alias.equalsIgnoreCase(name) ||
                                        alias.contains(name.toLowerCase()))
        ).distinct().map(record -> (UberEnchantment) record.enchantment()).collect(Collectors.toSet());
        /*BiFunction<String, String, Boolean> lookingAt = (String a, String b) -> Pattern.compile(a.toLowerCase()).matcher(b.toLowerCase()).lookingAt();
        BiFunction<String, String, Boolean> matches = (String a, String b) -> Pattern.compile(a.toLowerCase()).matcher(b.toLowerCase()).matches();
        Predicate<UberRecord> filterPartial = enchant ->
                lookingAt.apply(name, enchant.getName()) ||
                        enchant.getAliases().stream().anyMatch(n -> lookingAt.apply(name, n));
        Predicate<UberRecord> filterMatches = enchant ->
                matches.apply(name, enchant.getName()) ||
                        enchant.getAliases().stream().anyMatch(n -> matches.apply(name, n));
        Set<UberRecord> records = UberRecord.values().stream().filter(record -> record.enchantment() instanceof UberEnchantment && enchants.contains(record.enchantment())).collect(Collectors.toSet());
        Stream<UberRecord> stream = UberRecord.values().stream().filter(filterPartial);
        Set<UberEnchantment> elements;
        if (UberRecord.values().stream().anyMatch(filterMatches))
            elements = stream.filter(filterMatches).map(record -> (UberEnchantment) record.enchantment()).collect(Collectors.toSet());
        else
            elements = stream.map(record -> (UberEnchantment) record.enchantment()).collect(Collectors.toSet());
        return elements;*/
    }

    /**
     * Utility method for internal use.
     *
     * @param player Player
     * @param set  Set
     * @return Boolean
     */
    public static boolean multi(Player player, Set<? extends Enchantment> set) {
        if (set == null || set.isEmpty()) {
            ChatUtils.localized(player, "&c", "actions.enchant.invalid");
            ChatUtils.response(player, "&a/ulist enchants");
            return true;
        }
        if (set.size() > 1) {
            List<String> list = new ArrayList<>(set.stream().map(e -> {
                NamespacedKey key1 = VersionUtils.getKey(e);
                if (key1 == null)
                    return "";
                if (set.stream().filter(e2 -> {
                    NamespacedKey key2 = VersionUtils.getKey(e2);
                    if (key2 == null)
                        return false;
                    return key2.getKey().equals(key1.getKey());
                }).count() > 1)
                    return "    - " + key1;
                return "    - " + key1.getKey();

            }).toList());
            list.add(0, "\n" + UberLocale.get("utils.enchantments.multiple"));
            ChatUtils.response(player, list.toArray(String[]::new));
            return true;
        }
        return false;
    }

    public static boolean multi(Player player, List<Player> players) {
        if (players.size() > 1) {
            List<String> list = new ArrayList<>(players.stream().map(p -> "    - " + p.getName()).toList());
            list.add(0, "\n" + UberLocale.get("utils.players.multiple"));
            ChatUtils.response(player, list.toArray(String[]::new));
            return true;
        }
        return false;
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
        UberRecord.values().forEach(record -> {
            list.add(record.getName());
            list.add(record.getKey().toString());
            list.addAll(record.getAliases());
        });
        return list.stream().filter(a -> name.isBlank() || a.toLowerCase().contains(name.toLowerCase())).distinct().toList();
        /*if (name.isEmpty()) {
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
        });*/
        //return list;
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
            UberUtils.addData(item, enchantment, level);//UberUtils.addEnchantment(enchantment, item, level);
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
        if (enchant instanceof UberEnchantment enchantment) {
            UberUtils.addStoredEnchantment(enchantment, item, level);
        } else {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
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
        enchants.forEach((enchant, level) -> setEnchantment(enchant, item, level));
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
        //EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        enchants.forEach((k, v) -> setStoredEnchantment(k, item, v));
        /*for (Map.Entry<? extends Enchantment, Integer> entry : enchants.entrySet()) {
            meta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
        }*/
        //item.setItemMeta(meta);
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
        if (item.hasItemMeta() && UberUtils.getAllMap(item).containsKey(enchant)) {
            if (enchant instanceof UberEnchantment enchantment)
                return UberUtils.extractEnchantment(enchantment, item);
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            if (meta == null)
                return null;
            meta.addStoredEnchant(enchant, item.getEnchantmentLevel(enchant), true);
            book.setItemMeta(meta);
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
        if (item.hasItemMeta() && UberUtils.getAllMap(item).containsKey(enchant)) {
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
        Map<String, List<String>> map = new HashMap<>();
        map.put("ulist", list("    &6&l/ulist &7effects | enchants"));
        map.put("uadd", list(
                "    &6&l/uadd &7enchant <enchantment> <level>",
                "    &6&l/uadd &7enchant all <level>",
                "    &6&l/uadd &7effect <effect> <duration> <level>",
                "    &6&l/uadd &7meta <enchantment> <tag> <value>",
                "    &6&l/uadd &7name <string...>",
                "    &6&l/uadd &7lore <string...>"
        ));
        map.put("ucost", list("    &6&l/ucost &7<add | del | extract> enchant <enchantment> <level>"));
        map.put("udel", list(
                "    &6&l/udel &7enchant <enchantment>",
                "    &6&l/udel &7effect <effect>",
                "    &6&l/udel &7lore <line#>",
                "    &6&l/udel &7meta <enchantment> <tag>",
                "    &6&l/udel &7name"
        ));
        map.put("uextract", list("    &6&l/uextract &7<enchantment | id>"));
        map.put("uset", list(
                "    &6&l/uset &7effect <effect> <duration> <level>",
                "    &6&l/uset &7hidden <true | false>",
                "    &6&l/uset &7lore <line#> <string...>",
                "    &6&l/uset &7meta <enchantment> <tag> <value>",
                "    &6&l/uset &7name <string...>"
        ));
        map.put("uinsert", list("    &6&l/uinsert &7lore <line#> <string...>"));
        map.put("uclear", list("    &6&l/uclear &7enchant | effect | lore"));
        map.put("ureload", list("    &6&l/ureload"));

        if (!map.containsKey(command)) {
            List<String> list = new ArrayList<>();
            list.add("&6Command Help:");
            list.addAll(map.values().stream().map(l -> String.join("\n", l)).toList());
            ChatUtils.response(player, list.toArray(String[]::new));
        } else {
            ChatUtils.response(player, "&6Command Help:\n" + String.join("\n", map.get(command)));
        }
    }

    private static List<String> list(String... strings) {
        if (VersionUtils.isAtLeast("1.20.4"))
            return new ArrayList<>(List.of(strings));
        return new ArrayList<>(Arrays.stream(strings).filter(string -> !string.contains("meta")).toList());
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
                msg = UberLocale.getC("&a", "utils.enchantments.hidden_success");
            } else {
                msg = UberLocale.getC("&c", "utils.enchantments.already_hidden");
            }
            if (UberEnchantment.hasEnchantments(item))
                UberUtils.removeEnchantmentLore(item);
        } else {
            if (meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
                meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
                msg = UberLocale.getC("&a", "utils.enchantments.shown_success");
            } else {
                msg = UberLocale.getC("&c", "utils.enchantments.already_shown");
            }
            if (UberEnchantment.hasEnchantments(item))
                UberUtils.addEnchantmentLore(item);
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
        if (player.hasPermission("uber.add.enchant.all"))
            return matchEnchants(name);
        return UberRecord.values().stream().filter(a -> {
            if (name.isBlank() || a.getKey().toString().toLowerCase().contains(name.toLowerCase()))
                return player.hasPermission(String.format("uber.add.enchant.%1$s", a.getName().toLowerCase()));
            return false;
        }).<String>mapMulti((a, b) -> {
            NamespacedKey key = VersionUtils.getKey(a.getEnchant());
            if (key ==  null)
                return;
            b.accept(key.toString());
            a.getAliases().forEach(b);
        }).distinct().toList();
    }

    public static List<String> find(Player player, ItemStack item, String name) {
        Set<Enchantment> all = UberUtils.getAllMap(item).keySet();
        return UberRecord.values().stream().filter(record -> {
            if (all.contains(record.getEnchant()) && record.getKey().toString().toLowerCase().contains(name.toLowerCase()))
                return player.hasPermission(String.format("uber.del.enchant.%1$s", record.getName().toLowerCase()));
            return false;
        }).<String>mapMulti((a, b) -> {
            NamespacedKey key = VersionUtils.getKey(a.getEnchant());
            if (key ==  null)
                return;
            b.accept(key.toString());
            a.getAliases().forEach(b);
        }).distinct().toList();
    }

    /**
     * Utility method for internal use.
     *
     * @param enchantment Enchantment
     * @return Double
     */
    public static double getRarity(Enchantment enchantment) {
        NamespacedKey key = VersionUtils.getKey(enchantment);
        if (key == null)
            return 0.0;
        switch (key.getKey()) {
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
                 "quick_charge",
                 "density" -> {
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
                 "mending",
                 "breach",
                 "wind_burst" -> {
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
                UberRecord record = UberRecord.byEnchantment(enchantment);
                if (record == null)
                    return 0.0;
                if (enchantment instanceof UberEnchantment uber)
                    return uber.getRarity().getWeight();
                if (record instanceof ExcellentEnchantsRecord ex)
                    return ex.getWeight();
                return 0.0;
            }
        }
    }

    public static boolean canEnchant(Enchantment e, ItemStack i) {
        if (UberRecord.byEnchantment(e) instanceof ExcellentEnchantsRecord record)
            return record.getTargets().contains(VersionUtils.getKey(i.getType()).getKey());
        if (e instanceof UberEnchantment ue)
            return ue.canEnchantItem(i);
        return e.canEnchantItem(i);
    }

    public static boolean isTreasure(Enchantment e) {
        if (UberRecord.byEnchantment(e) instanceof ExcellentEnchantsRecord record)
            return record.isTreasure();
        return e.isTreasure();
    }
}
