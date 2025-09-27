package me.sciguymjm.uberenchant.enchantments.tasks;

import me.sciguymjm.uberenchant.api.utils.random.WeightedChance;
import me.sciguymjm.uberenchant.api.utils.random.WeightedEntry;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;

public class ArmorEffectChanceTask extends ArmorEffectTask {

    private final WeightedChance<Boolean> chance;

    public ArmorEffectChanceTask(Player player, EffectEnchantment enchantment, EquipmentSlot slot, Conditional conditional) {
        super(player, enchantment, slot, conditional);
        this.n = 0;
        this.duration = 200;
        double value = (double) enchantment.getLevel(getItem()) / enchantment.getMaxLevel();
        this.chance = WeightedChance.fromArray(
                new WeightedEntry<>(true, value),
                new WeightedEntry<>(false, 1 - value)
        );
    }

    @Override
    public boolean update() {
        if (!player.isValid() || !player.isOnline() || player.isDead())
            return false;
        item = getItem();
        if (item == null || conditional.test(player, item, enchantment)) {
            player.removePotionEffect(effect);
            return false;
        }
        int level = enchantment.getLevel(item);
        if (n++ >= Math.max(100, duration - 20 * level)) {
            n = 0;
            if (chance.select())
                return enchantment.apply(item, player, Math.min(100, enchantment.getDuration(item)));
        }
        return true;
    }
}
