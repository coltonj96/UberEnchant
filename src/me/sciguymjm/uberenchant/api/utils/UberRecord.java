package me.sciguymjm.uberenchant.api.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import java.util.*;
import java.util.function.Predicate;

public class UberRecord {

    private static Set<UberRecord> values = new HashSet<>();

    protected Enchantment enchantment;
    protected NamespacedKey key;
    protected String displayName;
    protected int minLevel;
    protected int maxLevel;
    protected double cost;
    protected double costMultiplier;
    protected double removelCost;
    protected double extractionCost;
    protected boolean useOnAnything;
    protected List<String> aliases;
    protected Map<Integer, Double> levelCost;

    /*public static UberRecord fromFile(Enchantment enchantment, File file, YamlConfiguration data) {
        Set<Enchantment> temp;
        if (VersionUtils.isAtLeast("1.20.4"))
            temp = new HashSet<>(Registry.ENCHANTMENT.stream().toList());
        else
            temp = new HashSet<>(List.of(Enchantment.values()));
        Collections.addAll(temp, UberEnchantment.values());
        NamespacedKey key = VersionUtils.getKey(enchantment);
        String name = key.getKey().toLowerCase();
        if (data.contains(name, true)) {
            Map<Integer, Double> list = new HashMap<>();
            ConfigurationSection section = data.getConfigurationSection(name);
            ConfigurationSection cost;
            if (!section.contains("cost_for_level")) {
                cost = section.createSection("cost_for_level");
                cost.createSection("1");
                cost.createSection("5");
                cost.createSection("10");
                if (name.contains("curse") || enchantment.isCursed()) {
                    cost.set("1", 100.0);
                    cost.set("5", 600.0);
                    cost.set("10", 1700.0);
                } else {
                    cost.set("1", 1000.0);
                    cost.set("5", 6000.0);
                    cost.set("10", 17000.0);
                }
                try {
                    data.save(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                cost = data.getConfigurationSection(name + ".cost_for_level");
            }
            cost.getKeys(false).forEach(k -> {
                try {
                    list.put(Integer.parseInt(k), cost.getDouble(k));
                } catch (NumberFormatException ignored) {}
            });

            return new UberRecord(enchantment,
                    key,
                    (enchantment instanceof UberEnchantment) ? ((UberEnchantment) enchantment).getDisplayName() : UberLocale.get(key.toString().replace(":", ".")),
                    section.getInt("min_level"),
                    section.getInt("max_level"),
                    section.getDouble("cost"),
                    section.getDouble("cost_multiplier"),
                    section.getDouble("removal_cost"),
                    section.getDouble("extraction_cost"),
                    section.getBoolean("use_on_anything"),
                    section.getStringList("aliases"),
                    list);
        }
        return null;
    }*/

    public UberRecord(Enchantment enchantment,
                      NamespacedKey key,
                      String displayName,
                      int minLevel,
                      int maxLevel,
                      double cost,
                      double costMultiplier,
                      double removelCost,
                      double extractionCost,
                      boolean useOnAnything,
                      List<String> aliases,
                      Map<Integer, Double> levelCost) {
        this.enchantment = enchantment;
        this.key = key;
        this.displayName = displayName;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.cost = cost;
        this.costMultiplier = costMultiplier;
        this.removelCost = removelCost;
        this.extractionCost = extractionCost;
        this.useOnAnything = useOnAnything;
        this.aliases = aliases;
        this.levelCost = levelCost;
    }

    /**
     * Gets the enchantment associated with this record.
     *
     * @return The enchantment
     */
    public Enchantment getEnchant() {
        return enchantment;
    }

    /**
     * Gets the name of this record.
     *
     * @return The name of the record (Same as
     * enchantment.getKey().getKey())
     */
    public String getName() {
        return key.getKey();
    }

    /**
     *  Gets the name of this record.
     *
     * @return The name of the record (Same as
     * enchantment.getKey().getKey())
     */
    public NamespacedKey getKey() {
        return key;
    }

    /**
     * Gets the display name of the enchantment this record represents.
     *
     * @return The display name of the enchantment
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the minimum level the enchantment can be.
     *
     * @return The minimum level of the enchantment
     */
    public int getMinLevel() {
        return minLevel;
    }

    /**
     * Gets the maximum level the enchantment can be.
     *
     * @return The maximum level of the enchantment
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Gets the cost of the enhchantment.
     *
     * @return The cost of the enchantment
     */
    public double getCost() {
        return cost;
    }

    /**
     * Gets the cost multiplier.
     *
     * @return The cost multiplier
     */
    public double getCostMultiplier() {
        return costMultiplier;
    }

    /**
     * Gets the removal cost.
     *
     * @return The removal cost
     */
    public double getRemovalCost() {
        return removelCost;
    }

    /**
     * Gets the extraction cost.
     *
     * @return the extraction cost
     */
    public double getExtractionCost() {
        return extractionCost;
    }

    /**
     * Gets wether or not the enchantment can be used on anything.
     *
     * @return If the enchantment can be used on anything
     */
    public boolean getCanUseOnAnything() {
        return useOnAnything;
    }

    /**
     * Gets a list of aliases for the enchantment, can be empty.
     *
     * @return A list of aliases
     */
    public List<String> getAliases() {
        return aliases;
    }

    /**
     * Gets a map of levels and their costs, can be empty.
     *
     * @return A map of levels and their cost
     */
    public Map<Integer, Double> getLevelCost() {
        return levelCost;
    }

    public static Set<UberRecord> values() {
        return values;
    }

    /**
     * Adds an UberRecord.
     *
     * @param record The record to add
     * @return True if it was added
     */
    public static boolean addRecord(UberRecord record) {
        if (values.stream().noneMatch(r -> r.getEnchant().equals(record.getEnchant())))
            return values.add(record);
        return false;
    }

    public static UberRecord byEnchantment(Enchantment enchantment) {
        return values.stream().filter(r -> r.getEnchant().equals(enchantment)).findFirst().orElse(null);
    }

    public static Set<UberRecord> getRecords() {
        return values;
    }

    public static List<UberRecord> getRecords(Predicate<UberRecord> filter) {
        return values.stream().filter(filter).toList();
    }

    protected static void reset() {
        values = new HashSet<>();
    }
}
