package me.sciguymjm.uberenchant.utils.enchanting;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.utils.FileUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.*;

public class EnchantmentTableEvents implements Listener {

    private Map<UUID, CustomOffer> players = new HashMap<>();
    private Map<UUID, Map<UberEnchantment, Integer>> books = new HashMap<>();
    public static List<String> enabled;

    public EnchantmentTableEvents() {
        enabled = FileUtils.loadConfig("/mechanics/enchantment_table.yml").getStringList("enabled_enchantments");
    }

    public static void reloadEnabled() {
        enabled = FileUtils.loadConfig("/mechanics/enchantment_table.yml").getStringList("enabled_enchantments");
    }

    public static boolean isEnabled(Enchantment enchant) {
        return enabled.contains(enchant.getKey().getKey());
    }

    @EventHandler
    public void onPrepare(PrepareItemEnchantEvent event) {
        UUID id = event.getEnchanter().getUniqueId();
        if (!EnchantmentTableUtils.seed.containsKey(id)) {
            EnchantmentTableUtils.seed.put(id, new Random().nextLong());
        }
        CustomOffer e = new CustomOffer(event.getEnchanter(), event.getItem(), event.getOffers(), event.getEnchantmentBonus(), -1);
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

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        UUID id = event.getEnchanter().getUniqueId();
        if (players.containsKey(id)) {
            Map<Enchantment, Integer> map = event.getEnchantsToAdd();
            int size = map.size() * 2;
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
            map.put(e.getOffer().getEnchantment(), e.getOffer().getEnchantmentLevel());
            if (event.getItem().getType().equals(Material.BOOK)) {
                books.put(id, cMap);
            } else {
                UberUtils.addEnchantments(cMap, event.getItem());
            }
            EnchantmentTableUtils.seed.put(id, new Random().nextLong());
        }
    }
}
