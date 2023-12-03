package me.sciguymjm.uberenchant.utils;

import org.bukkit.potion.PotionEffectType;

public class UberEffectsWrapper extends UberEffects {
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
    public UberEffectsWrapper(PotionEffectType effect, String name, String display, int id, int value) {
        super(effect, name, display, id, value);
    }
}
