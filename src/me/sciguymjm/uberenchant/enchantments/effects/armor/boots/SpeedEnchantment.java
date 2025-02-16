package me.sciguymjm.uberenchant.enchantments.effects.armor.boots;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.BootsEffectEnchantment;

public class SpeedEnchantment extends BootsEffectEnchantment {

    public SpeedEnchantment() {
        super("SPEED");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.VERY_RARE;
    }
}
