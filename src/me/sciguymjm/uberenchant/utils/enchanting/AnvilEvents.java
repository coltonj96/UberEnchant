package me.sciguymjm.uberenchant.utils.enchanting;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

import java.util.HashMap;
import java.util.Map;

public class AnvilEvents implements Listener {

    private short getDamage(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return (meta == null) ? 0 : (short) ((Damageable) meta).getDamage();
    }

    private void setDamage(ItemStack item, int damage) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Damageable dMeta = ((Damageable) meta);
            dMeta.setDamage(damage);
            item.setItemMeta(dMeta);
        }
    }

    private short getMaxDamage(ItemStack item) {
        return item.getType().getMaxDurability();
    }

    private boolean isDamageable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta instanceof Damageable;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void createResult(PrepareAnvilEvent event) {
        ItemStack EMPTY = new ItemStack(Material.AIR);
        AnvilInventory anvil = event.getInventory();
        ItemStack item = anvil.getItem(0);

        anvil.setRepairCost(1);
        int i = 0;

        if (item != null) {
            ItemStack itemstack1 = item.clone();
            ItemStack itemstack2 = anvil.getItem(1);
            Map<Enchantment, Integer> map;
            if (itemstack1.getType().equals(Material.ENCHANTED_BOOK))
                map = new HashMap<>(((EnchantmentStorageMeta) itemstack1.getItemMeta()).getStoredEnchants());
            else
                map = new HashMap<>(itemstack1.getEnchantments());
            int j = ((Repairable) item.getItemMeta()).getRepairCost() + (itemstack2 == null ? 0 : ((Repairable) itemstack2.getItemMeta()).getRepairCost());

            anvil.setRepairCost(0);

            if (itemstack2 != null) {
                boolean flag = itemstack2.getType().equals(Material.ENCHANTED_BOOK) && !((EnchantmentStorageMeta) itemstack2.getItemMeta()).getStoredEnchants().isEmpty();
                int k;
                int l;
                int i1;

                if (itemstack1.getItemMeta() instanceof Repairable && isValid(item, itemstack2)) {
                    k = Math.min(getDamage(itemstack1), getMaxDamage(itemstack1) / 4);
                    if (k <= 0) {
                        event.setResult(EMPTY);
                        anvil.setRepairCost(0);
                        return;
                    }

                    for (i1 = 0; k > 0 && i1 < itemstack2.getAmount(); ++i1) {
                        l = getDamage(itemstack1) - k;
                        setDamage(itemstack1, l);

                        ++i;
                        k = Math.min(getDamage(itemstack1), getMaxDamage(itemstack1) / 4);
                    }

                    anvil.setRepairCost(i1);
                } else {
                if (!flag && (!itemstack1.getType().equals(itemstack2.getType()) || !isDamageable(itemstack1))) {
                    event.setResult(EMPTY);
                    anvil.setRepairCost(0);
                    return;
                }

                    if (isDamageable(itemstack1) && !flag) {
                        k = getMaxDamage(item) - getDamage(item);
                        i1 = getMaxDamage(itemstack2) - getDamage(itemstack2);
                        l = i1 + getMaxDamage(itemstack1) * 12 / 100;
                        int j1 = k + l;
                        int k1 = getMaxDamage(itemstack1) - j1;

                        if (k1 < 0) {
                            k1 = 0;
                        }

                        if (k1 < getDamage(itemstack1)) {
                            setDamage(itemstack1, k1);
                            i += 2;
                        }
                    }

                    Map<Enchantment, Integer> map1;
                    if (itemstack2.getType().equals(Material.ENCHANTED_BOOK))
                        map1 = new HashMap<>(((EnchantmentStorageMeta) itemstack2.getItemMeta()).getStoredEnchants());
                    else
                        map1 = new HashMap<>(itemstack2.getEnchantments());

                    boolean flag1 = false;
                    boolean flag2 = false;

                for (Enchantment enchantment : map1.keySet()) {
                    if (enchantment != null) {

                        int l1 = map.getOrDefault(enchantment, 0);
                        int i2 = map1.get(enchantment);

                        i2 = l1 == i2 ? i2 + 1 : Math.max(i2, l1);

                        boolean flag3 = enchantment.canEnchantItem(item);

                        if (enchantment instanceof UberEnchantment uber)
                            flag3 = uber.canEnchantItem(item);

                        if (item.getType().equals(Material.ENCHANTED_BOOK)) {
                            flag3 = true;
                        }

                        for (Enchantment enchantment1 : map.keySet()) {
                            if (enchantment1 != enchantment && enchantment.conflictsWith(enchantment1)) {
                                flag3 = false;
                                ++i;
                            }
                        }

                        if (!flag3) {
                            flag2 = true;
                        } else {
                            flag1 = true;
                            int max = UberConfiguration.getByEnchant(enchantment).getMaxLevel();
                            if (i2 > max) {
                                i2 = max;
                            }

                            map.put(enchantment, i2);
                            int j2 = 0;

                            switch ((int) EnchantmentUtils.getRarity(enchantment)) {
                                case 10:
                                    j2 = 1;
                                    break;
                                case 5:
                                    j2 = 2;
                                    break;
                                case 2:
                                    j2 = 4;
                                    break;
                                case 1:
                                    j2 = 8;
                                default:
                                    break;
                            }

                            if (flag) {
                                j2 = Math.max(1, j2 / 2);
                            }

                            i += j2 * i2;
                            if (item.getAmount() > 1) {
                                i = 40;
                            }
                        }
                    }
                }

                    if (flag2 && !flag1) {
                        event.setResult(EMPTY);
                        anvil.setRepairCost(0);
                        return;
                    }
                }
            }

            ItemMeta meta = itemstack1.getItemMeta();
            if (!anvil.getRenameText().equals(meta.getDisplayName())) {
                meta.setDisplayName(ChatUtils.color(anvil.getRenameText()));
                itemstack1.setItemMeta(meta);
                i++;
            }

            anvil.setRepairCost(j + i);
            if (i <= 0) {
                itemstack1 = EMPTY;
            }

            if (!itemstack1.getType().equals(Material.AIR)) {
                int k2 = ((Repairable) itemstack1.getItemMeta()).getRepairCost();

                if (itemstack2 != null && !itemstack2.getType().equals(Material.AIR) && k2 < ((Repairable) itemstack2.getItemMeta()).getRepairCost()) {
                    k2 = ((Repairable) itemstack2.getItemMeta()).getRepairCost();
                }

                k2 = k2 * 2 + 1;

                Repairable meta2 = ((Repairable) itemstack1.getItemMeta());
                meta2.setRepairCost(k2);
                itemstack1.setItemMeta(meta2);
                UberUtils.removeEnchantmentLore(itemstack1);
                if (itemstack1.getType().equals(Material.ENCHANTED_BOOK)) {
                    EnchantmentUtils.setStoredEnchantments(map, itemstack1);
                } else {
                    EnchantmentUtils.setEnchantments(map, itemstack1);
                }
                UberUtils.addEnchantmentLore(itemstack1);
            }

            event.setResult(itemstack1);
        }
    }

    private boolean isValid(ItemStack item1, ItemStack item2) {
        Material m1 = item1.getType();
        Material m2 = item2.getType();
        switch (m1) {
            case WOODEN_AXE,
                    WOODEN_HOE,
                    WOODEN_PICKAXE,
                    WOODEN_SHOVEL,
                    WOODEN_SWORD,
                    SHIELD -> {
                switch (m2) {
                    case ACACIA_PLANKS,
                            BAMBOO_PLANKS,
                            BIRCH_PLANKS,
                            CHERRY_PLANKS,
                            CRIMSON_PLANKS,
                            DARK_OAK_PLANKS,
                            JUNGLE_PLANKS,
                            MANGROVE_PLANKS,
                            OAK_PLANKS,
                            SPRUCE_PLANKS,
                            WARPED_PLANKS -> {
                        return true;
                    }
                    default -> {
                        return false;
                    }
                }
            }
            case LEATHER_BOOTS,
                    LEATHER_CHESTPLATE,
                    LEATHER_HELMET,
                    LEATHER_LEGGINGS -> {
                return m2.equals(Material.LEATHER);
            }
            case STONE_AXE,
                    STONE_HOE,
                    STONE_PICKAXE,
                    STONE_SHOVEL,
                    STONE_SWORD -> {
                switch (m2) {
                    case COBBLED_DEEPSLATE,
                          COBBLESTONE,
                          BLACKSTONE -> {
                       return true;
                   }
                   default -> {
                        return false;
                    }
                }
            }
            case CHAINMAIL_BOOTS,
                    CHAINMAIL_CHESTPLATE,
                    CHAINMAIL_HELMET,
                    CHAINMAIL_LEGGINGS,
                    IRON_AXE,
                    IRON_HOE,
                    IRON_BOOTS,
                    IRON_CHESTPLATE,
                    IRON_HELMET,
                    IRON_LEGGINGS,
                    IRON_PICKAXE,
                    IRON_SWORD,
                    IRON_SHOVEL -> {
                return m2.equals(Material.IRON_INGOT);
            }
            case GOLDEN_BOOTS,
                    GOLDEN_CHESTPLATE,
                    GOLDEN_HELMET,
                    GOLDEN_LEGGINGS,
                    GOLDEN_AXE,
                    GOLDEN_HOE,
                    GOLDEN_PICKAXE,
                    GOLDEN_SHOVEL,
                    GOLDEN_SWORD -> {
                return m2.equals(Material.GOLD_INGOT);
            }
            case DIAMOND_BOOTS,
                    DIAMOND_CHESTPLATE,
                    DIAMOND_HELMET,
                    DIAMOND_LEGGINGS,
                    DIAMOND_AXE,
                    DIAMOND_HOE,
                    DIAMOND_PICKAXE,
                    DIAMOND_SHOVEL,
                    DIAMOND_SWORD -> {
                return m2.equals(Material.DIAMOND);
            }
            case NETHERITE_BOOTS,
                    NETHERITE_CHESTPLATE,
                    NETHERITE_HELMET,
                    NETHERITE_LEGGINGS,
                    NETHERITE_AXE,
                    NETHERITE_HOE,
                    NETHERITE_PICKAXE,
                    NETHERITE_SHOVEL,
                    NETHERITE_SWORD -> {
                return m2.equals(Material.NETHERITE_INGOT);
            }
            case TURTLE_HELMET -> {
                return m2.equals(Material.SCUTE);
            }
            case ELYTRA -> {
                return m2.equals(Material.PHANTOM_MEMBRANE);
            }
            default -> {
                return false;
            }
        }
    }
}
