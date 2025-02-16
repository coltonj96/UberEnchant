package me.sciguymjm.uberenchant.utils.reflection;

/*import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.bukkit.craftbukkit.v1_20_R3.CraftEquipmentSlot;*/

/**
 * UNUSED
 */
public class NMSEnchant {/*extends Enchantment {

    private IEnchant enchant;

    protected NMSEnchant(IEnchant enchantment, Rarity rarity) {
        super(rarity, category(enchantment), slots(enchantment));
        enchant = enchantment;
    }

    public static EnchantmentCategory category(IEnchant enchantment) {
        return switch (enchantment.getItemTarget()) {
            case WEAPON -> EnchantmentCategory.WEAPON;
            case TOOL -> EnchantmentCategory.DIGGER;
            case ARMOR -> EnchantmentCategory.ARMOR;
            case BOW -> EnchantmentCategory.BOW;
            case TRIDENT -> EnchantmentCategory.TRIDENT;
            case CROSSBOW -> EnchantmentCategory.CROSSBOW;
            case WEARABLE -> EnchantmentCategory.WEARABLE;
            case BREAKABLE -> EnchantmentCategory.BREAKABLE;
            case ARMOR_FEET -> EnchantmentCategory.ARMOR_FEET;
            case ARMOR_HEAD -> EnchantmentCategory.ARMOR_HEAD;
            case ARMOR_LEGS -> EnchantmentCategory.ARMOR_LEGS;
            case ARMOR_TORSO -> EnchantmentCategory.ARMOR_CHEST;
            case VANISHABLE -> EnchantmentCategory.VANISHABLE;
            case FISHING_ROD -> EnchantmentCategory.FISHING_ROD;
            default -> throw new IllegalStateException(enchantment.getItemTarget().toString());
        };
    }

    public static EquipmentSlot[] slots(IEnchant enchantment) {
        org.bukkit.inventory.EquipmentSlot[] slots = enchantment.getSlots();
        EquipmentSlot[] nmsSlots = new EquipmentSlot[slots.length];

        for (int i = 0; i < nmsSlots.length; i++) {
            org.bukkit.inventory.EquipmentSlot bukkitSlot = slots[i];
            nmsSlots[i] = CraftEquipmentSlot.getNMS(bukkitSlot);
        }

        return nmsSlots;
    }*/
}
