package me.sciguymjm.uberenchant.utils;

import org.bukkit.potion.PotionEffectType;

/**
 * Effects enum for use in UberEnchant
 */
public enum UberEffects {


    /**
     * For internal use.
     */
    SPEED(PotionEffectType.SPEED, "speed", UberLocale.get("effects.speed"), 1, 1),

    /**
     * For internal use.
     */
    SLOW(PotionEffectType.SLOW, "slowness", UberLocale.get("effects.slow"), 2, -1),

    /**
     * For internal use.
     */
    FAST_DIGGING(PotionEffectType.FAST_DIGGING, "haste", UberLocale.get("effects.fast_digging"), 3, 1),

    /**
     * For internal use.
     */
    SLOW_DIGGING(PotionEffectType.SLOW_DIGGING, "miningfatigue", UberLocale.get("effects.slow_digging"), 4, -1),

    /**
     * For internal use.
     */
    INCREASE_DAMAGE(PotionEffectType.INCREASE_DAMAGE, "strength", UberLocale.get("effects.increase_damage"), 5, 1),

    /**
     * For internal use.
     */
    HEAL(PotionEffectType.HEAL, "instanthealth", UberLocale.get("effects.heal"), 6, 1),

    /**
     * For internal use.
     */
    HARM(PotionEffectType.HARM, "instantdamage", UberLocale.get("effects.harm"), 7, -1),

    /**
     * For internal use.
     */
    JUMP(PotionEffectType.JUMP, "jumpboost", UberLocale.get("effects.jump"), 8, 1),

    /**
     * For internal use.
     */
    CONFUSION(PotionEffectType.CONFUSION, "nausea", UberLocale.get("effects.confusion"), 9, -1),

    /**
     * For internal use.
     */
    REGENERATION(PotionEffectType.REGENERATION, "regeneration", UberLocale.get("effects.regeneration"), 10, 1),

    /**
     * For internal use.
     */
    DAMAGE_RESISTANCE(PotionEffectType.DAMAGE_RESISTANCE, "resistance", UberLocale.get("effects.damage_resistance"), 11, 1),

    /**
     * For internal use.
     */
    FIRE_RESISTANCE(PotionEffectType.FIRE_RESISTANCE, "fireresistance", UberLocale.get("effects.fire_resistance"), 12, 1),

    /**
     * For internal use.
     */
    WATER_BREATHING(PotionEffectType.WATER_BREATHING, "waterbreathing", UberLocale.get("effects.water_breathing"), 13, 1),

    /**
     * For internal use.
     */
    INVISIBILITY(PotionEffectType.INVISIBILITY, "invisibility", UberLocale.get("effects.invisibility"), 14, 1),

    /**
     * For internal use.
     */
    BLINDNESS(PotionEffectType.BLINDNESS, "blindness", UberLocale.get("effects.blindness"), 15, -1),

    /**
     * For internal use.
     */
    NIGHT_VISION(PotionEffectType.NIGHT_VISION, "nightvision", UberLocale.get("effects.night_vision"), 16, 1),

    /**
     * For internal use.
     */
    HUNGER(PotionEffectType.HUNGER, "hunger", UberLocale.get("effects.hunger"), 17, -1),

    /**
     * For internal use.
     */
    WEAKNESS(PotionEffectType.WEAKNESS, "weakness", UberLocale.get("effects.weakness"), 18, -1),

    /**
     * For internal use.
     */
    POISON(PotionEffectType.POISON, "poison", UberLocale.get("effects.poison"), 19, -1),

    /**
     * For internal use.
     */
    WITHER(PotionEffectType.WITHER, "wither", UberLocale.get("effects.wither"), 20, -1),

    /**
     * For internal use.
     */
    HEALTH_BOOST(PotionEffectType.HEALTH_BOOST, "healthboost", UberLocale.get("effects.health_boost"), 21, 1),

    /**
     * For internal use.
     */
    ABSORPTION(PotionEffectType.ABSORPTION, "absorption", UberLocale.get("effects.absorption"), 22, 1),

    /**
     * For internal use.
     */
    SATURATION(PotionEffectType.SATURATION, "saturation", UberLocale.get("effects.saturation"), 23, 1),

    /**
     * For internal use.
     */
    GLOWING(PotionEffectType.GLOWING, "glowing", UberLocale.get("effects.glowing"), 24, 0),

    /**
     * For internal use.
     */
    LEVITATION(PotionEffectType.LEVITATION, "levitation", UberLocale.get("effects.levitation"), 25, 0),

    /**
     * For internal use.
     */
    LUCK(PotionEffectType.LUCK, "luck", UberLocale.get("effects.luck"), 26, 1),

    /**
     * For internal use.
     */
    UNLUCK(PotionEffectType.UNLUCK, "badluck", UberLocale.get("effects.unluck"), 27, -1),

    /**
     * For internal use.
     */
    SLOW_FALLING(PotionEffectType.SLOW_FALLING, "slowfalling", UberLocale.get("effects.slow_falling"), 28, 1),

    /**
     * For internal use.
     */
    CONDUIT_POWER(PotionEffectType.CONDUIT_POWER, "conduitpower", UberLocale.get("effects.conduit_power"), 29, 1),

    /**
     * For internal use.
     */
    DOLPHINS_GRACE(PotionEffectType.DOLPHINS_GRACE, "dolphinsgrace", UberLocale.get("effects.dolphins_grace"), 30, 1),

    /**
     * For internal use.
     */
    BAD_OMEN(PotionEffectType.BAD_OMEN, "badomen", UberLocale.get("effects.bad_omen"), 31, 0),

    /**
     * For internal use.
     */
    HERO_OF_THE_VILLAGE(PotionEffectType.HERO_OF_THE_VILLAGE, "heroofthevillage", UberLocale.get("effects.hero_of_the_village"), 32, 1),

    /**
     * For internal use.
     */
    DARKNESS(PotionEffectType.DARKNESS, "darkness", UberLocale.get("effects.darkness"), 33, -1);

    private final PotionEffectType effect;
    private final String name;
    private final String display;
    private final int id;
    private final int value;

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
        for (UberEffects e : values()) {
            if (e.name().equalsIgnoreCase(s))
                return e;
        }
        return null;
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
        for (UberEffects e : values()) {
            if (type.equals(e.effect))
                return e;
        }
        return null;
    }

    private String color(String display, int value) {
        return switch (value) {
            case -1 -> "&c" + display;
            case 0 -> "&f" + display;
            case 1 -> "&9" + display;
            default -> display;
        };
    }
}