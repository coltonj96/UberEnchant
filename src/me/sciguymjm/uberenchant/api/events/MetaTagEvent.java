package me.sciguymjm.uberenchant.api.events;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.MetaTag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MetaTagEvent<T> extends UberEvent {

    private Player player;
    private ItemStack item;
    private UberEnchantment enchantment;
    private MetaTag<T> tag;
    private T value;

    public MetaTagEvent(Player player, ItemStack item, UberEnchantment enchantment, MetaTag<T> tag, T value) {
        super();
        this.player = player;
        this.item = item;
        this.enchantment = enchantment;
        this.tag = tag;
        this.value = value;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return item;
    }

    public UberEnchantment getEnchantment() {
        return enchantment;
    }

    public MetaTag<T> getTag() {
        return tag;
    }

    public T getValue() {
        return value;
    }
}
