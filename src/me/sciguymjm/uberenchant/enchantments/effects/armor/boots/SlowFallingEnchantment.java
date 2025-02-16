package me.sciguymjm.uberenchant.enchantments.effects.armor.boots;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.BootsEffectEnchantment;

public class SlowFallingEnchantment extends BootsEffectEnchantment {

    public SlowFallingEnchantment() {
        super("SLOW_FALLING");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.VERY_RARE;
    }
}
