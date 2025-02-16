package me.sciguymjm.uberenchant.enchantments.abstraction;

import me.sciguymjm.uberenchant.api.events.UberArmorEquippedEvent;
import me.sciguymjm.uberenchant.enchantments.tasks.ArmorEffectTask;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public abstract class ArmorEffectEnchantment extends EffectEnchantment {

    public ArmorEffectEnchantment(String key) {
        super(key);
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ARMOR;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return EnchantmentTarget.ARMOR.includes(itemStack);
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
    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    @Override
    public String getTranslationKey() {
        return "";
    }

    @EventHandler
    public void onEquip(UberArmorEquippedEvent event) {
        ItemStack item = event.getItem();
        if (!containsEnchantment(item))
            return;
        addTask(new ArmorEffectTask(event.getPlayer(), this, event.getType()));
    }
}
