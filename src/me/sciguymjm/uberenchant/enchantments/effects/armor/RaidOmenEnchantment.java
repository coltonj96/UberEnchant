package me.sciguymjm.uberenchant.enchantments.effects.armor;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.ArmorEffectEnchantment;

public class RaidOmenEnchantment extends ArmorEffectEnchantment {

    public RaidOmenEnchantment() {
        super("RAID_OMEN");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }
}
