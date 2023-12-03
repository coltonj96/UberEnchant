package me.sciguymjm.uberenchant;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.events.UberEvent;
import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.api.utils.UberConfiguration.UberRecord;
import me.sciguymjm.uberenchant.commands.*;
import me.sciguymjm.uberenchant.commands.abstraction.UberCommand;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import me.sciguymjm.uberenchant.utils.FileUtils;
import me.sciguymjm.uberenchant.utils.UberLocale;
import me.sciguymjm.uberenchant.utils.enchanting.AnvilEvents;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentTableEvents;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * The Main class of UberEnchant
 */
public class UberEnchant extends JavaPlugin {

    private static UberEnchant plugin;
    private static Economy economy;

    public void onEnable() {
        plugin = this;
        File enchantments = new File(getDataFolder() + "/enchantments/default/vanilla_enchantments.yml");
        File old = new File(getDataFolder(), "enchantments.yml");
        saveDefaultConfig();

        initResources();

        /*FileConfiguration config = getConfig();
        if (!config.isSet("mechanics")) {
            config.set("mechanics.enchantment_table", true);
            config.set("mechanics.anvil", true);
            config.setComments("mechanics", List.of("Enable/Disable custom enchantment table and anvil mechanics"));
            saveConfig();
        }*/


        UberLocale.load(FileUtils.getFile("/locale/" + getConfig().getString("locale") + ".properties"));

        if (old.exists()) {
            try {
                YamlConfiguration.loadConfiguration(old).save(enchantments);
                old.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        new Metrics(this, 1952);

        if (!economyLoaded())
            getLogger().log(Level.WARNING, UberLocale.get("uberenchant.economy_not_found"));

        registerTabCommand("uadd", new AddCommand());
        registerTabCommand("uclear", new ClearCommand());
        registerTabCommand("ucost", new CostCommand());
        registerTabCommand("udel", new DelCommand());
        registerTabCommand("uextract", new ExtractCommand());
        registerCommand("uhelp", new HelpCommand());
        registerCommand("uinsert", new InsertCommand());
        registerTabCommand("ulist", new ListCommand());
        registerCommand("ureload", new ReloadCommand());
        registerTabCommand("uset", new SetCommand());

        EffectEnchantment.init();
        UberConfiguration.loadFromEnchantmentsFolder();

        final boolean enchants = FileUtils.getBoolean("/mechanics/enchantment_table.yml", "enabled");
        final boolean anvil = FileUtils.getBoolean("/mechanics/anvil.yml", "enabled");

        if (enchants) {
            registerEvents(new EnchantmentTableEvents());
        }

        if (anvil) {
            registerEvents(new AnvilEvents());
        }

        registerEvents(new Listener() {
            @EventHandler
            public void OnLoad(ServerLoadEvent event) {
                long found = UberEnchantment.getRegisteredEnchantments().stream().filter(a -> !a.getKey().getNamespace().equalsIgnoreCase(getName())).count();
                long loaded = UberRecord.values().stream().filter(a -> a.enchantment() instanceof UberEnchantment && !a.enchantment().getKey().getNamespace().equalsIgnoreCase(getName())).count();
                getLogger().log(Level.INFO,  "Found: " + found + " Registered UberEnchantments.");
                getLogger().log(Level.INFO, "Loaded: " + loaded + " UberEnchantments.");
                if (enchants)
                    getLogger().log(Level.INFO, "Custom enchantment table mechanics enabled!");
                if (anvil)
                    getLogger().log(Level.INFO, "Custom anvil mechanics enabled!");
            }
        });
    }

    public void onDisable() {
        unloadEnchantments();
    }

    private void initResources() {
        FileUtils.initResource("locale/en_us.properties");
        FileUtils.initResource("enchantments/default/vanilla_enchantments.yml");
        FileUtils.initResource("enchantments/default/vanilla_effects.yml");
        FileUtils.initResource("mechanics/anvil.yml");
        FileUtils.initResource("mechanics/enchantment_table.yml");
    }

    private boolean economyLoaded() {
        if (getServer().getPluginManager().getPlugin("Vault") == null)
            return false;
        RegisteredServiceProvider<Economy> economyService = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyService == null)
            return false;
        economy = economyService.getProvider();
        return economy.isEnabled();
    }

    private void registerCommand(String name, UberCommand command) {
        getCommand(name).setExecutor(command);
    }

    private void registerTabCommand(String name, UberTabCommand executor) {
        registerCommand(name, executor);
        getCommand(name).setTabCompleter(executor);
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    private void unloadEnchantments() {
        try {
            Field fieldByKey = Enchantment.class.getDeclaredField("byKey");
            Field fieldByName = Enchantment.class.getDeclaredField("byName");
            fieldByKey.setAccessible(true);
            fieldByName.setAccessible(true);
            HashMap<NamespacedKey, Enchantment> byKey = (HashMap<NamespacedKey, Enchantment>) fieldByKey.get(null);
            HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) fieldByName.get(null);
            for (Enchantment enchantment : UberEnchantment.values()) {
                if (enchantment instanceof UberEnchantment) {
                    byKey.remove(enchantment.getKey());
                    byName.remove(enchantment.getName());
                }
            }
        } catch (Exception ignored) {}
    }

    /**
     * Returns an instance of UberEnchant.
     *
     * @return The current instance of UberEnchant
     */
    public static UberEnchant instance() {
        return plugin;
    }

    /**
     * Convenience static method to register events with UberEnchant.
     *
     * @param listener - The listener to register
     */
    public static void registerEvents(Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    /**
     * Gets the economy currently being used.<br>
     * (May return null in case of no economy or unspported economy)
     *
     * @return The current economy or null
     */
    public static Economy getEconomy() {
        return economy;
    }

    /**
     * Gets whether there is an economy or not.
     *
     * @return True if the server has a supported economy
     */
    public static boolean hasEconomy() {
        return economy != null;
    }
}
