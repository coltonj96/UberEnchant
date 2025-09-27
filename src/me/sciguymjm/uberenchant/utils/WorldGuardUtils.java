package me.sciguymjm.uberenchant.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class WorldGuardUtils {

    public static boolean flag(LivingEntity who, Location where, StateFlag flag) {
        if (worldGuardLoaded()) {
            if (who == null || !flag.requiresSubject())
                return flag(where, flag);
            LocalPlayer lp = WorldGuardPlugin.inst().wrapPlayer((Player) who);
            return getWorldGuard().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt(where), lp, flag) == StateFlag.State.DENY;
        }
        return true;
    }

    public static boolean flag(Location where, StateFlag flag) {
        if (worldGuardLoaded())
            return getWorldGuard().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt(where), null, flag) == StateFlag.State.DENY;
        return true;
    }

    public static boolean denyPvp(LivingEntity who, Location where) {
        return flag(who, where, Flags.PVP);
    }

    public static boolean denyDamageAnimals(LivingEntity who, Location where) {
        return flag(who, where, Flags.DAMAGE_ANIMALS);
    }

    public static boolean denyPotionSplash(LivingEntity who, Location where) {
        return flag(who, where, Flags.POTION_SPLASH);
    }

    public static boolean worldGuardLoaded() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null ||
                !Bukkit.getPluginManager().isPluginEnabled("WorldGuard"))
            return false;
        return WorldGuard.getInstance() != null;
    }

    public static WorldGuard getWorldGuard() {
        if (worldGuardLoaded())
            return WorldGuard.getInstance();
        return null;
    }
}
