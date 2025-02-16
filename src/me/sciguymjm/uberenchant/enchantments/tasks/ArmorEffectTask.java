package me.sciguymjm.uberenchant.enchantments.tasks;

import me.sciguymjm.uberenchant.api.utils.UberTask;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArmorEffectTask implements UberTask {

    private Player player;
    private EffectEnchantment enchantment;
    private EquipmentSlot slot;
    private int n;

    public ArmorEffectTask(Player player, EffectEnchantment enchantment, EquipmentSlot slot) {
        this.player = player;
        this.enchantment = enchantment;
        this.slot = slot;
        this.n = 0;
    }

    @Override
    public boolean update() {
        if (!player.isValid() || !player.isOnline())
            return false;
        ItemStack i = player.getInventory().getItem(slot);
        if (i == null || !enchantment.containsEnchantment(i))
            return false;
        if (n++ >= 100) {
            n = 0;
            int level = enchantment.getLevel(i);
            PotionEffectType effect = enchantment.getEffect().getEffect();
            if (player.hasPotionEffect(effect)) {
                int amp = player.getPotionEffect(effect).getAmplifier();
                if (amp < level)
                    player.addPotionEffect(new PotionEffect(effect, 310, level, false, true));
                else
                    player.addPotionEffect(new PotionEffect(effect, 310, amp, false, true));
                return true;
            }
            player.addPotionEffect(new PotionEffect(effect, 310, level, false, true));
        }
        return true;
    }
}
