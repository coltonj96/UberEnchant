package me.sciguymjm.uberenchant.utils.reflection;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import org.bukkit.Keyed;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.EquipmentSlot;

/**
 * UNUSED
 */
public interface IEnchant extends Keyed {
    EnchantmentTarget getItemTarget();
    EquipmentSlot[] getSlots();

    int getStartLevel();
    int getMaxLevel();
    boolean isTreasure();
    boolean isCursed();
    boolean isTradeable();
    boolean isDiscoverable();
    String getName();



    Rarity getRarity();
    String getId();

}
