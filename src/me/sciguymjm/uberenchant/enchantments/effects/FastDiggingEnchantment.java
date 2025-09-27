package me.sciguymjm.uberenchant.enchantments.effects;

import me.sciguymjm.uberenchant.api.utils.Rarity;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.BoolTag;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.IntTag;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class FastDiggingEnchantment extends EffectEnchantment {

    public FastDiggingEnchantment() {
        super("FAST_DIGGING");
        setTag(BoolTag.HAS_CHANCE, true);
        setTag(IntTag.DURATION, 3);
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
    public void onDig(BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        Player player = event.getPlayer();
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
