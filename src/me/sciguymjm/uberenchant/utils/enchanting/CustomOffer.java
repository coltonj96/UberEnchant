package me.sciguymjm.uberenchant.utils.enchanting;

import me.sciguymjm.uberenchant.api.utils.random.UberRandom;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Utility class for internal use.
 */
public class CustomOffer {
    private boolean enchanted = false;
    private int slot;
    private final int[] costs = new int[3];
    private final EnchantmentOffer[] offers;
    private final Player player;
    private final ItemStack item;

    private EnchantmentTableUtils.CustomList list;

    public void setEnchanted() {
        enchanted = true;
    }

    public boolean isEnchanted() {
        return enchanted;
    }

    public CustomOffer(PrepareItemEnchantEvent event, int button) {
        UberRandom random;
        random = new UberRandom(EnchantmentTableUtils.seed.get(event.getEnchanter().getUniqueId()));
        player = event.getEnchanter();
        item = event.getItem();
        offers = event.getOffers();
        slot = button;

        //do {
            this.list = generate(event, random);
        //} while (checkNull(offers));
        //this.list = generate(event, random);
    }

    private boolean checkNull(EnchantmentOffer... objects) {
        for (Object object : objects)
            if (object == null)
                return true;
        return false;
    }

    private EnchantmentTableUtils.CustomList generate(PrepareItemEnchantEvent event, UberRandom random) {
        int bonus = event.getEnchantmentBonus();
        if (EnchantmentTableUtils.floorBonus()) {
            if (bonus >= 22)
                bonus += getFloorBonus(event.getEnchantBlock());
        }

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
            if (offers[i] != null && enchants[i] != null)
                offers[i] = new EnchantmentOffer(enchants[i], levels[i], costs[i]);
        }
        return list;
    }

    private int getFloorBonus(Block block) {
        double bonus = 0.0;
        for (int x = 1; x >= -1; x--) {
            for (int z = 1; z >= -1; z--) {
                bonus += getBonusValue(block.getRelative(x, -1, z).getType());
            }
        }
        return (int) Math.round(bonus);
    }

    private double getBonusValue(Material material) {
        if (EnchantmentTableUtils.bonusBlocks().containsKey(material))
            return EnchantmentTableUtils.bonusBlocks().get(material);
        return 0;
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
        if (slot == -1)
            return null;
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
