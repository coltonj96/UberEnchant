package me.sciguymjm.uberenchant.enchantments.tasks;

import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public interface Conditional {

    boolean test(LivingEntity entity, ItemStack item, EffectEnchantment enchantment);
}
