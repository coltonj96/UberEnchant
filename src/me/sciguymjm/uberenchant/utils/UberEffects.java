package me.sciguymjm.uberenchant.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Effects enum for use in UberEnchant
 */
public class UberEffects {

    private final PotionEffectType effect;
    private final String name;
    private final String display;
    private final int id;
    private final int value;

    public static Map<String, UberEffects> values;

     static {
        values = new HashMap<>();

        values.put("SPEED", wrap(PotionEffectType.SPEED, "speed", "effects.speed", 1, 1));
        values.put("SLOW", wrap(getType("SLOW:0", "SLOWNESS:1.20.4"), "slowness", "effects.slow", 2, -1));
        values.put("FAST_DIGGING", wrap(getType("FAST_DIGGING:0", "HASTE:1.20.4"), "haste", "effects.fast_digging", 3, 1));
        values.put("SLOW_DIGGING", wrap(getType("SLOW_DIGGING:0", "MINING_FATIGUE:1.20.4"), "miningfatigue", "effects.slow_digging", 4, -1));
        values.put("INCREASE_DAMAGE", wrap(getType("INCREASE_DAMAGE:0", "STRENGTH:1.20.4"), "strength", "effects.increase_damage", 5, 1));
        values.put("HEAL", wrap(getType("HEAL:0", "INSTANT_HEALTH:1.20.4"), "instanthealth", "effects.heal", 6, 1));
        values.put("HARM", wrap(getType("HARM:0", "INSTANT_DAMAGE:1.20.4"), "instantdamage", "effects.harm", 7, -1));
        values.put("JUMP", wrap(getType("JUMP:0", "JUMP_BOOST:1.20.4"), "jumpboost", "effects.jump", 8, 1));
        values.put("CONFUSION", wrap(getType("CONFUSION:0", "NAUSEA:1.20.4"), "nausea", "effects.confusion", 9, -1));
        values.put("REGENERATION", wrap(PotionEffectType.REGENERATION, "regeneration", "effects.regeneration", 10, 1));
        values.put("DAMAGE_RESISTANCE", wrap(getType("DAMAGE_RESISTANCE:0", "RESISTANCE:1.20.4"), "resistance", "effects.damage_resistance", 11, 1));
        values.put("FIRE_RESISTANCE", wrap(PotionEffectType.FIRE_RESISTANCE, "fireresistance", "effects.fire_resistance", 12, 1));
        values.put("WATER_BREATHING", wrap(PotionEffectType.WATER_BREATHING, "waterbreathing", "effects.water_breathing", 13, 1));
        values.put("INVISIBILITY", wrap(PotionEffectType.INVISIBILITY, "invisibility", "effects.invisibility", 14, 1));
        values.put("BLINDNESS", wrap(PotionEffectType.BLINDNESS, "blindness", "effects.blindness", 15, -1));
        values.put("NIGHT_VISION", wrap(PotionEffectType.NIGHT_VISION, "nightvision", "effects.night_vision", 16, 1));
        values.put("HUNGER", wrap(PotionEffectType.HUNGER, "hunger", "effects.hunger", 17, -1));
        values.put("WEAKNESS", wrap(PotionEffectType.WEAKNESS, "weakness", "effects.weakness", 18, -1));
        values.put("POISON", wrap(PotionEffectType.POISON, "poison", "effects.poison", 19, -1));
        values.put("WITHER", wrap(PotionEffectType.WITHER, "wither", "effects.wither", 20, -1));
        values.put("HEALTH_BOOST", wrap(PotionEffectType.HEALTH_BOOST, "healthboost", "effects.health_boost", 21, 1));
        values.put("ABSORPTION", wrap(PotionEffectType.ABSORPTION, "absorption", "effects.absorption", 22, 1));
        values.put("SATURATION", wrap(PotionEffectType.SATURATION, "saturation", "effects.saturation", 23, 1));
        values.put("GLOWING", wrap(PotionEffectType.GLOWING, "glowing", "effects.glowing", 24, 0));
        values.put("LEVITATION", wrap(PotionEffectType.LEVITATION, "levitation", "effects.levitation", 25, 0));
        values.put("LUCK", wrap(PotionEffectType.LUCK, "luck", "effects.luck", 26, 1));
        values.put("UNLUCK", wrap(PotionEffectType.UNLUCK, "badluck", "effects.unluck", 27, -1));
        values.put("SLOW_FALLING", wrap(PotionEffectType.SLOW_FALLING, "slowfalling", "effects.slow_falling", 28, 1));
        values.put("CONDUIT_POWER", wrap(PotionEffectType.CONDUIT_POWER, "conduitpower", "effects.conduit_power", 29, 1));
        values.put("DOLPHINS_GRACE", wrap(PotionEffectType.DOLPHINS_GRACE, "dolphinsgrace", "effects.dolphins_grace", 30, 1));
        values.put("BAD_OMEN", wrap(PotionEffectType.BAD_OMEN, "badomen", "effects.bad_omen", 31, -1));
        values.put("HERO_OF_THE_VILLAGE", wrap(PotionEffectType.HERO_OF_THE_VILLAGE, "heroofthevillage", "effects.hero_of_the_village", 32, 1));

        if (VersionUtils.isAtLeast("1.19"))
            values.put("DARKNESS", wrap(PotionEffectType.DARKNESS, "darkness", "effects.darkness", 33, -1));
        if (VersionUtils.isAtLeast("1.20.5")) {
            values.put("TRIAL_OMEN", wrap(getType("TRIAL_OMEN"), "trialomen", "effects.trial_omen", 34, 0));
            values.put("RAID_OMEN", wrap(getType("RAID_OMEN"), "raidomen", "effects.raid_omen", 35, 0));
            values.put("WIND_CHARGED", wrap(getType("WIND_CHARGED"), "windcharged", "effects.wind_charged", 36, -1));
            values.put("WEAVING", wrap(getType("WEAVING"), "weaving", "effects.weaving", 37, -1));
            values.put("OOZING", wrap(getType("OOZING"), "oozing", "effects.oozing", 38, -1));
            values.put("INFESTED", wrap(getType("INFESTED"), "infested", "effects.infested", 39, -1));
        }
    }

    private static PotionEffectType getType(String key) {
        NamespacedKey namespacedKey = NamespacedKey.minecraft(key.toLowerCase());
        return VersionUtils.isAtLeast("1.20.4") ? Registry.EFFECT.get(namespacedKey) : PotionEffectType.getByName(namespacedKey.getKey().toUpperCase());
    }

    private static PotionEffectType getType(String... s) {
        Version[] v = new Version[s.length];
        for (int i = 0; i < s.length; i++) {
            String[] split = s[i].split(":");
            v[i] = v(split[0], split[1]);
        }
        Version version = Arrays.stream(v).sorted((a, b) -> b.version.compareTo(a.version)).filter(ver -> VersionUtils.isAtLeast(ver.version)).findFirst().orElse(null);
        NamespacedKey key = NamespacedKey.minecraft(version.type.toLowerCase());
        return VersionUtils.isAtLeast("1.20.4") ? Registry.EFFECT.get(key) : PotionEffectType.getByName(key.getKey().toUpperCase());
    }

    private record Version(String type, String version) {}

    private static Version v(String type, String version) {
        return new Version(type, version);
    }

    private static UberEffectsWrapper wrap(PotionEffectType effect, String name, String display, int id, int value) {
        return new UberEffectsWrapper(effect, name, UberLocale.get(display), id, value);
    }

    /**
     * For internal use.
     *
     * @param effect  PotionEffectType
     * @param name    String
     * @param display String
     * @param id      int
     * @param value   int
     * @hidden
     */
    UberEffects(PotionEffectType effect, String name, String display, int id, int value) {
        this.effect = effect;
        this.name = name;
        this.display = color(display, value);
        this.id = id;
        this.value = value;
    }

    /**
     * For internal use.
     *
     * @return PotionEffectType
     * @hidden
     */
    public PotionEffectType getEffect() {
        return effect;
    }

    /**
     * For internal use.
     *
     * @return String
     * @hidden
     */
    public String getName() {
        return name;
    }

    /**
     * For internal use.
     *
     * @return String
     * @hidden
     */
    public String getDisplayName() {
        return display;
    }

    /**
     * For internal use.
     *
     * @return int
     * @hidden
     */
    public int getId() {
        return id;
    }

    /**
     * For internal use.
     *
     * @return int
     * @hidden
     */
    public int getValue() {
        return value;
    }

    /**
     * For internal use.
     *
     * @param s String
     * @return UberEffects
     * @hidden
     */
    public static UberEffects byName(String s) {
        return values.values().stream().filter(e -> e.name.equalsIgnoreCase(s)).findFirst().orElse(null);
    }

    /**
     * Utility method for internal use.
     *
     * @param s String
     * @return UberEffects
     * @hidden
     */
    public static UberEffects get(String s) {
        return values.get(s);
    }

    /**
     * For internal use.
     *
     * @param type PotionEffectType
     * @return UberEffects
     * @hidden
     */
    public static UberEffects getByType(PotionEffectType type) {
        return values.values().stream().filter(e -> type.equals(e.effect)).findFirst().orElse(null);
    }

    /**
     * Utility method for internal use.
     *
     * @param v int
     * @return List
     * @hidden
     */
    public static List<UberEffects> ofValue(int v) {
        return values.values().stream().filter(e -> e.value == v).collect(Collectors.toList());
    }

    /**
     * Utility method for internal use.
     *
     * @param v int
     * @param effect PotionEffectType
     * @return Boolean
     * @hidden
     */
    public static boolean valuesContain(int v, PotionEffectType effect) {
        return values.values().stream().anyMatch(e -> e.value == v && e.effect.equals(effect));
    }

    private String color(String display, int value) {
        return switch (value) {
            case -1 -> "&c" + display;
            case 0 -> "&f" + display;
            case 1 -> "&9" + display;
            default -> display;
        };
    }

    /**
     * Utility method for internal use.
     *
     * @return UberEffects
     * @hidden
     */
    public static UberEffects[] values() {
        return values.values().toArray(UberEffects[]::new);
    }
}