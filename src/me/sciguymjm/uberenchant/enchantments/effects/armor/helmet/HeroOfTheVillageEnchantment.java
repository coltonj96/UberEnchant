package me.sciguymjm.uberenchant.enchantments.effects.armor.helmet;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.HelmetEffectEnchantment;

public class HeroOfTheVillageEnchantment extends HelmetEffectEnchantment {

    public HeroOfTheVillageEnchantment() {
        super("HERO_OF_THE_VILLAGE");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.VERY_RARE;
    }
}
