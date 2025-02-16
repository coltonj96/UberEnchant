package me.sciguymjm.uberenchant.api.events;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Event that fires when a player equips armor with UberEnchantments on it
 */
public class UberArmorEquippedEvent extends PlayerEquipArmorEvent {

    /**
     * Fired when  a player equips armor with UberEnchantments
     *
     * @param player The player
     * @param item The armor
     * @param slot  Which slot the armor was equipped to
     */
    public UberArmorEquippedEvent(Player player, ItemStack item, int slot) {
        super(player, item, slot);
    }

    /**
     * Gets a map of all custom enchantments
     *
     * @return The map of enchantments
     */
    public Map<UberEnchantment, Integer> getEnchantments() {
        return UberEnchantment.getEnchantments(getItem());
    }
}
