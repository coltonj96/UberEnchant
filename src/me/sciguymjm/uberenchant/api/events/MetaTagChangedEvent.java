package me.sciguymjm.uberenchant.api.events;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.MetaTag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MetaTagChangedEvent<T> extends MetaTagEvent<T> {

    private T old;

    public MetaTagChangedEvent(Player player, ItemStack item, UberEnchantment enchantment, MetaTag<T> tag, T old, T value) {
        super(player, item, enchantment, tag, value);
        this.old = old;
    }

    public T getOldValue() {
        return old;
    }
}
