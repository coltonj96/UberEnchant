package me.sciguymjm.uberenchant.utils;

import me.sciguymjm.uberenchant.UberEnchant;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

/**
 * Utility class for internal use.
 */
public class FileUtils {

    public static void initResource(String path) {
        if (!path.startsWith("/"))
            path = "/" + path;
        File f = getFile(path);
        if (!f.exists())
            UberEnchant.instance().saveResource(path.replaceFirst("/", ""), false);
    }

    public static File getFile(String path) {
        return getFile(UberEnchant.instance(), path);
    }

    public static File getFile(Plugin plugin, String path) {
        if (!path.startsWith("/"))
            path = "/" + path;
        return new File(plugin.getDataFolder() + path);
    }

    public static YamlConfiguration loadConfig(Plugin plugin, String path) {
        File f = getFile(plugin, path);
        if (!f.exists())
            return null;
        return YamlConfiguration.loadConfiguration(f);
    }

    public static YamlConfiguration loadConfig(String path) {
        return loadConfig(UberEnchant.instance(), path);
    }

    public static YamlConfiguration loadConfig(File file) {
        if (!file.exists())
            return null;
        return YamlConfiguration.loadConfiguration(file);
    }

    public static YamlConfiguration createConfig(Plugin plugin, String path) {
        File file = getFile(plugin, path);
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return config;
    }

    public static YamlConfiguration createConfig(String path) {
        return createConfig(UberEnchant.instance(), path);
    }

    public static <T> T get(String path, String key, Class<T> type) {
        return get(path, key, null, type);
    }

    public static <T> T get(String path, String key, T def, Class<T> type) {
        return get(UberEnchant.instance(), path, key, def, type);
    }

    public static <T> T get(Plugin plugin, String path, String key, Class<T> type) {
        return get(plugin, path, key, null, type);
    }

    public static <T> T get(Plugin plugin, String path, String key, T def, Class<T> type) {
        YamlConfiguration config = loadConfig(plugin, path);
        if (config == null)
            return null;
        return config.getObject(key, type, def);
    }

    public static <T> T updateAndGet(String path, String key, T def, Class<T> type) {
        if (!contains(path, key))
            set(path, key, def);
        return get(path, key, def, type);
    }

    public static <T> void update(String path, String key, Object def, Class<T> type) {
        if (!contains(path, key))
            set(path, key, def);
    }

    public static boolean contains(String path, String key) {
        return contains(UberEnchant.instance(), path, key);
    }

    public static boolean contains(Plugin plugin, String path, String key) {
        YamlConfiguration config = loadConfig(plugin, path);
        if (config == null)
            return false;
        return config.contains(key);
    }

    public static void set(String path, String key, Object value) {
        set(UberEnchant.instance(), path, key, value);
    }

    public static void set(Plugin plugin, String path, String key, Object value) {
        File file = getFile(plugin, path);
        YamlConfiguration config = loadConfig(file);
        if (config == null)
            config = createConfig(plugin, path);
        config.set(key, value);
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
