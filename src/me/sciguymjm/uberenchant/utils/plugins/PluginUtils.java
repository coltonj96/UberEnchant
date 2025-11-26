package me.sciguymjm.uberenchant.utils.plugins;

import org.bukkit.Bukkit;

public abstract class PluginUtils {

    protected boolean pluginLoaded;

    protected String name;

    protected PluginUtils(String plugin) {
        pluginLoaded = Bukkit.getPluginManager().getPlugin(plugin) != null &&
                Bukkit.getPluginManager().isPluginEnabled(plugin);
        this.name = plugin;
    }

    public static void initAll() {
        new WorldGuardUtils();
        new TownyUtils();
        new VaultUtils();
        new ProtocolLibUtils();
    }

    public boolean isPluginLoaded() {
        return pluginLoaded;
    }
}
