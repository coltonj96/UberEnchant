package me.sciguymjm.uberenchant.enchantments.effects.armor;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.ArmorEffectEnchantment;

public class InvisibilityEnchantment extends ArmorEffectEnchantment {

    public InvisibilityEnchantment() {
        super("INVISIBILITY");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.VERY_RARE;
    }
}
