package me.sciguymjm.uberenchant.enchantments.effects.armor;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.ArmorEffectEnchantment;

public class AbsorptionEnchantment extends ArmorEffectEnchantment {

    public AbsorptionEnchantment() {
        super("ABSORBTION");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }
}
