package me.sciguymjm.uberenchant.enchantments.effects.armor;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.ArmorEffectEnchantment;

public class DamageResistanceEnchantment extends ArmorEffectEnchantment {

    public DamageResistanceEnchantment() {
        super("DAMAGE_RESISTANCE");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }
}
