package me.sciguymjm.uberenchant.utils.enchanting;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentUtils; // Or me.sciguymjm.uberenchant.api.utils.UberUtils
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.AnvilInventory; // Bukkit uses AnvilInventory for Grindstones in PrepareGrindstoneEvent
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GrindstoneEvents implements Listener {

    @EventHandler
    public void onPrepareGrindstone(PrepareGrindstoneEvent event) {
        AnvilInventory inventory = (AnvilInventory) event.getInventory(); // Cast to AnvilInventory
        ItemStack sourceItem = inventory.getItem(0);
        ItemStack sacrificeItem = inventory.getItem(1); // May be null
        ItemStack resultItem = event.getResult();

        // Check if there's a result item to process
        if (resultItem == null || resultItem.getType() == Material.AIR) {
            return;
        }

        // The core logic is to ensure that if vanilla Minecraft produces a non-enchanted item,
        // our custom enchantments are also stripped.
        // Vanilla grindstone operations that result in a non-enchanted item include:
        // 1. Disenchanting a single enchanted item (sourceItem has enchants, sacrificeItem is null).
        // 2. Combining two items where the result is stripping enchants (e.g., two enchanted items, or one enchanted and one not).
        // 3. Using a regular book as a sacrifice to get XP (sourceItem has enchants, sacrificeItem is a non-enchanted book, resultItem is a non-enchanted version of sourceItem or a plain book).

        // We don't need to check the specific operation type as long as there's a resultItem.
        // If vanilla determined a result, we just need to ensure custom enchants are gone from it.

        ItemStack modifiedResult = resultItem.clone();

        // Assuming EnchantmentUtils.getAllEnchantmentsMap gets all enchantments (regular and custom)
        // from an item, including stored enchantments if it's a book.
        // Fallback could be UberUtils.getAllMap(modifiedResult)
        Map<Enchantment, Integer> allEnchantments = EnchantmentUtils.getAllEnchantmentsMap(modifiedResult);

        if (allEnchantments == null || allEnchantments.isEmpty()) {
            // No enchantments to process on the result item.
            // This can happen if the result is already non-enchanted by vanilla logic.
            // Or if getAllEnchantmentsMap returned null/empty for some reason.
            // We might still want to ensure no custom enchants remain if the item *itself*
            // could have them directly (not through its meta), but that's less common.
            // For now, if the map is empty, we assume no custom enchants are present on the result.
            return;
        }

        Map<Enchantment, Integer> filteredEnchantments = new HashMap<>(allEnchantments);
        boolean modified = false;

        // Iterate over a copy of keys to avoid ConcurrentModificationException
        for (Enchantment enchantment : new HashMap<>(filteredEnchantments).keySet()) {
            if (enchantment instanceof UberEnchantment) {
                filteredEnchantments.remove(enchantment);
                modified = true;
            }
        }

        if (modified) {
            // Apply the filtered enchantments back.
            // EnchantmentUtils.setEnchantments should ideally handle both normal items and books (stored enchants).
            // If it doesn't, we might need separate calls.
            if (modifiedResult.getType() == Material.ENCHANTED_BOOK) {
                EnchantmentUtils.setStoredEnchantments(filteredEnchantments, modifiedResult);
            } else {
                EnchantmentUtils.setEnchantments(filteredEnchantments, modifiedResult);
            }
            event.setResult(modifiedResult);
        }
        // If not modified, the original resultItem from the event is fine.
    }
}
