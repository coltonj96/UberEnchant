package me.sciguymjm.uberenchant.enchantments.effects;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.BoolTag;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class IncreaseDamageEnchantment extends EffectEnchantment {

    public IncreaseDamageEnchantment() {
        super("INCREASE_DAMAGE");
        setTag(BoolTag.HAS_CHANCE, true);
    }

    @Override
    public Rarity getRarity() {
        return Rarity.VERY_RARE;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.BREAKABLE;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return EnchantmentTarget.ARMOR.includes(itemStack) || EnchantmentTarget.WEAPON.includes(itemStack);
    }

    @Override
    @EventHandler(priority = EventPriority.MONITOR)
    public void OnHit(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        if (event.getDamager() instanceof Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType() == Material.AIR || !containsEnchantment(item))
                return;
            if (conditions(item))
                return;
            if (BoolTag.HAS_CHANCE.test(item, this))
                apply(player);
            else
                apply(item, player);
        }
    }
}
