package me.sciguymjm.uberenchant.enchantments.effects.armor.helmet;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.HelmetEffectEnchantment;

public class BreathOfTheNautilusEnchantment extends HelmetEffectEnchantment {

    public BreathOfTheNautilusEnchantment() {
        super("BREATH_OF_THE_NAUTILUS");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.VERY_RARE;
    }

}
