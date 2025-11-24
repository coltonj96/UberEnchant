package me.sciguymjm.uberenchant;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.ArmorEquippedListener;
import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.api.utils.UberRunnable;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.*;
import me.sciguymjm.uberenchant.commands.*;
import me.sciguymjm.uberenchant.commands.abstraction.UberCommand;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import me.sciguymjm.uberenchant.utils.*;
import me.sciguymjm.uberenchant.utils.enchanting.AnvilEvents;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentTableEvents;
import me.sciguymjm.uberenchant.utils.plugins.PluginUtils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.MultiLineChart;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * The Main class of UberEnchant
 */
public class UberEnchant extends JavaPlugin {

    private static UberEnchant plugin;

    public void onEnable() {
        //Debugging.enable();
        plugin = this;

        initResources();
        update();
        PluginUtils.initAll();

        if (Versions.isV1_20_4())
            EffectEnchantment.init();

        Metrics metrics = new Metrics(this, 1952);
        metrics.addCustomChart(new MultiLineChart("players_and_servers", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            valueMap.put("servers", 1);
            valueMap.put("players", Bukkit.getOnlinePlayers().size());
            return valueMap;
        }));

        registerTabCommand("uadd", new AddCommand());
        registerTabCommand("uclear", new ClearCommand());
        registerTabCommand("ucost", new CostCommand());
        registerTabCommand("udel", new DelCommand());
        registerTabCommand("uextract", new ExtractCommand());
        registerCommand("uhelp", new HelpCommand());
        registerCommand("uinsert", new InsertCommand());
        registerTabCommand("uitem", new ItemCommand());
        registerTabCommand("ulist", new ListCommand());
        registerCommand("ureload", new ReloadCommand());
        registerTabCommand("uset", new SetCommand());

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
                        UberConfiguration.integrate();
                        UberConfiguration.loadFromEnchantmentsFolder();
                        long loaded = UberConfiguration.getRecords(a -> a.getEnchant() instanceof UberEnchantment && a.getKey() != null && !a.getKey().getNamespace().equalsIgnoreCase(getName())).size();
                        long def = UberConfiguration.getRecords(a -> a.getEnchant() instanceof EffectEnchantment && a.getKey() != null && a.getKey().getNamespace().equalsIgnoreCase(getName())).size();
                        long vanilla = UberConfiguration.getRecords(a -> a.getKey() != null && a.getKey().getNamespace().equalsIgnoreCase(NamespacedKey.MINECRAFT)).size();
                        List<String> strings = new ArrayList<>(List.of(new String[]{
                                UberLocale.getCF("&6", "console.enchantment_table_status", enchants ? "&aenabled" : "&cdisabled"),
                                UberLocale.getCF("&6", "console.anvil_status", anvil ? "&aenabled" : "&cdisabled"),
                                UberLocale.getCF("&6", "console.vanilla_enchantments", "&a" + vanilla),
                                UberLocale.getCF("&6", "console.default_enchantments", "&a" + def),
                                UberLocale.getCF("&6", "console.loaded_enchantments", "&a" + loaded)
                        }));
                        UberConfiguration.getIntegrated().forEach(name -> {
                            int enchantments = UberConfiguration.getRecords(record -> record.getKey() != null && record.getKey().getNamespace().equalsIgnoreCase(name)).size();
                            strings.add(UberLocale.getCF("&6", "console.integrated_loaded", "&a" + enchantments, "&a" + name));
                        });
                        strings.add(UberLocale.getCF("&6", "console.total_enchantments", "&a" + UberConfiguration.getRecords().stream().filter(value -> value.getEnchant() != null).count()));
                        int length = strings.stream().max(Comparator.comparing(String::length)).get().length();
                        /*String[] UE = {
                                "UU  UU BBBBB  EEEEE RRRRR  EEEEE NN    NN  CCCCC HH  HH  AAAA  NN    NN TTTTTT",
                                "UU  UU BB   B EE    RR   R EE    NNNN  NN CC     HH  HH AA  AA NNNN  NN   TT  ",
                                "UU  UU BBBBB  EEEE  RRRRR  EEEE  NN NN NN CC     HHHHHH AAAAAA NN NN NN   TT  ",
                                "UU  UU BB   B EE    RR RR  EE    NN  NNNN CC     HH  HH AA  AA NN  NNNN   TT  ",
                                "UUUUUU BBBBB  EEEEE RR  RR EEEEE NN    NN  CCCCC HH  HH AA  AA NN    NN   TT  "
                        };
                        log(Level.INFO, "\n" + String.join("\n", UE));
                        Arrays.stream(UE).forEach(s -> log(Level.INFO, s));*/
                        log(Level.INFO, "&8" + "=".repeat(length+4));
                        strings.forEach(string -> log(Level.INFO, "&8||  &5" + string + " ".repeat(length - string.length()+2) + "&8||"));
                        log(Level.INFO, "&8" + "=".repeat(length+4));
                    }
                }.runTask(plugin);
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
        FileUtils.initResource("locale/zh_cn.properties");
        FileUtils.initResource("enchantments/default/vanilla_enchantments.yml");
        FileUtils.initResource("enchantments/default/vanilla_effects.yml");
        FileUtils.initResource("mechanics/anvil.yml");
        FileUtils.initResource("mechanics/enchantment_table.yml");

        UberLocale.update();
        //UberLocale.updateEnchantments();
        UberLocale.load(FileUtils.getFile("/locale/" + getConfig().getString("locale") + ".properties"));
        //UberLocale.add("enchantments", FileUtils.getFile("/locale/enchantments.properties"));
    }

    private void initTags() {
        MetaTag.create("Test", PersistentDataType.BOOLEAN);
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
                throw new RuntimeException(e);
            }
        }
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

    public static void log(Level level, String... message) {
        if (level.equals(Level.WARNING)) {
            String[] temp = new String[message.length + 1];
            temp[0] = "&4!!!WARNING!!!";
            System.arraycopy(message, 0, temp, 1, message.length);
            message = temp;
        }
        ChatUtils.response(Bukkit.getConsoleSender(), message);
        //plugin.getLogger().log(level, message);
    }
}
