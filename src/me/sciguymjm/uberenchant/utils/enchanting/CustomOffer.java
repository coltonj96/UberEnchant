package me.sciguymjm.uberenchant.utils.enchanting;

import me.sciguymjm.uberenchant.api.utils.random.UberRandom;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CustomOffer {
    private boolean enchanted = false;
    private int slot;
    private int[] costs = new int[3];
    private EnchantmentOffer[] offers;
    private Player player;
    private ItemStack item;

    private EnchantmentTableUtils.CustomList list;

    public void setEnchanted() {
        enchanted = true;
    }

    public boolean isEnchanted() {
        return enchanted;
    }

    public CustomOffer(Player enchanter, ItemStack item, EnchantmentOffer[] offers, int bonus, int button) {
        UberRandom random = new UberRandom(EnchantmentTableUtils.seed.get(enchanter.getUniqueId())/*enchanter.getEnchantmentSeed()*/);
        player = enchanter;
        this.item = item;
        this.offers = offers;
        slot = button;

        for (int i = 0; i < 3; ++i) {
            costs[i] = EnchantmentTableUtils.getCost(random, i, bonus, item);
            if (costs[i] < i + 1)
                costs[i] = 0;
        }

        EnchantmentTableUtils.CustomList list = new EnchantmentTableUtils.CustomList(new ArrayList<>(), new ArrayList<>());

        Enchantment[] enchants = new Enchantment[3];
        int[] levels = new int[3];
        for (int i = 0; i < 3; ++i) {
            if (costs[i] > 0) {
                list = getEnchantmentList(i);

                if (list != null && !list.vanilla().isEmpty()) {
                    EnchantmentTableUtils.WeightedEnchantment enchant = list.vanilla().get(random.nextInt(list.vanilla().size()));
                    enchants[i] = enchant.getEnchantment();
                    levels[i] = enchant.getLevel();
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            if (offers[i] != null) {
                offers[i] = new EnchantmentOffer(enchants[i], levels[i], costs[i]);
            }
        }
        this.list = list;
    }

    public boolean hasEnchanted() {
        return enchanted;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }
    public void setHasEnchanted(boolean hasEnchanted) {
        enchanted = hasEnchanted;
    }

    public EnchantmentOffer getOffer() {
        return offers[slot];
    }

    public EnchantmentOffer[] getOffers() {
        return offers;
    }

    public EnchantmentTableUtils.CustomList getEnchantmentList(int slot) {
        return EnchantmentTableUtils.getEnchantmentList(player, item, slot, costs[slot]);
    }

    public EnchantmentTableUtils.CustomList getList() {
        return list;
    }
}
