package me.sciguymjm.uberenchant.enchantments.effects.armor;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.ArmorEffectEnchantment;

public class HealthBoostEnchantment extends ArmorEffectEnchantment {

    public HealthBoostEnchantment() {
        super("HEALTH_BOOST");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.VERY_RARE;
    }
}
