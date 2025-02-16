package me.sciguymjm.uberenchant.enchantments.effects;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ConfusionEnchantment extends EffectEnchantment {

    public ConfusionEnchantment() {
        super("CONFUSION");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNCOMMON;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.WEAPON;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return EnchantmentTarget.WEAPON.includes(itemStack);
    }

    @EventHandler
    public void OnHit(EntityDamageByEntityEvent event) {
        apply(event.getDamager(), event.getEntity());
    }
}
