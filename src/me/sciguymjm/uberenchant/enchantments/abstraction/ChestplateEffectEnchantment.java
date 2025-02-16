package me.sciguymjm.uberenchant.enchantments.abstraction;

import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public abstract class ChestplateEffectEnchantment extends ArmorEffectEnchantment {

    public ChestplateEffectEnchantment(String key) {
        super(key);
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ARMOR_TORSO;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return EnchantmentTarget.ARMOR_TORSO.includes(itemStack);
    }
}
