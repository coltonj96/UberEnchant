package me.sciguymjm.uberenchant.enchantments.abstraction;

import me.sciguymjm.uberenchant.utils.UberEffects;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

/**
 *
 */
public class EffectEnchantmentWrapper extends EffectEnchantment {

    private final UberEffects a;

    /**
     * For internal use
     *
     * @param effect UberEffects
     * @hidden
     */
    public EffectEnchantmentWrapper(UberEffects effect) {
        super(effect.getName());
        a = effect;
    }

    @Override
    public UberEffects getEffect() {
        return a;
    }

    @Override
    public int getMaxLevel() {
        //a.getEffect();
        return 10;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @SuppressWarnings("deprecation")
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        if (UberEffects.valuesContain(1, a.getEffect())) {
            return EnchantmentTarget.ARMOR.includes(item) || item.getType().equals(Material.SHIELD);
        } else if (UberEffects.valuesContain(-1, a.getEffect())) {
            return EnchantmentTarget.ARMOR.includes(item) || EnchantmentTarget.WEAPON.includes(item);
        } else if (UberEffects.valuesContain(0, a.getEffect())) {
            return true;
        }
        return false;
    }

}
