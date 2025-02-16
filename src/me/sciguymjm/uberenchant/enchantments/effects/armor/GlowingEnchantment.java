package me.sciguymjm.uberenchant.enchantments.effects.armor;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.ArmorEffectEnchantment;

public class GlowingEnchantment extends ArmorEffectEnchantment {

    public GlowingEnchantment() {
        super("GLOWING");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }
}
