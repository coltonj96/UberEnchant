package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for internal use.
 */
public class ClearCommand extends UberTabCommand {

    public ClearCommand() {
        super("uclear");
    }

    @Override
    public boolean onCmd() {
        if (args.length == 1) {
            ItemStack item = player.getInventory().getItemInMainHand();
            switch (args[0].toLowerCase()) {
                case "enchant" -> action("enchant", this::enchant, item);
                case "effect" -> action("effect", this::effect);
                case "lore" -> action("lore", this::lore, item);
                default -> EnchantmentUtils.help(player, "uclear");
            }
        } else
            response("&6%1$s", command.getUsage());
        return true;
    }

    @Override
    public List<String> onTab() {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            add(list, "uber.clear.enchant", "enchant");
            add(list, "uber.clear.effect", "effect");
            add(list, "uber.clear.lore", "lore");
        }
        return list;
    }

    private void enchant(ItemStack item) {
        if (item.hasItemMeta() && !UberUtils.getAllMap(item).isEmpty()) {
            UberUtils.getAllMap(item).keySet().forEach(enchant -> EnchantmentUtils.removeEnchantment(enchant, item));
            localized("&a", "actions.enchant.clear.success");
            return;
        }
        localized("&c", "actions.enchant.clear.fail");
    }

    private void effect() {
        if (!player.getActivePotionEffects().isEmpty()) {
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
            localized("&a", "actions.effect.clear.success");
            return;
        }
        localized("&c", "actions.effect.clear.no_effects");
    }
    private void lore(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.setLore(null);
        item.setItemMeta(meta);
        UberUtils.addEnchantmentLore(item);
        localized("&a", "actions.lore.clear.success");
    }
}
