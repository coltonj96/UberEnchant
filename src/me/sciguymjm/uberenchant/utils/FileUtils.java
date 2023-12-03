package me.sciguymjm.uberenchant.utils;

import me.sciguymjm.uberenchant.UberEnchant;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

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

    public static YamlConfiguration loadConfig(File file) {
        if (!file.exists())
            return null;
        return YamlConfiguration.loadConfiguration(file);
    }

    public static Object get(String path, String key, Object def) {
        return loadConfig(path).get(key, def);
    }

    public static void set(String path, String key, Object value) {
        File file = getFile(path);
        YamlConfiguration config = loadConfig(file);
        config.set(key, value);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
