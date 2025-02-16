package me.sciguymjm.uberenchant.api.utils;

import me.sciguymjm.uberenchant.UberEnchant;
import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.events.PlayerEquipArmorEvent;
import me.sciguymjm.uberenchant.api.events.UberArmorEquippedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

/**
 * Utility class for internal use.
 */
public class ArmorEquippedListener implements Listener {

    private boolean change(ItemStack[] a, ItemStack[] b) {
        return !Arrays.equals(a, b);
    }

    private int slot(ItemStack[] a, ItemStack[] b) {
        int index = Arrays.mismatch(a, b);
        return index > -1 ? index + 36 : -1;
    }

    private ItemStack getItem(ItemStack[] a, ItemStack[] b, PlayerInventory inventory) {
        return inventory.getItem(slot(a, b));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player && event.getClickedInventory() instanceof PlayerInventory inventory) {
            fireEvents(player, inventory);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))
            fireEvents(event.getPlayer(), event.getPlayer().getInventory());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        new BukkitRunnable() {

            @Override
            public void run() {
                ItemStack[] armor = player.getInventory().getArmorContents();
                for (int n = 0; n < 4; n++) {
                    if (armor[n] != null) {
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerEquipArmorEvent(player, armor[n], n + 36));
                        if (UberEnchantment.hasEnchantments(armor[n]))
                            Bukkit.getServer().getPluginManager().callEvent(new UberArmorEquippedEvent(player, armor[n], n + 36));
                    }
                }
            }
        }.runTaskLater(UberEnchant.instance(), 20L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        new BukkitRunnable() {

            @Override
            public void run() {
                ItemStack[] armor = player.getInventory().getArmorContents();
                for (int n = 0; n < 4; n++) {
                    if (armor[n] != null) {
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerEquipArmorEvent(player, armor[n], n + 36));
                        if (UberEnchantment.hasEnchantments(armor[n]))
                            Bukkit.getServer().getPluginManager().callEvent(new UberArmorEquippedEvent(player, armor[n], n + 36));
                    }
                }
            }
        }.runTask(UberEnchant.instance());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onReload(ServerLoadEvent event) {
        if (event.getType().equals(ServerLoadEvent.LoadType.RELOAD)) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        ItemStack[] armor = player.getInventory().getArmorContents();
                        for (int n = 0; n < 4; n++) {
                            if (armor[n] != null) {
                                Bukkit.getServer().getPluginManager().callEvent(new PlayerEquipArmorEvent(player, armor[n], n + 36));
                                if (UberEnchantment.hasEnchantments(armor[n]))
                                    Bukkit.getServer().getPluginManager().callEvent(new UberArmorEquippedEvent(player, armor[n], n + 36));
                            }
                        }
                    });
                }
            }.runTask(UberEnchant.instance());
        }
    }

    private void fireEvents(Player player, PlayerInventory inventory) {
        final ItemStack[] a = inventory.getArmorContents();
        new BukkitRunnable() {
            @Override
            public void run() {
                final ItemStack[] b = inventory.getArmorContents();
                if (change(a, b)) {
                    ItemStack item = getItem(a, b, inventory);
                    int slot = slot(a, b);
                    if (item != null) {
                        Bukkit.getServer().getPluginManager().callEvent(new PlayerEquipArmorEvent(player, item, slot));
                        if (UberEnchantment.hasEnchantments(item))
                            Bukkit.getServer().getPluginManager().callEvent(new UberArmorEquippedEvent(player, item, slot));
                    }
                }
            }
        }.runTask(UberEnchant.instance());
    }
}
