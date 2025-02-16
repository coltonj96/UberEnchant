package me.sciguymjm.uberenchant.utils.enchanting;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.events.UberEnchantmentsAddedEvent;
import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.*;

/**
 * Utility class for internal use.
 */
public class EnchantmentTableEvents implements Listener {

    private Map<UUID, CustomOffer> players = new HashMap<>();
    private Map<UUID, Map<UberEnchantment, Integer>> books = new HashMap<>();
    public static List<String> disabled;

    public EnchantmentTableEvents() {
        reloadEnabled();
    }

    public static void reloadEnabled() {
        disabled = FileUtils.loadConfig("/mechanics/enchantment_table.yml").getStringList("disabled_enchantments");
        if (FileUtils.get("/mechanics/enchantment_table.yml", "disable_effect_enchantments", true, Boolean.class))
            disabled.addAll(UberEnchantment.getRegisteredEnchantments().stream().map(e -> e.getKey().getKey()).toList());
    }

    public static boolean isDisabled(Enchantment enchant) {
        return disabled.contains(enchant.getKey().getKey());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepare(PrepareItemEnchantEvent event) {
        UUID id = event.getEnchanter().getUniqueId();
        if (!EnchantmentTableUtils.seed.containsKey(id))
            EnchantmentTableUtils.seed.put(id, new Random().nextLong());
        CustomOffer e  = new CustomOffer(event, -1);
        players.put(id, e);
        if (event.getItem().getType().equals(Material.ENCHANTED_BOOK) && books.containsKey(id)) {
            UberUtils.addStoredEnchantments(books.get(id), event.getItem());
            books.remove(id);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory().getType().equals(InventoryType.ENCHANTING)) {
            players.remove(event.getPlayer().getUniqueId());
            books.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEnchant(EnchantItemEvent event) {
        UUID id = event.getEnchanter().getUniqueId();
        if (players.containsKey(id)) {
            Map<Enchantment, Integer> map = event.getEnchantsToAdd();
            int size = map.size() * 2;
            if (size > 5)
                size = 5;
            map.clear();
            CustomOffer e = players.get(event.getEnchanter().getUniqueId());
            e.setSlot(event.whichButton());
            players.get(id).setHasEnchanted(true);
            EnchantmentTableUtils.CustomList list = e.getList();
            List<EnchantmentTableUtils.WeightedEnchantment> vList = list.vanilla();
            List<EnchantmentTableUtils.WeightedEnchantment> cList = list.custom();
            Map<UberEnchantment, Integer> cMap = new HashMap<>();
            for (int n = 0; n < size; n++) {
                if (!vList.isEmpty() && n < vList.size()) {
                    EnchantmentTableUtils.WeightedEnchantment enchant = list.vanilla().get(n);
                    map.put(enchant.getEnchantment(), enchant.getLevel());
                }
                if (!cList.isEmpty() && n < cList.size()) {
                    EnchantmentTableUtils.WeightedEnchantment enchant = list.custom().get(n);
                    if (enchant.getEnchantment() instanceof UberEnchantment)
                        cMap.put((UberEnchantment) enchant.getEnchantment(), enchant.getLevel());
                }
            }
            players.remove(id);
            if (e.getOffer() != null)
                map.put(e.getOffer().getEnchantment(), e.getOffer().getEnchantmentLevel());
            else
                map.put(event.getEnchantmentHint(), event.getLevelHint());
            if (event.getItem().getType().equals(Material.BOOK)) {
                books.put(id, cMap);
            } else {
                UberUtils.addEnchantments(cMap, event.getItem());
            }
            EnchantmentTableUtils.seed.put(id, new Random().nextLong());
            Map<Enchantment, Integer> big = new HashMap<>(map);
            big.putAll(cMap);
            UberEnchantmentsAddedEvent ce = new UberEnchantmentsAddedEvent(event.getEnchanter(), event.getItem(), big);
            Bukkit.getServer().getPluginManager().callEvent(ce);
        }
    }
}
