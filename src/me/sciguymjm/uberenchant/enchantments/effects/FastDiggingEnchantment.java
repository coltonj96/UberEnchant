package me.sciguymjm.uberenchant.enchantments.effects;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class FastDiggingEnchantment extends EffectEnchantment {

    public FastDiggingEnchantment() {
        super("FAST_DIGGING");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.RARE;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.TOOL;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return EnchantmentTarget.TOOL.includes(itemStack);
    }

    @EventHandler
    public void OnHit(EntityDamageByEntityEvent event) {
        apply(event.getDamager(), event.getEntity());
    }

    @EventHandler
    public void onDig(BlockBreakEvent event) {
        apply(event.getPlayer());
    }
}
