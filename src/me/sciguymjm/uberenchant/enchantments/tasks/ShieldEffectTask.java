package me.sciguymjm.uberenchant.enchantments.tasks;

import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ShieldEffectTask extends EffectTask {

    public ShieldEffectTask(LivingEntity entity, EffectEnchantment enchantment, Conditional conditional) {
        super(entity, enchantment, conditional);
        duration = 180;
    }

    @Override
    public boolean update() {
        if (!entity.isValid() || entity.isDead())
            return false;
        item = getItem();
        if (item == null || conditional.test(entity, item, enchantment))
            return false;
        int dur = Math.max(duration, enchantment.getDuration(item));
        if (n++ >= duration - 60) {
            n = 0;
            return enchantment.apply(item, entity, dur);
        }
        if (!entity.hasPotionEffect(effect))
            return enchantment.apply(item, entity, dur);
        return true;
    }

    @Override
    public ItemStack getItem() {
        if (entity instanceof Player player) {
            ItemStack main = player.getInventory().getItemInMainHand();
            ItemStack off = player.getInventory().getItemInOffHand();
            boolean shield_main = main.getType() == Material.SHIELD;
            boolean shield_off = off.getType() == Material.SHIELD;
            if (shield_main && shield_off)
                return main;
            if (!shield_main && shield_off)
                return off;
            if (shield_main)
                return main;
        }
        return null;
    }
}
