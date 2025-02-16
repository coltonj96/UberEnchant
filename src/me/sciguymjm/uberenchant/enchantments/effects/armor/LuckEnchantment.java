package me.sciguymjm.uberenchant.enchantments.effects.armor;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.ArmorEffectEnchantment;

public class LuckEnchantment extends ArmorEffectEnchantment {

    public LuckEnchantment() {
        super("LUCK");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.COMMON;
    }
}
