package me.sciguymjm.uberenchant.enchantments.effects.armor;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.ArmorEffectEnchantment;

public class TrialOmenEnchantment extends ArmorEffectEnchantment {

    public TrialOmenEnchantment() {
        super("TRIAL_OMEN");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }
}
