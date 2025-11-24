package me.sciguymjm.uberenchant.utils.plugins;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class WorldGuardUtils extends PluginUtils {

    private static WorldGuardUtils instance;

    WorldGuardUtils() {
        super("WorldGuard");
        if (pluginLoaded)
            pluginLoaded = WorldGuard.getInstance() != null;
        instance = this;
    }

    public static WorldGuardUtils instance() {
        if (instance == null)
            instance = new WorldGuardUtils();
        return instance;
    }

    public static boolean isLoaded() {
        return instance.isPluginLoaded();
    }

    public static boolean flag(LivingEntity who, Location where, StateFlag flag) {
        if (isLoaded()) {
            if (who == null || !flag.requiresSubject())
                return flag(where, flag);
            LocalPlayer lp = WorldGuardPlugin.inst().wrapPlayer((Player) who);
            return getWorldGuard().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt(where), lp, flag) == StateFlag.State.DENY;
        }
        return true;
    }

    public static boolean flag(Location where, StateFlag flag) {
        if (isLoaded())
            return getWorldGuard().getPlatform().getRegionContainer().createQuery().queryState(BukkitAdapter.adapt(where), null, flag) == StateFlag.State.DENY;
        return true;
    }

    public static boolean denyDamage(LivingEntity a, LivingEntity b) {
        return isLoaded() && (denyPvp(a, b) | denyDamageAnimals(a, b) | denyPotionSplash(a, b));
    }

    public static boolean denyPvp(LivingEntity who, Location where) {
        return isLoaded() && flag(who, where, Flags.PVP);
    }

    public static boolean denyPvp(LivingEntity who, LivingEntity entity) {
        return who instanceof Player && entity instanceof Player && denyPvp(who, entity.getLocation());
    }

    public static boolean denyDamageAnimals(LivingEntity who, Location where) {
        return isLoaded() && flag(who, where, Flags.DAMAGE_ANIMALS);
    }

    public static boolean denyDamageAnimals(LivingEntity who, LivingEntity entity) {
        return entity instanceof Animals && denyDamageAnimals(who, entity.getLocation());
    }

    public static boolean denyPotionSplash(LivingEntity who, Location where) {
        return isLoaded() && flag(who, where, Flags.POTION_SPLASH);
    }

    public static boolean denyPotionSplash(LivingEntity who, LivingEntity entity) {
        return denyPotionSplash(who, entity.getLocation());
    }

    public static WorldGuard getWorldGuard() {
        if (isLoaded())
            return WorldGuard.getInstance();
        return null;
    }
}
