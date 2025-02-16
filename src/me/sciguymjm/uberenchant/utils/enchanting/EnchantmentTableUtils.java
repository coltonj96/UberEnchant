package me.sciguymjm.uberenchant.utils.enchanting;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.api.utils.random.UberRandom;
import me.sciguymjm.uberenchant.api.utils.random.Weighted;
import me.sciguymjm.uberenchant.api.utils.random.WeightedChance;
import me.sciguymjm.uberenchant.api.utils.random.WeightedEntry;
import me.sciguymjm.uberenchant.utils.FileUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Utility class for internal use.
 */
public class EnchantmentTableUtils {

    public static Map<UUID, Long> seed;

    private static boolean floor_bonus;
    private static Map<Material, Double> bonus_map;
    private static WeightedChance<Integer> weight;

    static {
        seed = new HashMap<>();
        floor_bonus = FileUtils.get("/mechanics/enchantment_table.yml", "floor_bonus", false, Boolean.class);
        bonus_map = new HashMap<>();
        FileUtils.loadConfig("/mechanics/enchantment_table.yml").getStringList("bonus_blocks").forEach(e -> {
            String[] split = e.split(":");

            Material key = Registry.MATERIAL.get(NamespacedKey.minecraft(split[0]));
            double value = 0.0;
            try {
                 value = Double.parseDouble(split[1]);
            } catch (NumberFormatException err) {
                err.printStackTrace();
            }
            if (key != null && key.isBlock())
                bonus_map.put(key, value);
        });
        weight = new WeightedChance<>(
                new WeightedEntry<>(0, 0.12),
                new WeightedEntry<>(1, 0.22),
                new WeightedEntry<>(2, 0.32),
                new WeightedEntry<>(3, 0.22),
                new WeightedEntry<>(4, 0.12)
        );
    }

    /**
     * Gets whether the floor bonus is  enabled or not
     *
     * @return Whether it's enabled  or not
     */
    public static boolean floorBonus() {
        return floor_bonus;
    }

    /**
     * Gets a map  of the currently configured floor bonus materials and values
     *
     * @return A map
     */
    public static Map<Material, Double> bonusBlocks() {
        return bonus_map;
    }

    private static int enchantValue(ItemStack item) {
        return switch (item.getType().getKey().getKey().toLowerCase()) {
            case "book",
                 "bow",
                 "crossbow",
                 "fishing_rod",
                 "trident" -> 1;
            case "chainmail_boots",
                 "chainmail_chestplate",
                 "chainmail_helmet",
                 "chainmail_leggings" -> 12;
            case "diamond_axe",
                 "diamond_boots",
                 "diamond_chestplate",
                 "diamond_helmet",
                 "diamond_hoe",
                 "diamond_leggings",
                 "diamond_pickaxe",
                 "diamond_shovel",
                 "diamond_sword" -> 10;
            case "golden_boots",
                 "golden_chestplate",
                 "golden_helmet",
                 "golden_leggings" -> 25;
            case "golden_axe",
                 "golden_hoe",
                 "golden_pickaxe",
                 "golden_shovel",
                 "golden_sword" -> 22;
            case "iron_boots",
                 "iron_chestplate",
                 "iron_helmet",
                 "iron_leggings",
                 "turtle_helmet" -> 9;
            case "iron_axe",
                 "iron_hoe",
                 "iron_pickaxe",
                 "iron_shovel",
                 "iron_sword" -> 14;
            case "stone_axe",
                 "stone_hoe",
                 "stone_pickaxe",
                 "stone_shovel",
                 "stone_sword" -> 5;
            case "leather_boots",
                 "leather_chestplate",
                 "leather_helmet",
                 "leather_leggings",
                 "mace",
                 "netherite_axe",
                 "netherite_boots",
                 "netherite_helmet",
                 "netherite_hoe",
                 "netherite_leggings",
                 "netherite_pickaxe",
                 "netherite_shovel",
                 "netherite_sword",
                 "wooden_axe",
                 "wooden_hoe",
                 "wooden_pickaxe",
                 "wooden_shovel",
                 "wooden_sword" -> 15;
            default -> 0;
        };
    }

    /**
     * Utility method for internal use.
     *
     * @param random
     * @param slot
     * @param bonus
     * @param item
     * @return
     */
    public static int getCost(UberRandom random, int slot, int bonus, ItemStack item) {
        int value = enchantValue(item);
        if (value <= 0) {
            return 0;
        } else {
            int l = random.nextInt(8) + 1 + (bonus >> 1) + random.nextInt(bonus + 1);
            return slot == 0 ? Math.max(l / 3, 1) : (slot == 1 ? l * 2 / 3 + 1 : Math.max(l, bonus));
        }
    }

    /**
     * Utility method for internal use.
     *
     * @param player
     * @param item
     * @param slot
     * @param cost
     * @return
     */
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

    /**
     * Utility method for internal use.
     *
     * @param random
     * @param item
     * @param cost
     * @param flag
     * @return
     */
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

    /**
     * Utility method for internal use.
     *
     * @param list
     * @param instance
     */
    public static void filter(CustomList list, WeightedEnchantment instance) {
        list.vanilla.removeIf(weightedEnchantment -> instance.enchantment.conflictsWith(weightedEnchantment.enchantment));
        list.custom.removeIf(weightedEnchantment -> instance.enchantment.conflictsWith(weightedEnchantment.enchantment));
    }

    /**
     * Utility method for internal use.
     *
     * @param cost
     * @param item
     * @param flag
     * @return
     */
    public static CustomList getAvailable(int cost, ItemStack item, boolean flag) {
        Material type = item.getType();
        //List<WeightedEnchantment> list = new ArrayList<>();
        boolean isBook = type == Material.BOOK;
        List<UberConfiguration.UberRecord> enchantments = UberConfiguration.getRecords();

        CustomList list = new CustomList(new ArrayList<>(), new ArrayList<>());

        //enchantments.removeIf(r -> !custom && r.enchantment() instanceof UberEnchantment);

        /*UberConfiguration.getRecords().stream()
                .filter(record -> EnchantmentTableEvents.isDisabled(record.getEnchant()))
                .filter(record -> {
                    Enchantment enchantment = record.getEnchant();
                    return (!enchantment.isTreasure() || flag) && (enchantment.canEnchantItem(item) || isBook);
                })
                .forEach(record -> {
            Enchantment enchantment = record.getEnchant();
            //if (!EnchantmentTableEvents.isDisabled(enchantment)) {
                //if ((!enchantment.isTreasure() || flag) && (enchantment.canEnchantItem(item) || isBook)) {
                    for (int j = record.getMaxLevel(); j >= record.getMinLevel(); --j) {
                        if (cost >= minCost(j, enchantment) && cost <= maxCost(j, enchantment)) {
                            if (!(enchantment instanceof UberEnchantment))
                                list.vanilla.add(new WeightedEnchantment(enchantment, j));
                            list.custom.add(new WeightedEnchantment(enchantment, j));
                            break;
                        }
                    }
                //}
           //}
        });*/

        for (UberConfiguration.UberRecord record : enchantments) {
            Enchantment enchantment = record.getEnchant();
            if (EnchantmentTableEvents.isDisabled(enchantment))
                continue;
            if ((!enchantment.isTreasure() || flag) && (enchantment.canEnchantItem(item) || isBook)) {
                for (int j = record.getMaxLevel(); j >= record.getMinLevel(); --j) {
                    if (cost >= minCost(j, enchantment) && cost <= maxCost(j, enchantment)) {
                        if (enchantment instanceof UberEnchantment)
                            list.custom.add(new WeightedEnchantment(enchantment, Math.max(j - weight.select(), 1)));
                        else
                            list.vanilla.add(new WeightedEnchantment(enchantment, j));
                        break;
                    }
                }
            }
        }
        return list;
    }

    /**
     * Utility class for internal use.
     */
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
            return switch (enchantment.getKey().getKey().toLowerCase()) {
                case "protection",
                     "sharpness",
                     "efficiency",
                     "power",
                     "piercing" -> 10.0;
                case "fire_protection",
                     "feather_falling",
                     "projectile_protection",
                     "smite",
                     "bane_of_arthropods",
                     "knockback",
                     "unbreaking",
                     "loyalty",
                     "quick_charge",
                     "density" -> 5.0;
                case "blast_protection",
                     "respiration",
                     "aqua_affinity",
                     "depth_strider",
                     "frost_walker",
                     "fire_aspect",
                     "looting",
                     "sweeping", "sweeping_edge",
                     "fortune",
                     "punch",
                     "flame",
                     "lure",
                     "impaling",
                     "riptide",
                     "multishot",
                     "mending",
                     "breach",
                     "wind_burst" -> 2.0;
                case "thorns",
                     "binding_curse",
                     "soul_speed",
                     "swift_sneak",
                     "silk_touch",
                     "infinity",
                     "channeling",
                     "vanishing_curse" -> 1.0;
                default -> (enchantment instanceof UberEnchantment uber) ? uber.getRarity().getWeight() : 0.0;
            };
        }
    }

    private interface Cost {
        int calc(int a, int b);
    }

    private static int minCost(int i, Enchantment e) {
        Cost cost = (int base, int per) -> base + per * (i - 1);
        if (e == null)
            return 1 + 10 * (i - 1);
        return switch (e.getKey().getKey().toLowerCase()) {
            case "protection",
                 "sharpness" -> cost.calc(1, 11);
            case "fire_protection" -> cost.calc(10, 8);
            case "feather_falling" -> cost.calc(5, 6);
            case "blast_protection",
                 "smite",
                 "bane_of_arthropods",
                 "unbreaking" -> cost.calc(5, 8);
            case "projectile_protection" -> cost.calc(3, 6);
            case "respiration",
                 "depth_strider",
                 "frost_walker",
                 "soul_speed" -> cost.calc(10, 10);
            case "aqua_affinity" -> cost.calc(1, 0);
            case "thorns",
                 "fire_aspect" -> cost.calc(10, 20);
            case "binding_curse",
                 "channeling",
                 "vanishing_curse" -> cost.calc(25, 0);
            case "knockback" -> cost.calc(5, 20);
            case "looting",
                 "fortune",
                 "luck_of_the_sea",
                 "lure",
                 "breach",
                 "wind_burst" -> cost.calc(15, 9);
            case "sweeping", "sweeping_edge" -> cost.calc(5, 9);
            case "efficiency",
                 "power",
                 "piercing" -> cost.calc(1, 10);
            case "silk_touch" -> cost.calc(15, 0);
            case "punch",
                 "quick_charge" -> cost.calc(12, 20);
            case "flame",
                 "infinity",
                 "multishot" -> cost.calc(20, 0);
            case "loyalty" -> cost.calc(12, 7);
            case "impaling" -> cost.calc(1, 8);
            case "riptide" -> cost.calc(17, 7);
            case "mending",
                 "swift_sneak" -> cost.calc(25, 25);
            default -> 1 + 10 * (i - 1);
        };
    }

    private static int maxCost(int i, Enchantment e) {
        Cost cost = (int base, int per) -> base + per * (i - 1);
        if (e == null)
            return minCost(i, null) + 5;
        return switch (e.getKey().getKey().toLowerCase()) {
            case "protection" -> cost.calc(12, 11);
            case "fire_protection" -> cost.calc(18, 8);
            case "blast_protection" -> cost.calc(13, 8);
            case "feather_falling" -> cost.calc(11, 6);
            case "projectile_protection" -> cost.calc(9, 6);
            case "respiration" -> cost.calc(40, 10);
            case "aqua_affinity" -> cost.calc(41, 0);
            case "thorns",
                 "fire_aspect" -> cost.calc(60, 20);
            case "binding_curse",
                 "flame",
                 "infinity",
                 "loyalty",
                 "riptide",
                 "channeling",
                 "multishot",
                 "quick_charge",
                 "piercing",
                 "vanishing_curse" -> cost.calc(50, 0);
            case "knockback" -> cost.calc(55, 20);
            case "looting",
                 "fortune",
                 "luck_of_the_sea",
                 "lure" -> cost.calc(65, 9);
            case "efficiency" -> cost.calc(51, 10);
            case "silk_touch" -> cost.calc(65, 0);
            case "unbreaking" -> cost.calc(55, 8);
            case "mending",
                 "swift_sneak" -> cost.calc(75, 25);
            case "depth_strider",
                 "frost_walker",
                 "soul_speed" -> cost.calc(25, 10);
            case "sweeping", "sweeping_edge" -> cost.calc(20, 9);
            case "power" -> cost.calc(16, 10);
            case "sharpness" -> cost.calc(21, 11);
            case "smite",
                 "bane_of_arthropods" -> cost.calc(25, 8);
            case "impaling" -> cost.calc(21, 8);
            case "punch" -> cost.calc(37, 20);
            default -> minCost(i, e) + 5;
        };
    }

    /**
     * Utility method for internal use.
     *
     * @param random
     * @param list
     * @param i
     * @return
     */
    public static Optional<WeightedEnchantment> getRandomItem(UberRandom random, List<WeightedEnchantment> list, int i) {
        if (i <= 0) {
            return Optional.empty();
        } else {
            int j = random.nextInt(i);
            return getWeightedItem(list, j);
        }
    }

    /**
     * Utility method for internal use.
     *
     * @param list
     * @param i
     * @return
     */
    public static Optional<WeightedEnchantment> getWeightedItem(List<WeightedEnchantment> list, int i) {
        Iterator<WeightedEnchantment> iterator = list.iterator();

        WeightedEnchantment entry;
        do {
            if (!iterator.hasNext()) {
                return Optional.empty();
            }

            entry = iterator.next();
            i -= (int) entry.weight();
        } while (i >= 0);

        return Optional.of(entry);
    }

    /**
     * Utility method for internal use.
     *
     * @param list
     * @return
     */
    public static int getTotalWeight(List<WeightedEnchantment> list) {
        long i = 0L;

        WeightedEnchantment entry;

        for (Iterator<WeightedEnchantment> iterator = list.iterator(); iterator.hasNext(); i += (long) entry.weight()) {
            entry = iterator.next();
        }

        return (int) i;
    }

    /**
     * Utility method for internal use.
     *
     * @param random
     * @param list
     * @return
     */
    public static Optional<WeightedEnchantment> getRandomItem(UberRandom random, List<WeightedEnchantment> list) {
        return getRandomItem(random, list, getTotalWeight(list));
    }

    /**
     * Utility method for internal use.
     *
     * @param vanilla
     * @param custom
     */
    public record CustomList(List<WeightedEnchantment> vanilla, List<WeightedEnchantment> custom) {
    }
}
