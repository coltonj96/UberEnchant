package me.sciguymjm.uberenchant.utils;

import me.sciguymjm.uberenchant.UberEnchant;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

/**
 * Economy related utilities.
 */
public class EconomyUtils {

    private static Economy economy;

    static {
        if (hasEconomy())
            economy = getEconomy();
    }

    /**
     * For internal use.
     *
     * @return Boolean
     */
    public static boolean hasEconomy() {
        return UberEnchant.hasEconomy();
    }

    /**
     * For internal use.
     *
     * @return Boolean
     */
    public static boolean useEconomy() {
        return UberEnchant.instance().getConfig().getBoolean("use_economy");
    }

    /**
     * For internal use.
     *
     * @return Economy
     */
    public static Economy getEconomy() {
        return UberEnchant.getEconomy();
    }

    /**
     * For internal use.
     *
     * @param path String
     * @return Double
     */
    public static double getCost(String path) {
        return UberEnchant.instance().getConfig().getDouble(path);
    }

    /**
     * For internal use.
     *
     * @return Double
     */
    public static double getBalance(Player player) {
        return economy.getBalance(player);
    }

    /**
     * For internal use.
     *
     * @param cost Double
     * @return Boolean
     */
    public static boolean has(Player player, double cost) {
        return economy.has(player, cost);
    }

    /**
     * For internal use.
     *
     * @param cost Double
     * @return EconomyResponse
     */
    public static EconomyResponse withdraw(Player player, double cost) {
        return economy.withdrawPlayer(player, cost);
    }
}
