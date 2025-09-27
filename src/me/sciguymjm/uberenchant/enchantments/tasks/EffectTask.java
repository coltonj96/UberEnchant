package me.sciguymjm.uberenchant.enchantments.tasks;

import me.sciguymjm.uberenchant.api.utils.UberTask;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class EffectTask implements UberTask {

    protected LivingEntity entity;
    protected ItemStack item;
    protected EffectEnchantment enchantment;
    protected PotionEffectType effect;
    protected Conditional conditional;
    protected int n;
    protected int duration;

    public EffectTask(LivingEntity entity, EffectEnchantment enchantment, Conditional conditional) {
        this.entity = entity;
        this.enchantment = enchantment;
        this.effect = enchantment.getEffect().getEffect();
        this.conditional = conditional;
        this.n = 300;
        this.duration = 300;
    }

    @Override
    public boolean update() {
        if (!entity.isValid() || entity.isDead())
            return false;
        item = getItem();
        if (item == null || conditional.test(entity, item, enchantment)) {
            entity.removePotionEffect(effect);
            return false;
        }
        if (n++ >= duration - 60) {
            n = 0;
            return enchantment.apply(item, entity, duration);
        }
        if (!entity.hasPotionEffect(effect))
            return enchantment.apply(item, entity, duration);
        return true;
    }

    public ItemStack getItem() {
        if (entity instanceof HumanEntity human)
            return human.getInventory().getItemInMainHand();
        return entity.getEquipment().getItemInMainHand();
    }
}
