package me.sciguymjm.uberenchant.utils;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtils {

    private static String version;

    static {
        version = Bukkit.getBukkitVersion();
    }

    public static int[] getBukkitVersion() {
        return parseVersion(version);
    }

    private static int[] parseVersion(String version) {
        Matcher m = Pattern.compile("(\\d+)\\.(\\d+)\\.{0,1}(\\d?)").matcher(version);
        if (m.find())
            return new int[]{
                    Integer.parseInt(m.group(1)),
                    Integer.parseInt(m.group(2)),
                    m.group(3).isBlank() ? 0 : Integer.parseInt(m.group(3))
            };
        return new int[] {0, 0, 0};
    }

    public static boolean isOlder(String v) {
        int[] v1 = getBukkitVersion();
        int[] v2 = parseVersion(v);
        return v1[0] <= v2[0] && v1[1] <= v2[1] && v1[2] <= v2[2];
    }

    public static boolean isAtLeast(String v) {
        int[] v1 = getBukkitVersion();
        int[] v2 = parseVersion(v);
        return v1[0] >= v2[0] && v1[1] >= v2[1] && v1[2] >= v2[2];
    }
}
