package me.sciguymjm.uberenchant.api.events;

import org.bukkit.event.*;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryEvent;

/**
 * Unused
 */
public class UberEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    /**
     * Unused
     */
    protected boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
