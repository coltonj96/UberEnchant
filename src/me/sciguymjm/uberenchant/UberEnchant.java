package me.sciguymjm.uberenchant;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.api.utils.UberConfiguration.UberRecord;
import me.sciguymjm.uberenchant.commands.*;
import me.sciguymjm.uberenchant.commands.abstraction.UberCommand;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import me.sciguymjm.uberenchant.utils.UberLocale;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
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
import java.util.logging.Level;

/**
 * The Main class of UberEnchant
 */
public class UberEnchant extends JavaPlugin {

    private static UberEnchant plugin;
    private static Economy economy;

    public void onEnable() {
        plugin = this;
        File enchantments = new File(getDataFolder() + "/enchantments/default/vanilla_enchantments.yml");
        File effects = new File(getDataFolder() + "/enchantments/default/vanilla_effects.yml");
        File old = new File(getDataFolder(), "enchantments.yml");
        saveDefaultConfig();

        if (!(new File(getDataFolder() + "/locale/", "en_us.properties")).exists())
            saveResource("locale/en_us.properties", false);

        UberLocale.load(new File(getDataFolder() + "/locale/" + getConfig().getString("locale") + ".properties"));

        if (old.exists()) {
            try {
                YamlConfiguration.loadConfiguration(old).save(enchantments);
                old.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!enchantments.exists())
            saveResource("enchantments/default/vanilla_enchantments.yml", false);

        if (!effects.exists())
            saveResource("enchantments/default/vanilla_effects.yml", false);

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


        registerEvents(new Listener() {
            @EventHandler
            public void OnLoad(ServerLoadEvent event) {
                long found = UberEnchantment.getRegisteredEnchantments().stream().filter(a -> !a.getKey().getNamespace().equalsIgnoreCase(getName())).count();
                long loaded = UberRecord.values().stream().filter(a -> a.enchantment() instanceof UberEnchantment && !a.enchantment().getKey().getNamespace().equalsIgnoreCase(getName())).count();
                getLogger().log(Level.INFO, "Found: " + found + " Registered UberEnchantments.");
                getLogger().log(Level.INFO, "Loaded: " + loaded + " UberEnchantments.");
            }
        });
    }

    public void onDisable() {
        unloadEnchantments();
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
                    byKey.remove(enchantment.getKey());
                    byName.remove(enchantment.getName());
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
