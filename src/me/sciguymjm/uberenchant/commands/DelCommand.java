package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.utils.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

/**
 * For internal use.
 */
public class DelCommand extends UberTabCommand {

    @Override
    public boolean onCmd() {
        if (args.length != 0) {
            ItemStack item = player.getInventory().getItemInMainHand();
            switch (args[0].toLowerCase()) {
                case "enchant" -> {
                    if (hasPermission("uber.del.enchant"))
                        enchant(item);
                    else
                        response(Reply.PERMISSIONS);
                }
                case "effect" -> {
                    if (hasPermission("uber.del.effect"))
                        effect();
                    else
                        response(Reply.PERMISSIONS);
                }
                case "lore" -> {
                    if (hasPermission("uber.del.lore"))
                        lore(item);
                    else
                        response(Reply.PERMISSIONS);
                }
                case "name" -> {
                    if (hasPermission("uber.del.name"))
                        name(item);
                    else
                        response(Reply.PERMISSIONS);
                }
                default -> EnchantmentUtils.help(player, "udel");
            }
        } else {
            response("&6%1$s", command.getUsage());
        }
        return true;
    }

    @Override
    public List<String> onTab() {
        List<String> list = new ArrayList<String>();
        if (args.length == 1) {
            if (hasPermission("uber.del.enchant"))
                list.add("enchant");
            if (hasPermission("uber.del.effect"))
                list.add("effect");
            if (hasPermission("uber.del.lore"))
                list.add("lore");
            if (hasPermission("uber.del.name"))
                list.add("name");
        } else if (args.length == 2) {
            ItemStack item = player.getInventory().getItemInMainHand();
            switch (args[0].toLowerCase()) {
                case "enchant" -> {
                    if (!item.getType().equals(Material.AIR) && item.getEnchantments().size() > 0) {
                        item.getEnchantments().keySet().forEach(enchant -> list.add(enchant.getKey().getKey().toLowerCase()));
                    }
                }
                case "effect" -> {
                    player.getActivePotionEffects().forEach(effect -> list.add(effect.getType().getName().toLowerCase()));
                }
            }
        }
        return list;
    }

    private void enchant(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 2) {
            response("&a/udel enchant &c<enchantment | id>");
            response(Reply.ARGUMENTS);
            return;
        }
        Enchantment enchantment = EnchantmentUtils.getEnchantment(args[1]);
        if (enchantment != null) {
            UberConfiguration.UberRecord enchant = UberConfiguration.getByEnchant(enchantment);
            if (!hasPermission(String.format("uber.del.enchant.%1$s", enchant.getName().toLowerCase()))) {
                response(Reply.PERMISSIONS);
                return;
            }
            if (hasPermission("uber.del.enchant.free")) {
                EnchantmentUtils.removeEnchantment(enchantment, item);
                response("&a" + UberLocale.get("actions.enchant.remove.success", enchant.getDisplayName()));
                return;
            }
            if (EconomyUtils.hasEconomy()) {
                if (EconomyUtils.has(player, enchant.getRemovalCost())) {
                    if (EnchantmentUtils.removeEnchantment(enchantment, item)) {
                        EconomyUtils.withdraw(player, enchant.getRemovalCost());
                        response("&a" + UberLocale.get("actions.enchant.remove.pay_success", enchant.getDisplayName(), enchant.getRemovalCost()));
                    } else {
                        response("&c" + UberLocale.get("actions.enchant.remove.no_enchant", enchant.getDisplayName()));
                    }
                } else {
                    response("&c" + UberLocale.get("actions.enchant.remove.pay_more", enchant.getRemovalCost() - EconomyUtils.getBalance(player)));
                }
            } else {
                response(Reply.NO_ECONOMY);
            }
            return;
        }
        response("&c" + UberLocale.get("actions.enchant.not_exist"));
    }

    private void effect() {
        if (args.length < 2) {
            response("&a/udel effect &c<effect | id>");
            response(Reply.ARGUMENTS);
            return;
        }
        PotionEffectType effect = EffectUtils.getEffect(args[1]);
        if (effect != null && player.hasPotionEffect(effect)) {
            EffectUtils.removeEffect(player, effect);
            response("&a" + UberLocale.get("actions.effect.remove.success"));
            return;
        }
        response("&c" + UberLocale.get("actions.effect.remove.not_exist"));
    }

    private void lore(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 2) {
            response("&a/udel lore &c<line#>");
            response(Reply.ARGUMENTS);
            return;
        }
        int index = UberUtils.offset(item);
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore() || (meta.hasLore() && meta.getLore().size() - index == 0)) {
            response("&c" + UberLocale.get("actions.lore.remove.no_lore"));
            return;
        }
        List<String> lore = meta.getLore();
        int size = lore.size() - index;
        if (size == 1) {
            lore.remove(index);
            meta.setLore(null);
            item.setItemMeta(meta);
            response("&a" + UberLocale.get("actions.lore.remove.success"));
            return;
        }
        int line = -1;
        try {
            line = Integer.parseInt(args[1]);
        } catch (NumberFormatException err) {
            response("&a/udel %1$s &c%2$s", args);
            response("&c" + UberLocale.get("actions.lore.remove.line_number"));
            return;
        }
        if (line > (size - 1) || line < 0) {
            response("&a/udel %1$s &c%2$s", args);
            response("&c" + UberLocale.get("actions.lore.remove.no_line"));
            return;
        }
        if (Integer.toString(line).contains(".")) {
            response("&a/udel %1$s &c%2$s", args);
            response(Reply.WHOLE_NUMBER);
            return;
        }
        lore.remove(index + line);
        meta.setLore(lore);
        item.setItemMeta(meta);
        response("&a" + UberLocale.get("actions.lore.remove.success"));
    }

    private void name(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (!item.hasItemMeta()) {
            response("&c" + UberLocale.get("actions.name.no_name"));
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) {
            response("&c" + UberLocale.get("actions.name.no_name"));
            return;
        }
        if (EconomyUtils.hasEconomy()) {
            double cost = EconomyUtils.getCost("cost.name.remove");
            if (EconomyUtils.has(player, cost)) {
                EconomyUtils.withdraw(player, cost);
                meta.setDisplayName(null);
                item.setItemMeta(meta);
                response("&a" + UberLocale.get("actions.name.remove.pay_success", cost));
            } else {
                response("&c" + UberLocale.get("actions.name.remove.pay_fail", (cost - EconomyUtils.getBalance(player))));
            }
        } else {
            meta.setDisplayName(null);
            item.setItemMeta(meta);
            response("&a" + UberLocale.get("actions.name.remove.success"));
        }
    }
}
