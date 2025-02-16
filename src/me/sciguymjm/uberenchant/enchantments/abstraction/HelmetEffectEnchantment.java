package me.sciguymjm.uberenchant.enchantments.abstraction;

import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public abstract class HelmetEffectEnchantment extends ArmorEffectEnchantment {

    public HelmetEffectEnchantment(String key) {
        super(key);
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ARMOR_HEAD;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return EnchantmentTarget.ARMOR_HEAD.includes(itemStack);
    }
}
