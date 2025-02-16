package me.sciguymjm.uberenchant.enchantments.abstraction;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.utils.UberEffects;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class EffectEnchantmentWrapper extends UberEnchantment {

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
        if (UberEffects.valuesContain(1, a.getEffect()))
            return EnchantmentTarget.ARMOR.includes(item) || item.getType().equals(Material.SHIELD);
        else if (UberEffects.valuesContain(-1, a.getEffect()))
            return EnchantmentTarget.ARMOR.includes(item) || EnchantmentTarget.WEAPON.includes(item);
        else
            return UberEffects.valuesContain(0, a.getEffect());
    }

    @Override
    public String getTranslationKey() {
        return "";
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public Rarity getRarity() {
        return null;
    }

    @Override
    public String getPermission() {
        return "";
    }
}
