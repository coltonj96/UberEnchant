package me.sciguymjm.uberenchant.enchantments.effects;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import me.sciguymjm.uberenchant.utils.WorldGuardUtils;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class HarmEnchantment extends EffectEnchantment {

    public HarmEnchantment() {
        super("HARM");
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
        if (event.isCancelled()) {
            return; // Respect other plugins that cancelled the event
        }

        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        if (damager instanceof Player && victim instanceof Player) {
            Player attacker = (Player) damager;
            Player target = (Player) victim;
            if (!WorldGuardUtils.isPvPEnabled(attacker, target)) {
                return; // WorldGuard prevents PvP, so don't apply custom effect
            }
        }
        // If checks pass (or not Player vs Player), proceed to apply effect
        apply(damager, victim);
    }
}