package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.utils.EconomyUtils;
import me.sciguymjm.uberenchant.utils.EnchantmentUtils;
import me.sciguymjm.uberenchant.utils.Reply;
import me.sciguymjm.uberenchant.utils.UberLocale;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

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
        if (args.length == 1)
            list = EnchantmentUtils.find(player, args[0]);
        return list;
    }

    private void enchant(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        Enchantment enchantment = EnchantmentUtils.getEnchantment(args[0]);
        if (enchantment != null) {
            if (item.getType().equals(Material.ENCHANTED_BOOK)) {
                response("&c" + UberLocale.get("actions.enchant.extract.book"));
                return;
            }
            UberConfiguration.UberRecord enchant = UberConfiguration.getByEnchant(enchantment);
            if (!hasPermission(String.format("uber.extract.enchant.%1$s", enchant.getName().toLowerCase()))) {
                response(Reply.PERMISSIONS);
                return;
            }
            ItemStack book = EnchantmentUtils.extractEnchantment(enchantment, item);
            if (book == null) {
                response("&c" + UberLocale.get("actions.enchant.extract.not_found", enchant.getDisplayName()));
                return;
            }
            int level = item.getEnchantmentLevel(enchantment);
            if (hasPermission("uber.extract.enchant.free")) {
                if (player.getInventory().addItem(book).isEmpty()) {
                    EnchantmentUtils.removeEnchantment(enchantment, item);
                    response("&a" + UberLocale.get("actions.enchant.extract.free_success", enchant.getDisplayName()));
                } else {
                    response("&c" + UberLocale.get("actions.enchant.extract.no_room"));
                }
                return;
            }
            if (EconomyUtils.hasEconomy()) {
                if (!hasPermission(String.format("uber.extract.enchant.%1$s", enchant.getName().toLowerCase()))) {
                    response(Reply.PERMISSIONS);
                    return;
                }
                double cost = enchant.getExtractionCost() + (enchant.getCostMultiplier() * enchant.getExtractionCost() * (level - 1));
                if (EconomyUtils.has(player, cost)) {
                    if (player.getInventory().addItem(book).isEmpty()) {
                        EnchantmentUtils.removeEnchantment(enchantment, item);
                        EconomyResponse n = EconomyUtils.withdraw(player, cost);
                        response("&a" + UberLocale.get("actions.enchant.extract.pay_success", enchant.getDisplayName(), n.amount));
                    } else {
                        response("&c" + UberLocale.get("actions.enchant.extract.no_room"));
                    }
                    return;
                } else {
                    response("&c" + UberLocale.get("actions.enchant.extract.pay_more", cost - EconomyUtils.getBalance(player)));
                }
            } else {
                response(Reply.NO_ECONOMY);
            }
            return;
        }
        response("&c" + UberLocale.get("actions.enchant.not_exist"));
    }
}
