package me.sciguymjm.uberenchant.enchantments.abstraction;

import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public abstract class BootsEffectEnchantment extends ArmorEffectEnchantment {

    public BootsEffectEnchantment(String key) {
        super(key);
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ARMOR_FEET;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return EnchantmentTarget.ARMOR_FEET.includes(itemStack);
    }
}
