package me.sciguymjm.uberenchant.api.utils;

import me.sciguymjm.uberenchant.UberEnchant;
import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.utils.UberLocale;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utility class to handle saving and loading UberRecords from config files.
 */
public class UberConfiguration {

    private static List<UberRecord> values = new ArrayList<>();

    public static List<UberRecord> getRecords() {
        return values;
    }

    public static List<UberRecord> getRecords(Predicate<UberRecord> filter) {
        return values.stream().filter(filter).toList();
    }

    private static final Set<File> files = new HashSet<>();

    /**
     * Loads UberRecords found in specified Yaml file. Usage:
     *
     * <pre>{@code
     * UberConfiguration.loadFromFile(new File(plugin.getDataFolder(), "MyFile.yml"));
     * }</pre>
     *
     * @param file The file to load from
     * @see #loadFromFile(String)
     * @see #loadFromFile(String, String)
     */
    @SuppressWarnings("deprecation")
    public static void loadFromFile(File file) {
        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
        files.add(file);
        List<Enchantment> temp = new ArrayList<>(Registry.ENCHANTMENT.stream().toList());
        Collections.addAll(temp, UberEnchantment.values());
        for (Enchantment enchant : temp) {
            if (!(enchant instanceof UberEnchantment) && !enchant.getKey().getNamespace().equalsIgnoreCase(NamespacedKey.MINECRAFT))
                continue;
            if (data.contains(enchant.getKey().getKey(), true)) {
                Map<Integer, Double> list = new HashMap<>();
                ConfigurationSection section = data.getConfigurationSection(enchant.getKey().getKey().toLowerCase());
                if (!section.contains("cost_for_level")) {
                    ConfigurationSection cost = section.createSection("cost_for_level");
                    cost.createSection("1");
                    cost.createSection("5");
                    cost.createSection("10");
                    if (enchant.getKey().getKey().toLowerCase().contains("curse") || enchant.isCursed()) {
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
                        data = YamlConfiguration.loadConfiguration(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ConfigurationSection section2 = data.getConfigurationSection(enchant.getKey().getKey().toLowerCase() + ".cost_for_level");
                section2.getKeys(false).forEach(key -> {
                    try {
                        Integer level = Integer.parseInt(key);
                        Double cost = section2.getDouble(key);
                        list.put(level, cost);
                    } catch (NumberFormatException ignored) {}
                });
                UberRecord record = new UberRecord(enchant, enchant.getKey().getKey(), (enchant instanceof UberEnchantment) ? ((UberEnchantment) enchant).getDisplayName() : UberLocale.get("enchant." + enchant.getKey().getKey()), section.getInt("min_level"), section.getInt("max_level"), section.getDouble("cost"), section.getDouble("cost_multiplier"), section.getDouble("removal_cost"), section.getDouble("extraction_cost"), section.getBoolean("use_on_anything"), section.getStringList("aliases"), list);
                addRecord(record);
            }
        }
    }

    /**
     * Loads UberRecords found in Yaml file specified by the path. Usage:
     *
     * <pre>{@code
     * UberConfiguration.loadFromFile(plugin.getDataFolder() + "/MyFile.yml");
     * }</pre>
     *
     * @param path The path to load from
     * @see #loadFromFile(File)
     * @see #loadFromFile(String, String)
     */
    public static void loadFromFile(String path) {
        loadFromFile(new File(path));
    }

    /**
     * Loads UberRecords found in specified Yaml file and path. Usage:
     *
     * <pre>{@code
     * UberConfiguration.loadFromFile(plugin.getDataFolder().getPath(), "MyFile.yml"));
     * }</pre>
     *
     * @param path The path to the file
     * @param file The file
     * @see #loadFromFile(File)
     * @see #loadFromFile(String)
     */
    public static void loadFromFile(String path, String file) {
        loadFromFile(new File(path, file));
    }

    /**
     * Loads all UberRecords in any Yaml files found under UberEnchants
     * enchantments folder<br>
     * (Not meant to be used in other plugins.<br>
     * Plugins can place their Yaml files there as this is called from
     * UberEnchant itself)
     */
    public static void loadFromEnchantmentsFolder() {
        File folder = new File(UberEnchant.instance().getDataFolder() + "/enchantments/");
        for (File path : folder.listFiles()) {
            if (path.isDirectory()) {
                for (File file : path.listFiles(f -> f.getName().endsWith(".yml"))) {
                    loadFromFile(file);
                }
            } else {
                if (path.getName().endsWith(".yml"))
                    loadFromFile(path);
            }
        }
    }

    /**
     * Saves any UberRecords for the specified plugin to the specified file
     * under UberEnchants enchantments folder<br>
     * Usage:
     *
     * <pre>{@code
     * UberConfiguration.saveToEnchantmentsFolder(plugin, "MyFile.yml");
     * }</pre>
     *
     * @param plugin The plugin to save records from
     * @param file   The file to save
     */
    public static void saveToEnchantmentsFolder(Plugin plugin, String file) {
        saveToFile(plugin, new File(UberEnchant.instance().getDataFolder() + "/enchantments/" + plugin.getName() + "/" + file));
    }

    /**
     * Saves any UberRecords for the specified plugin to a Yaml file. Usage:
     *
     * <pre>{@code
     * UberConfiguration.saveToFile(plugin, "MyFile.yml");
     * }</pre>
     *
     * @param plugin the plugin to save records from
     * @param path   The file to save
     * @see #saveToFile(Plugin, File)
     */
    public static void saveToFile(Plugin plugin, String path) {
        saveToFile(plugin, new File(plugin.getDataFolder(), path));
    }

    /**
     * Saves any UberRecords for the specified plugin to a Yaml file. Usage:
     *
     * <pre>{@code
     * UberConfiguration.saveToFile(plugin, new File(plugin.getDataFolder(), "MyFile.yml"));
     * }</pre>
     *
     * @param plugin The plugin to save records from
     * @param file   The file to save
     * @see #saveToFile(Plugin, String)
     */
    public static void saveToFile(Plugin plugin, File file) {
        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
        for (UberRecord record : values.stream().filter(a -> a.enchantment.getKey().getNamespace().equals(plugin.getName().toLowerCase(Locale.ROOT))).toList()) {
            ConfigurationSection path = data.createSection(record.name);
            path.set("min_level", record.min_level);
            path.set("max_level", record.max_level);
            path.set("cost", record.cost);
            path.set("cost_multiplier", record.cost_multiplier);
            path.createSection("cost_for_level", record.cost_for_level);
            path.set("removal_cost", record.removal_cost);
            path.set("extraction_cost", record.extraction_cost);
            path.set("use_on_anything", record.can_use_on_anything);
            path.set("aliases", record.aliases);
        }
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reloadAll() {
        values = new ArrayList<>();
        files.forEach(f -> {
            if (f.exists())
                loadFromFile(f);
        });
    }

    /**
     * Get an UberRecord by a specified enchantment.
     *
     * @param enchantment The enchantment
     * @return The associated UberRecord or null
     */
    public static UberRecord getByEnchant(Enchantment enchantment) {
        return values.stream().filter(e -> e.enchantment.equals(enchantment)).findFirst().orElse(null);
    }

    /**
     * Convenient method to both add an UberRecord and register a specified
     * UberEnchantment.
     *
     * @param enchantment The UberEnchantment
     * @param cost        Cost of the enchantment
     * @param multiplier  Cost multiplier
     * @param removal     Cost to remove
     * @param extract     Cost to extract
     * @param anything    Wether it can be used on anything or not
     * @param aliases     List of aliases (Can be empty)
     * @param level_cost  Cost per level map (Can be empty)
     */
    public static void registerUberRecord(UberEnchantment enchantment, double cost, double multiplier, double removal, double extract, boolean anything, List<String> aliases, Map<Integer, Double> level_cost) {
        if (!UberEnchantment.isRegistered(enchantment))
            UberEnchantment.register(enchantment);
        addRecord(new UberRecord(enchantment, enchantment.getKey().getKey(), enchantment.getDisplayName(), enchantment.getStartLevel(), enchantment.getMaxLevel(), cost, multiplier, removal, extract, anything, aliases, level_cost));
    }

    /**
     * Adds an UberRecord.
     *
     * @param record The record to add
     * @return True if it was added
     */
    public static boolean addRecord(UberRecord record) {
        return values.add(record);
    }

    /**
     * Utility record class for ease of adding records
     */
    public record UberRecord(Enchantment enchantment, String name, String display_name, int min_level, int max_level,
                             double cost, double cost_multiplier, double removal_cost, double extraction_cost,
                             boolean can_use_on_anything, List<String> aliases, Map<Integer, Double> cost_for_level) {

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
            return name;
        }

        /**
         * Gets the display name of the enchantment this record represents.
         *
         * @return The display name of the enchantment
         */
        public String getDisplayName() {
            return display_name;
        }

        /**
         * Gets the minimum level the enchantment can be.
         *
         * @return The minimum level of the enchantment
         */
        public int getMinLevel() {
            return min_level;
        }

        /**
         * Gets the maximum level the enchantment can be.
         *
         * @return The maximum level of the enchantment
         */
        public int getMaxLevel() {
            return max_level;
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
            return cost_multiplier;
        }

        /**
         * Gets the removal cost.
         *
         * @return The removal cost
         */
        public double getRemovalCost() {
            return removal_cost;
        }

        /**
         * Gets the extraction cost.
         *
         * @return the extraction cost
         */
        public double getExtractionCost() {
            return extraction_cost;
        }

        /**
         * Gets wether or not the enchantment can be used on anything.
         *
         * @return If the enchantment can be used on anything
         */
        public boolean getCanUseOnAnything() {
            return can_use_on_anything;
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
        public Map<Integer, Double> getCostForLevel() {
            return cost_for_level;
        }

        /**
         * Gets a list of all available UberRecords
         *
         * @return A list of UberRecords
         */
        public static List<UberRecord> values() {
            return values;
        }
    }
}