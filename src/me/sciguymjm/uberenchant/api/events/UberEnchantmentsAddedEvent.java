package me.sciguymjm.uberenchant.api.events;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class UberEnchantmentsAddedEvent extends UberEvent {

    private Player player;
    private ItemStack item;
    private Map<Enchantment, Integer> enchantments;

    public UberEnchantmentsAddedEvent(Player player, ItemStack item, Map<Enchantment, Integer> enchantments) {
        super();
        this.player = player;
        this.item = item;
        this.enchantments = enchantments;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return item;
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }
}
