package me.sciguymjm.uberenchant.enchantments.effects.armor;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.ArmorEffectEnchantment;

public class ConduitPowerEnchantment extends ArmorEffectEnchantment {

    public ConduitPowerEnchantment() {
        super("CONDUIT_POWER");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }
}
