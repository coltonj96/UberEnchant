package me.sciguymjm.uberenchant.api.events;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.MetaTag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MetaTagRemovedEvent<T> extends MetaTagEvent<T> {

    public MetaTagRemovedEvent(Player player, ItemStack item, UberEnchantment enchantment, MetaTag<T> tag, T value) {
        super(player, item, enchantment, tag, value);
    }
}
