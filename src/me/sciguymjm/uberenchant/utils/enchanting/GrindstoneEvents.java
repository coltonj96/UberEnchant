package me.sciguymjm.uberenchant.utils.enchanting;

import me.sciguymjm.uberenchant.UberEnchant;
import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.UberUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Random;
import java.util.function.ToIntFunction;

/*
    Credit for original code by https://github.com/memersmaster
*/

public class GrindstoneEvents implements Listener {

    @EventHandler
    public void onPrepareGrindstone(PrepareGrindstoneEvent event) {
        GrindstoneInventory inventory = event.getInventory();
        ItemStack slot1 = inventory.getItem(0);
        ItemStack slot2 = inventory.getItem(1);
        ItemStack result = getResult(slot1, slot2, event.getResult());

        if (empty(result))
            return;

        int xp = getExp(slot1, slot2);

        if (xp > 0) {
            ItemStack item = result.clone();
            if (UberUtils.hasStoredData(item))
                for (UberEnchantment enchant : UberUtils.getStoredMap(item).keySet())
                    EnchantmentUtils.removeStoredEnchantment(enchant, item);
            if (UberUtils.hasData(item))
                for (UberEnchantment enchant : UberUtils.getMap(item).keySet())
                    EnchantmentUtils.removeEnchantment(enchant, item);
            if (item.getType().equals(Material.ENCHANTED_BOOK))
                item.setType(Material.BOOK);
            event.setResult(item);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null)
            return;
        if (!event.getView().getTopInventory().getType().equals(InventoryType.GRINDSTONE))
            return;

        InventoryView view = event.getView();
        GrindstoneInventory inventory = (GrindstoneInventory) view.getTopInventory();

        if (event.getClickedInventory().getType().equals(InventoryType.GRINDSTONE)) {
            if (event.getSlot() == 2 && empty(event.getCursor())) {
                ItemStack slot1 = inventory.getItem(0);
                ItemStack slot2 = inventory.getItem(1);

                int xp = getExp(slot1, slot2);

                if (xp > 0)
                    ((Player) view.getPlayer()).giveExp(xp);

                return;
            }
            if (event.getSlot() != 2 && !event.isShiftClick()) {
                int slot = event.getSlot();
                ItemStack cursor = event.getCursor();
                ItemStack current = view.getItem(slot);
                if (empty(cursor) && !empty(current))
                    return;
                view.setCursor(current);
                new BukkitRunnable() {
                    public void run() {
                        view.setItem(slot, cursor);
                    }
                }.runTask(UberEnchant.instance());
                return;
            }
        }

        if (event.isShiftClick()) {
            if (inventory.firstEmpty() == 0) {
                int slot = event.getSlot();
                ItemStack current = event.getCurrentItem();
                if (empty(current))
                    return;
                if (!current.getEnchantments().isEmpty())
                    return;
                if (!UberUtils.hasData(current) && !UberUtils.hasStoredData(current))
                    return;
                inventory.setItem(0, current);
                event.getClickedInventory().setItem(slot, null);
            }
        }
    }

    private ItemStack getResult(ItemStack slot1, ItemStack slot2, ItemStack result) {
        if (empty(result)) {
            if (!empty(slot1) && (UberUtils.hasData(slot1) || UberUtils.hasStoredData(slot1))&& empty(slot2))
                result = slot1.clone();
            if (!empty(slot2) && (UberUtils.hasData(slot2) || UberUtils.hasStoredData(slot2)) && empty(slot1))
                result = slot2.clone();
        }
        return result;
    }

    private boolean empty(ItemStack item) {
        return item == null || item.getType().isAir();
    }

    private int getExp(ItemStack... items) {
        int xp = 0;

        for (ItemStack item : items)
            if (item != null)
                xp += getItemExp(item);

        if (xp > 0) {
            int n = (int) Math.ceil((double) xp / 2.0D);
            return n + new Random().nextInt(n);
        }

        return xp;
    }

    private int getItemExp(ItemStack item) {
        int xp = 0;
        ToIntFunction<Map.Entry<UberEnchantment, Integer>> map = entry -> {
            if (entry.getKey().isCursed())
                return 0;
            return EnchantmentTableUtils.minCost(entry.getValue(), entry.getKey());
        };
        if (UberUtils.hasData(item))
            xp += UberUtils.getMap(item).entrySet().stream().mapToInt(map).sum();
        if (UberUtils.hasStoredData(item))
            xp += UberUtils.getStoredMap(item).entrySet().stream().mapToInt(map).sum();
        return xp;
    }
}