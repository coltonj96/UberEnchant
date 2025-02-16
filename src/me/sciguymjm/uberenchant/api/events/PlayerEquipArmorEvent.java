package me.sciguymjm.uberenchant.api.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

/**
 * Event that fires when a player equips armor
 */
public class PlayerEquipArmorEvent extends UberEvent {

    private Player player;
    private ItemStack item;
    private int slot;

    /**
     * Fired when  a player equips armor
     *
     * @param player The player
     * @param item The armor
     * @param slot  Which slot the armor was equipped to
     */
    public PlayerEquipArmorEvent(Player player, ItemStack item, int slot) {
        super();
        this.player = player;
        this.item = item;
        this.slot = slot;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getSlot() {
        return slot;
    }

    public EquipmentSlot getType() {
        return switch (slot) {
            case 36 -> EquipmentSlot.FEET;
            case 37 -> EquipmentSlot.LEGS;
            case 38 -> EquipmentSlot.CHEST;
            case 39 -> EquipmentSlot.HEAD;
            default -> null;
        };
    }
}
