package me.sciguymjm.uberenchant.api.events;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.MetaTag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MetaTagAddedEvent<T> extends MetaTagEvent<T> {

    public MetaTagAddedEvent(Player player, ItemStack item, UberEnchantment enchantment, MetaTag<T> tag, T value) {
        super(player, item, enchantment, tag, value);
    }
}
