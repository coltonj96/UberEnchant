package me.sciguymjm.uberenchant.enchantments.tasks;

import me.sciguymjm.uberenchant.api.utils.UberTask;
import me.sciguymjm.uberenchant.api.utils.random.WeightedChance;
import me.sciguymjm.uberenchant.api.utils.random.WeightedEntry;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class ArmorEffectChanceTask implements UberTask {

    private Player player;
    private EffectEnchantment enchantment;
    private EquipmentSlot slot;
    private int n;
    private WeightedChance<Boolean> chance;

    public ArmorEffectChanceTask(Player player, EffectEnchantment enchantment, EquipmentSlot slot) {
        this.player = player;
        this.enchantment = enchantment;
        this.slot = slot;
        this.n = 0;
        double value = (double) enchantment.getLevel(player.getInventory().getItem(slot)) / enchantment.getMaxLevel();
        this.chance = WeightedChance.fromArray(
                new WeightedEntry<>(true, value),
                new WeightedEntry<>(false, 1 - value)
        );
    }

    @Override
    public boolean update() {
        if (!player.isValid() || !player.isOnline() || !player.isDead())
            return false;
        ItemStack i = player.getInventory().getItem(slot);
        if (i == null || !enchantment.containsEnchantment(i))
            return false;
        if (chance.select()) {
            if (n % 100 == 0) {
                int level = enchantment.getLevel(i);
                if (player.hasPotionEffect(enchantment.getEffect().getEffect())) {
                    if (player.getPotionEffect(enchantment.getEffect().getEffect()).getAmplifier() < level)
                        player.addPotionEffect(new PotionEffect(enchantment.getEffect().getEffect(), 100, level, false, true));
                } else {
                    player.addPotionEffect(new PotionEffect(enchantment.getEffect().getEffect(), 100, level, false, true));
                }
            }
        }
        return true;
    }
}
