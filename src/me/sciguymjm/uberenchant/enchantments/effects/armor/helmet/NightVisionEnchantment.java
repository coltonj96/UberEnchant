package me.sciguymjm.uberenchant.enchantments.effects.armor.helmet;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.HelmetEffectEnchantment;

public class NightVisionEnchantment extends HelmetEffectEnchantment {

    public NightVisionEnchantment() {
        super("NIGHT_VISION");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.VERY_RARE;
    }
}
