package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.utils.EconomyUtils;
import me.sciguymjm.uberenchant.utils.Reply;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentUtils;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * For internal use.
 */
public class ExtractCommand extends UberTabCommand {

    @Override
    public boolean onCmd() {
        if (args.length != 0) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (hasPermission("uber.extract.enchant"))
                enchant(item);
            else
                response(Reply.PERMISSIONS);
        } else {
            response("&6%1$s", command.getUsage());
        }
        return true;
    }

    @Override
    public List<String> onTab() {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (!item.getType().equals(Material.AIR) && !UberUtils.getAllMap(item).isEmpty())
                UberUtils.getAllMap(item).keySet().forEach(enchant -> list.add(enchant.getKey().getKey().toLowerCase()));
        }
        return list;
    }

    private void enchant(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        Set<Enchantment> set = EnchantmentUtils.getMatches(args[0]);
        if (EnchantmentUtils.multi(player, set))
            return;
        Enchantment enchantment = set.iterator().next();
        if (enchantment != null) {
            if (item.getType().equals(Material.ENCHANTED_BOOK)) {
                localized("&c", "actions.enchant.extract.book");
                return;
            }
            UberConfiguration.UberRecord enchant = UberConfiguration.getByEnchant(enchantment);
            if (!hasPermission("uber.extract.enchant.%1$s", enchant.getName().toLowerCase())) {
                response(Reply.PERMISSIONS);
                return;
            }
            ItemStack book = EnchantmentUtils.extractEnchantment(enchantment, item);
            if (book == null) {
                localized("&c", "actions.enchant.extract.not_found", enchant.getDisplayName());
                return;
            }
            int level = UberUtils.getAllMap(item).get(enchantment);
            if (hasPermission("uber.extract.enchant.free") || !EconomyUtils.useEconomy()) {
                if (player.getInventory().addItem(book).isEmpty()) {
                    EnchantmentUtils.removeEnchantment(enchantment, item);
                    localized("&a", "actions.enchant.extract.free_success", enchant.getDisplayName());
                } else {
                    localized("&c", "actions.enchant.extract.no_room");
                }
                return;
            }
            if (EconomyUtils.hasEconomy()) {
                if (!hasPermission("uber.extract.enchant.%1$s", enchant.getName().toLowerCase())) {
                    response(Reply.PERMISSIONS);
                    return;
                }
                double cost = enchant.getExtractionCost() + (enchant.getCostMultiplier() * enchant.getExtractionCost() * (level - 1));
                if (EconomyUtils.has(player, cost)) {
                    if (player.getInventory().addItem(book).isEmpty()) {
                        EnchantmentUtils.removeEnchantment(enchantment, item);
                        EconomyResponse n = EconomyUtils.withdraw(player, cost);
                        localized("&a", "actions.enchant.extract.pay_success", enchant.getDisplayName(), n.amount);
                    } else {
                        localized("&c", "actions.enchant.extract.no_room");
                    }
                    return;
                } else {
                    localized("&c", "actions.enchant.extract.pay_more", cost - EconomyUtils.getBalance(player));
                }
            } else {
                response(Reply.NO_ECONOMY);
            }
            return;
        }
        localized("&c", "actions.enchant.not_exist");
    }
}
