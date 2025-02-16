package me.sciguymjm.uberenchant.enchantments.effects.armor;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.ArmorEffectEnchantment;

public class DolphinsGraceEnchantment extends ArmorEffectEnchantment {

    public DolphinsGraceEnchantment() {
        super("DOLPHINS_GRACE");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }
}
