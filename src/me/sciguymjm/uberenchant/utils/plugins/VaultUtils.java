package me.sciguymjm.uberenchant.utils.plugins;

import me.sciguymjm.uberenchant.UberEnchant;
import me.sciguymjm.uberenchant.utils.UberLocale;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Level;

public class VaultUtils extends PluginUtils {

    private static VaultUtils instance;

    private static Economy economy;

    protected VaultUtils() {
        super("Vault");
        if (UberEnchant.instance().getConfig().getBoolean("use_economy")) {
            if (pluginLoaded) {
                RegisteredServiceProvider<Economy> economyService = UberEnchant.instance().getServer().getServicesManager().getRegistration(Economy.class);
                if (economyService != null) {
                    economy = economyService.getProvider();
                    pluginLoaded = economy.isEnabled();
                } else {
                    UberEnchant.log(Level.WARNING, UberLocale.getC("&c", "uberenchant.economy_not_found"));
                    pluginLoaded = false;
                }
            } else {
                UberEnchant.log(Level.WARNING, UberLocale.getC("&c", "uberenchant.economy_not_found"));
            }
        }
        instance = this;
    }

    public static VaultUtils instance() {
        if (instance == null)
            instance = new VaultUtils();
        return instance;
    }

    public static boolean isLoaded() {
        return instance.isPluginLoaded();
    }

    /**
     * Gets the economy currently being used.<br>
     * (May return null in case of no economy or unsupported economy)
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

    public static boolean useEconomy() {
        return UberEnchant.instance().getConfig().getBoolean("use_economy");
    }

    public static double getCost(String path) {
        return UberEnchant.instance().getConfig().getDouble(path);
    }

    public static double getBalance(Player player) {
        return economy.getBalance(player);
    }

    public static boolean has(Player player, double cost) {
        return economy.has(player, cost);
    }

    public static EconomyResponse withdraw(Player player, double cost) {
        return economy.withdrawPlayer(player, cost);
    }
}
