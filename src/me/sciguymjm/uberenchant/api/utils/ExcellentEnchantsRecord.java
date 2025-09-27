package me.sciguymjm.uberenchant.api.utils;

import me.sciguymjm.uberenchant.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

public class ExcellentEnchantsRecord extends UberRecord implements EnchantmentData {

    private static Plugin plugin;
    private static YamlConfiguration types;

    static {
        plugin = Bukkit.getPluginManager().getPlugin("ExcellentEnchants");
        types = FileUtils.loadConfig(plugin, "item_types.yml");
    }

    private double weight;
    private int minBase;
    private int minPer;
    private int maxBase;
    private int maxPer;
    private boolean treasure;
    private List<String> targets;

    public ExcellentEnchantsRecord(Enchantment enchantment,
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
        super(enchantment, key, displayName, minLevel, maxLevel, cost, costMultiplier, removelCost, extractionCost, useOnAnything, aliases, levelCost);
        this.displayName = FileUtils.get(Bukkit.getPluginManager().getPlugin("ExcellentEnchants"), "enchants/" + getKey().getKey() + ".yml", "Definition.DisplayName", super.displayName, String.class);
        YamlConfiguration config = FileUtils.loadConfig(plugin, "enchants/" + key.getKey() + ".yml");
        if (config != null) {
            weight = config.getInt("Definition.Weight");
            minBase = config.getInt("Definition.MinCost.Base");
            minPer = config.getInt("Definition.MinCost.Per_Level");
            maxBase = config.getInt("Definition.MaxCost.Base");
            maxPer = config.getInt("Definition.MaxCost.Per_Level");
            treasure = config.getBoolean("Distribution.Treasure");
            targets = types.getStringList("Categories." + config.getString("Definition.PrimaryItems") + ".Items");
        }
    }

    public ExcellentEnchantsRecord(UberRecord record) {
        this(record.enchantment,
                record.key,
                record.displayName,
                record.minLevel,
                record.maxLevel,
                record.cost,
                record.costMultiplier,
                record.removelCost,
                record.extractionCost,
                record.useOnAnything,
                record.aliases,
                record.levelCost);
    }

    public List<String> getTargets() {
        return targets;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public int getMinBase() {
        return minBase;
    }

    @Override
    public int getMinPer() {
        return minPer;
    }

    @Override
    public int getMaxBase() {
        return maxBase;
    }

    @Override
    public int getMaxPer() {
        return maxPer;
    }

    @Override
    public boolean isTreasure() {
        return treasure;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
