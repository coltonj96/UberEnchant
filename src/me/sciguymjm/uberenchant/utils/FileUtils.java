package me.sciguymjm.uberenchant.utils;

import me.sciguymjm.uberenchant.UberEnchant;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class FileUtils {

    public static void initResource(String path) {
        File f = new File(UberEnchant.instance().getDataFolder() + "/" + path);
        if (!f.exists())
            UberEnchant.instance().saveResource(path, false);
    }

    public static File getFile(String path) {
        return new File(UberEnchant.instance().getDataFolder() + path);
    }

    public static YamlConfiguration loadConfig(String path) {
        File f = getFile(path);
        if (!f.exists())
            return null;
        return YamlConfiguration.loadConfiguration(f);
    }

    public static boolean getBoolean(String path, String key) {
        return loadConfig(path).getBoolean(key, false);
    }
}
