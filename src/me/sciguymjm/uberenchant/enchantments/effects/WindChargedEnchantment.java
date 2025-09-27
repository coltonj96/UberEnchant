package me.sciguymjm.uberenchant.enchantments.effects;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class WindChargedEnchantment extends EffectEnchantment {

    public WindChargedEnchantment() {
        super("WIND_CHARGED");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.WEAPON;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return EnchantmentTarget.WEAPON.includes(itemStack);
    }
}
