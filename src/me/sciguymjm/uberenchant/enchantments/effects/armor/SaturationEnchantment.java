package me.sciguymjm.uberenchant.enchantments.effects.armor;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.BoolTag;
import me.sciguymjm.uberenchant.enchantments.abstraction.ArmorEffectEnchantment;

public class SaturationEnchantment extends ArmorEffectEnchantment {

    public SaturationEnchantment() {
        super("SATURATION");
        setTag(BoolTag.HAS_CHANCE, true);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }
}
