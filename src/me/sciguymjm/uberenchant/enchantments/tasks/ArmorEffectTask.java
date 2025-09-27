package me.sciguymjm.uberenchant.enchantments.tasks;

import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ArmorEffectTask extends EffectTask {

    protected Player player;
    protected EquipmentSlot slot;

    public ArmorEffectTask(Player player, EffectEnchantment enchantment, EquipmentSlot slot, Conditional conditional) {
        super(player, enchantment, conditional);
        this.player = player;
        this.slot = slot;
    }

    @Override
    public ItemStack getItem() {
        return player.getInventory().getItem(slot);
    }
}
