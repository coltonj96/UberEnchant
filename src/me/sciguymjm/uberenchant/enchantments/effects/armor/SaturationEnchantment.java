package me.sciguymjm.uberenchant.enchantments.effects.armor;

import me.sciguymjm.uberenchant.api.events.UberArmorEquippedEvent;
import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.ArmorEffectEnchantment;
import me.sciguymjm.uberenchant.enchantments.tasks.ArmorEffectChanceTask;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class SaturationEnchantment extends ArmorEffectEnchantment {

    public SaturationEnchantment() {
        super("SATURATION");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }

    @Override
    @EventHandler
    public void onEquip(UberArmorEquippedEvent event) {
        ItemStack item = event.getItem();
        if (!containsEnchantment(item))
            return;
        addTask(new ArmorEffectChanceTask(event.getPlayer(), this, event.getType()));
    }
}
