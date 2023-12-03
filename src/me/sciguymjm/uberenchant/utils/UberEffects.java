package me.sciguymjm.uberenchant.utils;

import org.bukkit.potion.PotionEffectType;

import java.util.*;
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

        values.put("SPEED", new UberEffectsWrapper(PotionEffectType.SPEED, "speed", UberLocale.get("effects.speed"), 1, 1));
        values.put("SLOW", new UberEffectsWrapper(PotionEffectType.SLOW, "slowness", UberLocale.get("effects.slow"), 2, -1));
        values.put("FAST_DIGGING", new UberEffectsWrapper(PotionEffectType.FAST_DIGGING, "haste", UberLocale.get("effects.fast_digging"), 3, 1));
        values.put("SLOW_DIGGING", new UberEffectsWrapper(PotionEffectType.SLOW_DIGGING, "miningfatigue", UberLocale.get("effects.slow_digging"), 4, -1));
        values.put("INCREASE_DAMAGE", new UberEffectsWrapper(PotionEffectType.INCREASE_DAMAGE, "strength", UberLocale.get("effects.increase_damage"), 5, 1));
        values.put("HEAL", new UberEffectsWrapper(PotionEffectType.HEAL, "instanthealth", UberLocale.get("effects.heal"), 6, 1));
        values.put("HARM", new UberEffectsWrapper(PotionEffectType.HARM, "instantdamage", UberLocale.get("effects.harm"), 7, -1));
        values.put("JUMP", new UberEffectsWrapper(PotionEffectType.JUMP, "jumpboost", UberLocale.get("effects.jump"), 8, 1));
        values.put("CONFUSION", new UberEffectsWrapper(PotionEffectType.CONFUSION, "nausea", UberLocale.get("effects.confusion"), 9, -1));
        values.put("REGENERATION", new UberEffectsWrapper(PotionEffectType.REGENERATION, "regeneration", UberLocale.get("effects.regeneration"), 10, 1));
        values.put("DAMAGE_RESISTANCE", new UberEffectsWrapper(PotionEffectType.DAMAGE_RESISTANCE, "resistance", UberLocale.get("effects.damage_resistance"), 11, 1));
        values.put("FIRE_RESISTANCE", new UberEffectsWrapper(PotionEffectType.FIRE_RESISTANCE, "fireresistance", UberLocale.get("effects.fire_resistance"), 12, 1));
        values.put("WATER_BREATHING", new UberEffectsWrapper(PotionEffectType.WATER_BREATHING, "waterbreathing", UberLocale.get("effects.water_breathing"), 13, 1));
        values.put("INVISIBILITY", new UberEffectsWrapper(PotionEffectType.INVISIBILITY, "invisibility", UberLocale.get("effects.invisibility"), 14, 1));
        values.put("BLINDNESS", new UberEffectsWrapper(PotionEffectType.BLINDNESS, "blindness", UberLocale.get("effects.blindness"), 15, -1));
        values.put("NIGHT_VISION", new UberEffectsWrapper(PotionEffectType.NIGHT_VISION, "nightvision", UberLocale.get("effects.night_vision"), 16, 1));
        values.put("HUNGER", new UberEffectsWrapper(PotionEffectType.HUNGER, "hunger", UberLocale.get("effects.hunger"), 17, -1));
        values.put("WEAKNESS", new UberEffectsWrapper(PotionEffectType.WEAKNESS, "weakness", UberLocale.get("effects.weakness"), 18, -1));
        values.put("POISON", new UberEffectsWrapper(PotionEffectType.POISON, "poison", UberLocale.get("effects.poison"), 19, -1));
        values.put("WITHER", new UberEffectsWrapper(PotionEffectType.WITHER, "wither", UberLocale.get("effects.wither"), 20, -1));
        values.put("HEALTH_BOOST", new UberEffectsWrapper(PotionEffectType.HEALTH_BOOST, "healthboost", UberLocale.get("effects.health_boost"), 21, 1));
        values.put("ABSORPTION", new UberEffectsWrapper(PotionEffectType.ABSORPTION, "absorption", UberLocale.get("effects.absorption"), 22, 1));
        values.put("SATURATION", new UberEffectsWrapper(PotionEffectType.SATURATION, "saturation", UberLocale.get("effects.saturation"), 23, 1));
        values.put("GLOWING", new UberEffectsWrapper(PotionEffectType.GLOWING, "glowing", UberLocale.get("effects.glowing"), 24, 0));
        values.put("LEVITATION", new UberEffectsWrapper(PotionEffectType.LEVITATION, "levitation", UberLocale.get("effects.levitation"), 25, 0));
        values.put("LUCK", new UberEffectsWrapper(PotionEffectType.LUCK, "luck", UberLocale.get("effects.luck"), 26, 1));
        values.put("UNLUCK", new UberEffectsWrapper(PotionEffectType.UNLUCK, "badluck", UberLocale.get("effects.unluck"), 27, -1));
        values.put("SLOW_FALLING", new UberEffectsWrapper(PotionEffectType.SLOW_FALLING, "slowfalling", UberLocale.get("effects.slow_falling"), 28, 1));
        values.put("CONDUIT_POWER", new UberEffectsWrapper(PotionEffectType.CONDUIT_POWER, "conduitpower", UberLocale.get("effects.conduit_power"), 29, 1));
        values.put("DOLPHINS_GRACE", new UberEffectsWrapper(PotionEffectType.DOLPHINS_GRACE, "dolphinsgrace", UberLocale.get("effects.dolphins_grace"), 30, 1));
        values.put("BAD_OMEN", new UberEffectsWrapper(PotionEffectType.BAD_OMEN, "badomen", UberLocale.get("effects.bad_omen"), 31, -1));
        values.put("HERO_OF_THE_VILLAGE", new UberEffectsWrapper(PotionEffectType.HERO_OF_THE_VILLAGE, "heroofthevillage", UberLocale.get("effects.hero_of_the_village"), 32, 1));

        if (VersionUtils.isAtLeast("1.19")) {
            values.put("DARKNESS", new UberEffectsWrapper(PotionEffectType.DARKNESS, "darkness", UberLocale.get("effects.darkness"), 33, -1));
        }
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
     * For internal use.
     *
     * @param type PotionEffectType
     * @return UberEffects
     * <p>
     * For internal use.
     */
    public static UberEffects getByType(PotionEffectType type) {
        return values.values().stream().filter(e -> type.equals(e.effect)).findFirst().orElse(null);
    }

    public static List<UberEffects> ofValue(int v) {
        return values.values().stream().filter(e -> e.value == v).collect(Collectors.toList());
    }

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

    public static UberEffects[] values() {
        return values.values().toArray(UberEffects[]::new);
    }
}