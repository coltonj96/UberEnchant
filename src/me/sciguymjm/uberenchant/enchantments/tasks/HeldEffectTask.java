package me.sciguymjm.uberenchant.enchantments.tasks;

import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import org.bukkit.entity.LivingEntity;

public class HeldEffectTask extends EffectTask {

    public HeldEffectTask(LivingEntity entity, EffectEnchantment enchantment, Conditional conditional) {
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
}
