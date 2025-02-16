package me.sciguymjm.uberenchant.enchantments.effects.armor.helmet;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.HelmetEffectEnchantment;

public class WaterBreathingEnchantment extends HelmetEffectEnchantment {

    public WaterBreathingEnchantment() {
        super("WATER_BREATHING");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.VERY_RARE;
    }

}
