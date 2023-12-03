package me.sciguymjm.uberenchant.utils.enchanting;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.api.utils.random.UberRandom;
import me.sciguymjm.uberenchant.api.utils.random.Weighted;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Utility class for internal use.
 */
public class EnchantmentTableUtils {

    public static Map<UUID, Long> seed;

    static {
        seed = new HashMap<>();
    }

    private static int enchantValue(ItemStack item) {
        switch (item.getType()) {
            case BOOK,
                    BOW,
                    CROSSBOW,
                    FISHING_ROD,
                    TRIDENT -> {
                return 1;
            }
            case CHAINMAIL_BOOTS,
                    CHAINMAIL_CHESTPLATE,
                    CHAINMAIL_HELMET,
                    CHAINMAIL_LEGGINGS -> {
                return 12;
            }
            case DIAMOND_AXE,
                    DIAMOND_BOOTS,
                    DIAMOND_CHESTPLATE,
                    DIAMOND_HELMET,
                    DIAMOND_HOE,
                    DIAMOND_LEGGINGS,
                    DIAMOND_PICKAXE,
                    DIAMOND_SHOVEL,
                    DIAMOND_SWORD -> {
                return 10;
            }
            case GOLDEN_BOOTS,
                    GOLDEN_CHESTPLATE,
                    GOLDEN_HELMET,
                    GOLDEN_LEGGINGS -> {
                return 25;
            }
            case GOLDEN_AXE,
                    GOLDEN_HOE,
                    GOLDEN_PICKAXE,
                    GOLDEN_SHOVEL,
                    GOLDEN_SWORD -> {
                return 22;
            }

            case IRON_BOOTS,
                    IRON_CHESTPLATE,
                    IRON_HELMET,
                    IRON_LEGGINGS,
                    TURTLE_HELMET -> {
                return 9;
            }
            case IRON_AXE,
                    IRON_HOE,
                    IRON_PICKAXE,
                    IRON_SHOVEL,
                    IRON_SWORD -> {
                return 14;
            }
            case STONE_AXE,
                    STONE_HOE,
                    STONE_PICKAXE,
                    STONE_SHOVEL,
                    STONE_SWORD -> {
                return 5;
            }
            case LEATHER_BOOTS,
                    LEATHER_CHESTPLATE,
                    LEATHER_HELMET,
                    LEATHER_LEGGINGS,
                    NETHERITE_AXE,
                    NETHERITE_BOOTS,
                    NETHERITE_CHESTPLATE,
                    NETHERITE_HELMET,
                    NETHERITE_HOE,
                    NETHERITE_LEGGINGS,
                    NETHERITE_PICKAXE,
                    NETHERITE_SHOVEL,
                    NETHERITE_SWORD,
                    WOODEN_AXE,
                    WOODEN_HOE,
                    WOODEN_PICKAXE,
                    WOODEN_SHOVEL,
                    WOODEN_SWORD -> {
                return 15;
            }
            default -> {
                return 0;
            }
        }
    }

    public static int getCost(UberRandom random, int slot, int bookShelves, ItemStack item) {
        int value = enchantValue(item);
        if (value <= 0) {
            return 0;
        } else {
            if (bookShelves > 15) {
                bookShelves = 15;
            }

            int l = random.nextInt(8) + 1 + (bookShelves >> 1) + random.nextInt(bookShelves + 1);

            return slot == 0 ? Math.max(l / 3, 1) : (slot == 1 ? l * 2 / 3 + 1 : Math.max(l, bookShelves * 2));
        }
    }

    public static CustomList getEnchantmentList(Player player, ItemStack item, int slot, int cost) {
        UberRandom random = new UberRandom(seed.get(player.getUniqueId()) + slot);

        CustomList list = selectEnchantment(random, item, cost, false);

        if (item.getType().equals(Material.BOOK) && list.vanilla.size() > 1) {
            list.vanilla.remove(random.nextInt(list.vanilla.size()));
        }

        if (item.getType().equals(Material.BOOK) && list.custom.size() > 1) {
            list.custom.remove(random.nextInt(list.custom.size()));
        }

        return list;
    }

    private static int clamp(int i, int j) {
        return Math.min(Math.max(i, j), Integer.MAX_VALUE);
    }

    public static CustomList selectEnchantment(UberRandom random, ItemStack item, int cost, boolean flag) {
        CustomList list = new CustomList(new ArrayList<>(), new ArrayList<>());

        int value = enchantValue(item);

        if (value > 0) {
            cost += 1 + random.nextInt(value / 4 + 1) + random.nextInt(value / 4 + 1);
            float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;

            cost = clamp(Math.round((float) cost + (float) cost * f), 1);

            CustomList available = getAvailable(cost, item, flag);

            Optional<WeightedEnchantment> vInst;
            if (!available.vanilla.isEmpty()) {
                vInst = getRandomItem(random, available.vanilla);
                Objects.requireNonNull(list.vanilla);
                vInst.ifPresent(list.vanilla::add);
            }

            Optional<WeightedEnchantment> cInst;
            if (!available.vanilla.isEmpty()) {
                cInst = getRandomItem(random, available.custom);
                Objects.requireNonNull(list.custom);
                cInst.ifPresent(list.custom::add);
            }

            while (random.nextInt(50) <= cost) {
                if (!list.vanilla.isEmpty()) {
                    filter(available, list.vanilla.get(list.vanilla.size() - 1));
                }

                if (!list.custom.isEmpty()) {
                    filter(available, list.custom.get(list.custom.size() - 1));
                }

                if (!available.vanilla.isEmpty()) {
                    vInst = getRandomItem(random, available.vanilla);
                    Objects.requireNonNull(list.vanilla);
                    vInst.ifPresent(list.vanilla::add);
                }

                if (!available.custom.isEmpty()) {
                    cInst = getRandomItem(random, available.custom);
                    Objects.requireNonNull(list.custom);
                    cInst.ifPresent(list.custom::add);
                }

                if (available.vanilla.isEmpty() && available.custom.isEmpty())
                    break;

                cost /= 2;
            }
        }
        return list;
    }

    public static void filter(CustomList list, WeightedEnchantment instance) {
        list.vanilla.removeIf(weightedEnchantment -> instance.enchantment.conflictsWith((weightedEnchantment).enchantment));
        list.custom.removeIf(weightedEnchantment -> instance.enchantment.conflictsWith((weightedEnchantment).enchantment));
    }

    public static CustomList getAvailable(int cost, ItemStack item, boolean flag) {
        Material type = item.getType();
        //List<WeightedEnchantment> list = new ArrayList<>();
        boolean isBook = type == Material.BOOK;
        List<UberConfiguration.UberRecord> enchantments = UberConfiguration.getRecords();

        CustomList list = new CustomList(new ArrayList<>(), new ArrayList<>());

        //enchantments.removeIf(r -> !custom && r.enchantment() instanceof UberEnchantment);

        for (UberConfiguration.UberRecord record : enchantments) {
            Enchantment enchantment = record.getEnchant();
            if (!EnchantmentTableEvents.isEnabled(enchantment))
                continue;
            if ((!enchantment.isTreasure() || flag) && (enchantment.canEnchantItem(item) || isBook)) {
                for (int j = record.getMaxLevel(); j > record.getMinLevel() - 1; --j) {
                    if (cost >= minCost(j, enchantment) && cost <= maxCost(j, enchantment)) {
                        if (!(enchantment instanceof UberEnchantment))
                            list.vanilla.add(new WeightedEnchantment(enchantment, j));
                        list.custom.add(new WeightedEnchantment(enchantment, j));
                        break;
                    }
                }
            }
        }
        return list;
    }

    public static class WeightedEnchantment implements Weighted<WeightedEnchantment> {

        private final WeightedEnchantment instance;
        private final Enchantment enchantment;
        private final int level;

        public WeightedEnchantment(Enchantment enchantment, int level) {
            this.enchantment = enchantment;
            this.level = level;
            instance = this;
        }

        public Enchantment getEnchantment() {
            return enchantment;
        }

        @Override
        public WeightedEnchantment value() {
            return instance;
        }

        @Override
        public double weight() {
            return rarity();
        }

        public int getLevel() {
            return level;
        }

        private double rarity() {
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

    private static int minCost(int i, Enchantment e) {
        if (e == null)
            return 1 + i * 10;
        int n = 0;
        switch (e.getKey().getKey()) {
            case "protection", "sharpness" -> n = 1 + (i - 1) * 11;
            case "fire_protection" -> n = 10 + (i - 1) * 8;
            case "feather_falling" -> n = 5 + (i - 1) * 6;
            case "blast_protection", "smite", "bane_of_arthropods", "unbreaking" -> n = 5 + (i - 1) * 8;
            case "projectile_protection" -> n = 3 + (i - 1) * 6;
            case "respiration" -> n = 10 * i;
            case "aqua_affinity" -> n = 1;
            case "thorns", "fire_aspect" -> n = 10 + 20 * (i - 1);
            case "depth_strider", "frost_walker", "soul_speed" -> n = i * 10;
            case "binding_curse", "channeling", "vanishing_curse" -> n = 25;
            case "knockback" -> n = 5 + 20 * (i - 1);
            case "looting", "fortune", "luck_of_the_sea", "lure" -> n = 15 + (i - 1) * 9;
            case "sweeping" -> n = 5 + (i - 1) * 9;
            case "efficiency" -> n = 1 + 10 * (i - 1);
            case "silk_touch" -> n = 15;
            case "power", "piercing" -> n = 1 + (i - 1) * 10;
            case "punch", "quick_charge" -> n = 12 + (i - 1) * 20;
            case "flame", "infinity", "multishot" -> n = 20;
            case "loyalty" -> n = 5 + i * 7;
            case "impaling" -> n = 1 + (i - 1) * 8;
            case "riptide" -> n = 10 + i * 7;
            case "mending", "swift_sneak" -> n = i * 25;
            default -> n = 1 + i * 10;
        }
        return n;
    }

    private static int maxCost(int i, Enchantment e) {
        if (e == null)
            return minCost(i, null) + 5;
        int n = minCost(i, e);
        switch (e.getKey().getKey()) {
            case "protection" -> n += 11;
            case "fire_protection", "blast_protection" -> n += 8;
            case "feather_falling", "projectile_protection" -> n += 6;
            case "respiration" -> n += 30;
            case "aqua_affinity" -> n += 40;
            case "thorns",
                    "binding_curse",
                    "knockback",
                    "fire_aspect",
                    "looting",
                    "efficiency",
                    "silk_touch",
                    "unbreaking",
                    "fortune",
                    "flame",
                    "infinity",
                    "luck_of_the_sea",
                    "lure",
                    "loyalty",
                    "riptide",
                    "channeling",
                    "multishot",
                    "quick_charge",
                    "piercing",
                    "mending",
                    "vanishing_curse",
                    "swift_sneak" -> n += 50;
            case "depth_strider",
                    "frost_walker",
                    "sweeping",
                    "power",
                    "soul_speed" -> n += 15;
            case "sharpness", "smite", "bane_of_arthropods", "impaling" -> n += 20;
            case "punch" -> n += 25;
            default -> n += minCost(i, e) + 5;
        }
        return n;
    }

    public static Optional<WeightedEnchantment> getRandomItem(UberRandom random, List<WeightedEnchantment> list, int i) {
        if (i <= 0) {
            return Optional.empty();
        } else {
            int j = random.nextInt(i);
            return getWeightedItem(list, j);
        }
    }

    public static Optional<WeightedEnchantment> getWeightedItem(List<WeightedEnchantment> list, int i) {
        Iterator<WeightedEnchantment> iterator = list.iterator();

        WeightedEnchantment entry;
        do {
            if (!iterator.hasNext()) {
                return Optional.empty();
            }

            entry = iterator.next();
            i -= entry.weight();
        } while (i >= 0);

        return Optional.of(entry);
    }

    public static int getTotalWeight(List<WeightedEnchantment> list) {
        long i = 0L;

        WeightedEnchantment entry;

        for (Iterator<WeightedEnchantment> iterator = list.iterator(); iterator.hasNext(); i += (long) entry.weight()) {
            entry = iterator.next();
        }

        return (int) i;
    }

    public static Optional<WeightedEnchantment> getRandomItem(UberRandom random, List<WeightedEnchantment> list) {
        return getRandomItem(random, list, getTotalWeight(list));
    }

    public record CustomList(List<WeightedEnchantment> vanilla, List<WeightedEnchantment> custom) {
    }
}
