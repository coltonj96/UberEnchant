package me.sciguymjm.uberenchant.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Utility class for interacting with the WorldGuard plugin.
 * Provides methods to check region flags, particularly for PvP.
 */
public class WorldGuardUtils {

    /**
     * Checks if PvP is enabled at the victim's location according to WorldGuard flags.
     * This method also considers the INVINCIBILITY flag on the victim.
     *
     * If WorldGuard is not enabled or an error occurs during the check,
     * this method defaults to returning {@code true} (PvP allowed).
     *
     * @param attacker The player initiating the PvP action (currently unused for specific bypass checks, but good for context).
     * @param victim The player who is being targeted.
     * @return {@code false} if WorldGuard explicitly denies PvP at the victim's location or if the victim is invincible,
     *         {@code true} otherwise (PvP allowed by default or if WorldGuard is not present/errors out).
     */
    public static boolean isPvPEnabled(Player attacker, Player victim) {
        // Check if WorldGuard plugin is present and enabled
        if (!Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            return true; // WorldGuard not present, default to PvP allowed
        }

        try {
            WorldGuard worldGuard = WorldGuard.getInstance();
            RegionContainer container = worldGuard.getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();

            // Convert Bukkit Location to WorldEdit Location for WorldGuard querying
            com.sk89q.worldedit.util.Location victimWgLocation = BukkitAdapter.adapt(victim.getLocation());

            // 1. Check the PVP flag at the victim's location.
            // queryState can return ALLOW, DENY, or null (if the flag is not set).
            // If null, WorldGuard typically defaults to allowed or inherits from global/parent regions.
            // We explicitly block PvP if the state is DENY.
            State pvpState = query.queryState(victimWgLocation, null, Flags.PVP); // Using null for LocalPlayer checks general state
            if (pvpState == State.DENY) {
                return false; // PvP is explicitly denied by WorldGuard at the victim's location.
            }

            // 2. Check the INVINCIBILITY flag at the victim's location.
            // If the INVINCIBILITY flag is set to ALLOW, the player is considered invincible.
            State invincibilityState = query.queryState(victimWgLocation, null, Flags.INVINCIBILITY);
            if (invincibilityState == State.ALLOW) { // Note: ALLOW for INVINCIBILITY means the player IS invincible.
                return false; // Victim is invincible according to WorldGuard.
            }

            // If PvP is not explicitly DENIED and victim is not INVINCIBLE, PvP is considered allowed.
            return true;

        } catch (NoClassDefFoundError e) {
            // This catch block handles cases where WorldGuard classes are not found at runtime.
            // This can happen if UberEnchant was compiled with WorldGuard API but WorldGuard plugin is not on the server.
            // Optionally log this, but for a soft dependency, failing silently and allowing action is common.
            // Bukkit.getLogger().warning("[UberEnchant] WorldGuard API classes not found. Assuming PvP is allowed.");
            return true; // Fail safe: allow action if WorldGuard classes are missing.
        } catch (Exception e) {
            // Catch any other unexpected errors during the WorldGuard check.
            Bukkit.getLogger().severe("[UberEnchant] Error while checking WorldGuard PvP flags: " + e.getMessage());
            e.printStackTrace(); // Log the full stack trace for debugging.
            return true; // Fail safe: allow action in case of other errors.
        }
    }
}
