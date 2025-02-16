package me.sciguymjm.uberenchant;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.ArmorEquippedListener;
import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.api.utils.UberConfiguration.UberRecord;
import me.sciguymjm.uberenchant.api.utils.UberRunnable;
import me.sciguymjm.uberenchant.commands.*;
import me.sciguymjm.uberenchant.commands.abstraction.UberCommand;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import me.sciguymjm.uberenchant.utils.Debugging;
import me.sciguymjm.uberenchant.utils.FileUtils;
import me.sciguymjm.uberenchant.utils.UberLocale;
import me.sciguymjm.uberenchant.utils.enchanting.AnvilEvents;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentTableEvents;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * The Main class of UberEnchant
 */
public class UberEnchant extends JavaPlugin {

    private static UberEnchant plugin;
    private static Economy economy;

    public void onEnable() {
        Debugging.enable();
        plugin = this;

        initResources();
        update();
        EffectEnchantment.init();

        new Metrics(this, 1952);

        if (getConfig().getBoolean("use_economy") && !economyLoaded())
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

        UberConfiguration.loadFromEnchantmentsFolder();

        final boolean enchants = FileUtils.get("/mechanics/enchantment_table.yml", "enabled", false, Boolean.class);
        final boolean anvil = FileUtils.get("/mechanics/anvil.yml", "enabled", false, Boolean.class);

        if (enchants)
            registerEvents(new EnchantmentTableEvents());
        if (anvil)
            registerEvents(new AnvilEvents());

        registerEvents(new ArmorEquippedListener());

        registerEvents(new Listener() {
            @EventHandler
            public void OnLoad(ServerLoadEvent event) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        long found = UberEnchantment.getRegisteredEnchantments().stream().filter(a -> !a.getKey().getNamespace().equalsIgnoreCase(getName())).count();
                        long loaded = UberRecord.values().stream().filter(a -> a.enchantment() instanceof UberEnchantment && !a.enchantment().getKey().getNamespace().equalsIgnoreCase(getName())).count();
                        getLogger().log(Level.INFO, UberLocale.getF("console.found_enchantments", found));
                        getLogger().log(Level.INFO, UberLocale.getF("console.loaded_enchantments", loaded));
                        getLogger().log(Level.INFO, UberLocale.getF("console.enchantment_table_status", enchants ? "enabled" : "disabled"));
                        getLogger().log(Level.INFO, UberLocale.getF("console.anvil_status", anvil ? "enabled" : "disabled"));
                        //test();
                    }
                }.runTaskLater(plugin, 100);
            }
        });

        UberRunnable.getInstance();
    }

    public void onDisable() {
        //unloadEnchantments();
    }

    private void initResources() {
        saveDefaultConfig();

        FileUtils.initResource("locale/en_us.properties");
        FileUtils.initResource("enchantments/default/vanilla_enchantments.yml");
        FileUtils.initResource("enchantments/default/vanilla_effects.yml");
        FileUtils.initResource("mechanics/anvil.yml");
        FileUtils.initResource("mechanics/enchantment_table.yml");

        UberLocale.load(FileUtils.getFile("/locale/" + getConfig().getString("locale") + ".properties"));
    }

    private void update() {
        File enchantments = new File(getDataFolder() + "/enchantments/default/vanilla_enchantments.yml");
        File old = new File(getDataFolder(), "enchantments.yml");

        FileConfiguration config = getConfig();
        if (config.isSet("mechanics")) {

            boolean anvil = config.getBoolean("mechanics.anvil", false);
            boolean table = config.getBoolean("mechanics.enchantment_table", false);

            FileUtils.set("/mechanics/enchantment_table.yml", "enabled", table);
            FileUtils.set("/mechanics/anvil.yml", "enabled", anvil);

            config.set("mechanics.enchantment_table", null);
            config.set("mechanics.anvil", null);
            config.set("mechanics", null);
            config.setComments("mechanics", null);
            saveConfig();
        }

        if (old.exists()) {
            try {
                YamlConfiguration.loadConfiguration(old).save(enchantments);
                old.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    /*@SuppressWarnings({"unchecked", "deprecation"})
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
    }*/

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
