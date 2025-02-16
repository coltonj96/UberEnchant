package me.sciguymjm.uberenchant.utils;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for internal use.
 */
public class VersionUtils {

    private static String version;

    static {
        version = Bukkit.getBukkitVersion();
    }

    /**
     * Gets the current server version as an integer array
     *
     * @return An int array of the current server version
     */
    public static int[] getBukkitVersion() {
        return parseVersion(version);
    }

    private static int[] parseVersion(String version) {
        Matcher m = Pattern.compile("(\\d+)\\.(\\d+)\\.?(\\d?)").matcher(version);
        if (m.find())
            return new int[]{
                    Integer.parseInt(m.group(1)),
                    Integer.parseInt(m.group(2)),
                    m.group(3).isBlank() ? 0 : Integer.parseInt(m.group(3))
            };
        return new int[] {0, 0, 0};
    }

    /**
     * Checks if the current server version is at least the input or better
     *
     * @param v A version string in the format of ##.##.## IE 1.21.1
     * @return Whether the current version is at least the input version or not
     */
    public static boolean isAtLeast(String v) {
        int[] v1 = getBukkitVersion();
        int[] v2 = parseVersion(v);
        return (v1[0] > v2[0]) ||
                (v1[0] == v2[0] && v1[1] > v2[1]) ||
                (v1[0] == v2[0] && v1[1] == v2[1] && v1[2] >= v2[2]);
    }
}
