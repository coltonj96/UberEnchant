package me.sciguymjm.uberenchant.enchantments.effects.armor;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.ArmorEffectEnchantment;

public class FireResistanceEnchantment extends ArmorEffectEnchantment {

    public FireResistanceEnchantment() {
        super("FIRE_RESISTANCE");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.VERY_RARE;
    }
}
