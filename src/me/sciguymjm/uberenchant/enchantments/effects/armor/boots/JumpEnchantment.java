package me.sciguymjm.uberenchant.enchantments.effects.armor.boots;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.BootsEffectEnchantment;

public class JumpEnchantment extends BootsEffectEnchantment {

    public JumpEnchantment() {
        super("JUMP");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }
}
