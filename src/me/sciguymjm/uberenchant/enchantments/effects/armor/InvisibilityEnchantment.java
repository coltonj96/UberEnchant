package me.sciguymjm.uberenchant.enchantments.effects.armor;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.BoolTag;
import me.sciguymjm.uberenchant.enchantments.abstraction.ArmorEffectEnchantment;

public class InvisibilityEnchantment extends ArmorEffectEnchantment {

    public InvisibilityEnchantment() {
        super("INVISIBILITY");
        setTag(BoolTag.ON_HELD, true);
    }

    @Override
    public Rarity getRarity()    {
        return Rarity.VERY_RARE;
    }
}
