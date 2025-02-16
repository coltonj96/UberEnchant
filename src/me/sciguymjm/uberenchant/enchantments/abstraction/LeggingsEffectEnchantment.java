package me.sciguymjm.uberenchant.enchantments.abstraction;

import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public abstract class LeggingsEffectEnchantment extends ArmorEffectEnchantment {

    public LeggingsEffectEnchantment(String key) {
        super(key);
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ARMOR_LEGS;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return EnchantmentTarget.ARMOR_LEGS.includes(itemStack);
    }
}
