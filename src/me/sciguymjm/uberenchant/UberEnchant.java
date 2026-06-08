package me.sciguymjm.uberenchant;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.ArmorEquippedListener;
import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.api.utils.UberRunnable;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.*;
import me.sciguymjm.uberenchant.commands.abstraction.UberCommand;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import me.sciguymjm.uberenchant.metrics.BStatsMetrics;
import me.sciguymjm.uberenchant.metrics.FastStatsMetrics;
import me.sciguymjm.uberenchant.utils.*;
import me.sciguymjm.uberenchant.utils.enchanting.AnvilEvents;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentTableEvents;
import me.sciguymjm.uberenchant.utils.enchanting.GrindstoneEvents;
import me.sciguymjm.uberenchant.utils.enchanting.VillagerEvents;
import me.sciguymjm.uberenchant.utils.plugins.PluginUtils;
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

    private UberRunnable runnable;

    private static UberEnchant plugin;

    public void onEnable() {
        plugin = this;

        Debugging.get(this);

        //debug.enable();

        initResources();
        update();
        PluginUtils.initAll();

        if (Versions.isV1_20_4())
            EffectEnchantment.init();

        UberCommand.init();

        final boolean enchants = FileUtils.get("/mechanics/enchantment_table.yml", "enabled", false, Boolean.class);
        final boolean anvil = FileUtils.get("/mechanics/anvil.yml", "enabled", false, Boolean.class);
        final boolean villagers = FileUtils.get("/mechanics/villager.yml", "enabled", false, Boolean.class);
        final boolean grindstone = FileUtils.get("/mechanics/grindstone.yml", "enabled", false, Boolean.class);

        if (enchants)
            registerEvents(new EnchantmentTableEvents());
        if (anvil)
            registerEvents(new AnvilEvents());
        if (villagers)
            registerEvents(new VillagerEvents());
        if (grindstone)
            registerEvents(new GrindstoneEvents());

        registerEvents(new ArmorEquippedListener());

        registerEvents(new Listener() {
            @EventHandler
            public void OnLoad(ServerLoadEvent event) {
                new BukkitRunnable() {
                    private String toggle(boolean toggle) {
                        return toggle ? "&aEnabled" : "&cDisabled";
                    }
                    @Override
                    public void run() {
                        UberConfiguration.integrate();
                        UberConfiguration.loadFromEnchantmentsFolder();
                        long loaded = UberConfiguration.getRecords(a -> a.getEnchant() instanceof UberEnchantment && a.getKey() != null && !a.getKey().getNamespace().equalsIgnoreCase(getName())).size();
                        long def = UberConfiguration.getRecords(a -> a.getEnchant() instanceof EffectEnchantment && a.getKey() != null && a.getKey().getNamespace().equalsIgnoreCase(getName())).size();
                        long vanilla = UberConfiguration.getRecords(a -> a.getKey() != null && a.getKey().getNamespace().equalsIgnoreCase(NamespacedKey.MINECRAFT)).size();
                        List<String> strings = new ArrayList<>(List.of(new String[]{
                                UberLocale.getCF("&6", "console.enchantment_table_status", toggle(enchants)),
                                UberLocale.getCF("&6", "console.anvil_status", toggle(anvil)),
                                UberLocale.getCF("&6", "console.villager_status", toggle(villagers)),
                                UberLocale.getCF("&6", "console.grindstone_status", toggle(grindstone)),
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

        runnable = UberRunnable.getInstance();

        startMetrics();

        /* new BukkitRunnable() {
            @Override
            public void run() {
                WeightedChance<EnchantmentTableUtils.WeightedEnchantment> weights = new WeightedChance<>();
                Map<String, Integer> stats = new HashMap<>();
                UberRecord.getRecords().forEach(record -> {
                    EnchantmentTableUtils.WeightedEnchantment entry = new EnchantmentTableUtils.WeightedEnchantment(record.getEnchant(), 1);
                    weights.add(entry);
                    stats.put(VersionUtils.key(record.getEnchant()), 0);
                });
                stats.put("null", 0);
                weights.add(new EnchantmentTableUtils.WeightedEnchantment(null, 0), Debugging.debug(weights.getTotal()) / 2.0);
                //double total = UberEnchantment.getRegisteredEnchantments().size() + 1.0;
                double total = 1000000.0;
                for (int n = 1; n <= total; n++) {
                    Enchantment enc = weights.select().getEnchantment();
                    if (enc == null) {
                        stats.put("null", stats.get("null") + 1);
                        continue;
                    }
                    String name = VersionUtils.key(enc);
                    stats.put(name, stats.get(name) + 1);
                }
                stats.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEach(entry -> {
                    double percent = (entry.getValue() / total) * 100.0;
                    percent = Math.round(percent * 100.0);
                    percent /= 100.0;
                    log(Level.INFO, "&8" + entry.getKey() + " = (" + entry.getValue() + ") " + percent + "%");
                });
            }
        }.runTaskLater(this, 20L);*/
    }

    public void onDisable() {
        //unloadEnchantments();
        runnable.stop();
        stopMetrics();
    }

    private void initResources() {
        saveDefaultConfig();

        FileUtils.initResource("locale/en_us.properties");
        FileUtils.initResource("locale/zh_cn.properties");
        FileUtils.initResource("enchantments/default/vanilla_enchantments.yml");
        FileUtils.initResource("enchantments/default/vanilla_effects.yml");
        FileUtils.initResource("mechanics/anvil.yml");
        FileUtils.initResource("mechanics/enchantment_table.yml");
        FileUtils.initResource("mechanics/villager.yml");
        FileUtils.initResource("mechanics/grindstone.yml");

        UberLocale.update();
        //UberLocale.updateEnchantments();
        UberLocale.load(FileUtils.getFile("/locale/" + getConfig().getString("locale") + ".properties"));
        //UberLocale.add("enchantments", FileUtils.getFile("/locale/enchantments.properties"));
    }

    private void startMetrics() {
        FastStatsMetrics.getInstance();
        BStatsMetrics.getInstance();
    }

    private void stopMetrics() {
        BStatsMetrics.stop();
        FastStatsMetrics.stop();
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

        if (old.exists())
            try {
                YamlConfiguration.loadConfiguration(old).save(enchantments);
                old.delete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
