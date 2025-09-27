package me.sciguymjm.uberenchant.api.utils;

import me.sciguymjm.uberenchant.UberEnchant;
import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.utils.UberLocale;
import me.sciguymjm.uberenchant.utils.VersionUtils;
import org.bukkit.Bukkit;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;

/**
 * Utility class to handle saving and loading UberRecords from config files.
 */
public class UberConfiguration {

    private static final Set<String> plugins = new HashSet<>();

    private static final Set<Enchantment> enchantments;

    static {
        if (VersionUtils.isAtLeast("1.20.4"))
            enchantments = new HashSet<>(Registry.ENCHANTMENT.stream().toList());
        else
            enchantments = new HashSet<>(List.of(Enchantment.values()));
        Collections.addAll(enchantments, UberEnchantment.values());
    }

    public static Set<String> getIntegrated() {
        return plugins;
    }

    public static Set<UberRecord> getRecords() {
        return UberRecord.getRecords();
    }

    public static List<UberRecord> getRecords(Predicate<UberRecord> filter) {
        return UberRecord.getRecords(filter);
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
        for (Enchantment enchant : enchantments.stream().distinct().toList()) {
            if (UberRecord.getRecords().stream().anyMatch(e -> e.getEnchant().equals(enchant)))
                continue;
                    //displayName = FileUtils.get(ex, "enchants/" + key.getKey() + ".yml", "Definition.DisplayName", displayName, String.class);
            NamespacedKey key = VersionUtils.getKey(enchant);
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
                    if (name.contains("curse") || enchant.isCursed()) {
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
                        //data = YamlConfiguration.loadConfiguration(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    cost = section.getConfigurationSection("cost_for_level");
                }
                cost.getKeys(false).forEach(k -> {
                    try {
                        list.put(Integer.parseInt(k), cost.getDouble(k));
                    } catch (NumberFormatException ignored) {}
                });
                UberRecord record = new UberRecord(enchant,
                        key,
                        (enchant instanceof UberEnchantment) ? ((UberEnchantment) enchant).getDisplayName() : UberLocale.get(key.toString().replace(":", ".")),
                        section.getInt("min_level"),
                        section.getInt("max_level"),
                        section.getDouble("cost"),
                        section.getDouble("cost_multiplier"),
                        section.getDouble("removal_cost"),
                        section.getDouble("extraction_cost"),
                        section.getBoolean("use_on_anything"),
                        section.getStringList("aliases"),
                        list);
                if (VersionUtils.getKey(enchant).getNamespace().equalsIgnoreCase("ExcellentEnchants"))
                    UberRecord.addRecord(new ExcellentEnchantsRecord(record));
                else
                    UberRecord.addRecord(record);
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

        for (File path : folder.listFiles())
            if (path.isDirectory())
                for (File file : path.listFiles(f -> f.getName().endsWith(".yml")))
                    if (!files.contains(file))
                        loadFromFile(file);
            else
                if (path.getName().endsWith(".yml") && !files.contains(path))
                    loadFromFile(path);
    }

    public static void updateConfigs() {
        File folder = new File(UberEnchant.instance().getDataFolder() + "/enchantments/");

        for (File path : folder.listFiles())
            if (path.isDirectory())
                for (File file : path.listFiles(f -> f.getName().endsWith(".yml")))
                    if (!files.contains(file))
                        loadFromFile(file);
                    else
                    if (path.getName().endsWith(".yml") && !files.contains(path))
                        loadFromFile(path);
    }

    private static  <T> T find(Collection<T> collection, Predicate<T> predicate) {
        return collection.stream().filter(predicate).findFirst().orElse(null);
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
        for (UberRecord record : UberRecord.values().stream().filter(a -> VersionUtils.getKey(a.getEnchant()).getNamespace().equals(plugin.getName().toLowerCase(Locale.ROOT))).toList()) {
            ConfigurationSection path = data.createSection(record.getKey().getKey());
            path.set("min_level", record.getMinLevel());
            path.set("max_level", record.getMaxLevel());
            path.set("cost", record.getCost());
            path.set("cost_multiplier", record.getCostMultiplier());
            path.createSection("cost_for_level", record.getLevelCost());
            path.set("removal_cost", record.getRemovalCost());
            path.set("extraction_cost", record.getExtractionCost());
            path.set("use_on_anything", record.getCanUseOnAnything());
            path.set("aliases", record.getAliases());
        }
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reloadAll() {
        UberRecord.reset();
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
        return UberRecord.values().stream().filter(e -> e.getEnchant().equals(enchantment)).findFirst().orElse(null);
    }

    /**
     * Adds an UberRecord.
     *
     * @param record The record to add
     * @return True if it was added
     */
    public static boolean addRecord(UberRecord record) {
        return UberRecord.addRecord(record);
    }

    /*private static void update(File file) {
        YamlConfiguration config = FileUtils.loadConfig(file);
        for (String key : config.getKeys(false)) {
            Enchantment enchantment = find(enchantments, e -> VersionUtils.getKey(e).toString().equalsIgnoreCase(key));
            if (!config.contains(key + ".settings")) {
                ConfigurationSection settings = config.createSection(key + ".settings");
                int weight = Utils
                if (enchantment instanceof UberEnchantment ue)
                    ue.getRarity().getWeight()
                settings.set("weight", );
            }
        }
    }*/

    /*
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
    /*public static void registerUberRecord(UberEnchantment enchantment, double cost, double multiplier, double removal, double extract, boolean anything, List<String> aliases, Map<Integer, Double> level_cost) {
        if (!UberEnchantment.isRegistered(enchantment))
            UberEnchantment.register(enchantment);
        UberRecord.addRecord(new UberRecord(enchantment, cost, multiplier, removal, extract, anything, aliases, level_cost));
    }*/

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> key) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(key.apply(t), Boolean.TRUE) == null;
    }

    @SuppressWarnings("deprecation")
    public static void integrate()  {
        List<Enchantment> temp;
        if (VersionUtils.isAtLeast("1.20.4"))
            temp = new ArrayList<>(Registry.ENCHANTMENT.stream().toList());
        else
            temp = Arrays.asList(Enchantment.values());
        List<String> plugins = temp.stream().filter(distinctByKey(e -> VersionUtils.getKey(e).getNamespace()))
                .filter(e -> !(e instanceof UberEnchantment) && !VersionUtils.getKey(e).getNamespace().equalsIgnoreCase(NamespacedKey.MINECRAFT))
                .map(k -> VersionUtils.getKey(k).getNamespace()).toList();
        for (String name : plugins) {
            Plugin plugin = Arrays.stream(Bukkit.getPluginManager().getPlugins()).filter(p -> p.getName().toLowerCase(Locale.ROOT).equals(name)).findFirst().orElse(null);
            if (plugin == null)
                continue;
            UberConfiguration.plugins.add(name);
            File file = new File(UberEnchant.instance().getDataFolder() + "/enchantments/" + name + "/default_enchantments.yml");
            if (file.exists())
                return;
            YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
            List<Enchantment> enchantments = temp.stream().filter(a -> VersionUtils.getKey(a).getNamespace().equalsIgnoreCase(name)).toList();
            for (Enchantment enchant : enchantments) {
                ConfigurationSection path = data.createSection(VersionUtils.getKey(enchant).getKey());
                path.set("min_level", 1);
                path.set("max_level", 10);
                path.set("cost", 1000.0);
                path.set("cost_multiplier", 0.02);
                path.set("removal_cost", 100.0);
                path.set("extraction_cost", 1000.0);
                path.set("use_on_anything", false);
                path.set("aliases", new ArrayList<String>());
            }
            try {
                data.save(file);
                UberEnchant.log(Level.INFO, UberLocale.getF("console.plugin_integrated", enchantments.size(), plugin.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}