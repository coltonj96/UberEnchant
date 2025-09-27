package me.sciguymjm.uberenchant.enchantments.effects.armor;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.BoolTag;
import me.sciguymjm.uberenchant.enchantments.abstraction.ArmorEffectEnchantment;

public class HealEnchantment extends ArmorEffectEnchantment {

    public HealEnchantment() {
        super("HEAL");
        setTag(BoolTag.HAS_CHANCE, true);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.VERY_RARE;
    }
}