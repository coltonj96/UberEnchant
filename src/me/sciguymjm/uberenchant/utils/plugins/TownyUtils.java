package me.sciguymjm.uberenchant.utils.plugins;

import com.palmergames.bukkit.towny.utils.CombatUtil;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

public class TownyUtils extends PluginUtils {

    private static TownyUtils instance;

    protected TownyUtils() {
        super("Towny");
        instance = this;
    }

    public static TownyUtils instance() {
        if (instance == null)
            instance = new TownyUtils();
        return instance;
    }

    public static boolean isLoaded() {
        return instance.isPluginLoaded();
    }

    public static boolean denyDamage(Entity a, Entity b) {
        return isLoaded() && CombatUtil.preventDamageCall(a, b, EntityDamageEvent.DamageCause.CUSTOM);
    }
}
