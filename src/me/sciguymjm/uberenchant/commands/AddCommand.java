package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.utils.*;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentUtils;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for internal use.
 */
public class AddCommand extends UberTabCommand {

    @Override
    public boolean onCmd() {
        if (args.length != 0) {
            ItemStack item = player.getInventory().getItemInMainHand();
            switch (args[0].toLowerCase()) {
                case "enchant" -> {
                    if (hasPermission("uber.add.enchant"))
                        enchant(item);
                    else
                        response(Reply.PERMISSIONS);
                }
                case "effect" -> {
                    if (hasPermission("uber.add.effect"))
                        effect();
                    else
                        response(Reply.PERMISSIONS);
                }
                case "lore" -> {
                    if (hasPermission("uber.add.lore"))
                        lore(item);
                    else
                        response(Reply.PERMISSIONS);
                }
                case "name" -> {
                    if (hasPermission("uber.add.name"))
                        name(item);
                    else
                        response(Reply.PERMISSIONS);
                }
                default -> EnchantmentUtils.help(player, "uadd");
            }
        } else {
            response("&6%1$s", command.getUsage());
        }
        return true;
    }

    @Override
    public List<String> onTab() {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            if (hasPermission("uber.add.enchant"))
                list.add("enchant");
            if (hasPermission("uber.add.effect"))
                list.add("effect");
            if (hasPermission("uber.add.lore"))
                list.add("lore");
            if (hasPermission("uber.add.name"))
                list.add("name");
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "enchant" -> list = EnchantmentUtils.find(player, args[1]);
                case "effect" -> list = EffectUtils.matchEffects(args[1]);
            }
        }
        /*if (args.length == 5 && args[0].equalsIgnoreCase("effect")) {
            list = Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).toList();
        }*/
        return list;
    }

    private void enchant(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            if (!hasPermission("uber.add.enchant.book")) {
                response(Reply.HOLD_ITEM);
                return;
            }
            item = new ItemStack(Material.ENCHANTED_BOOK);
        }
        if (args.length < 3) {
            response("&a/uadd enchant &c<enchantment | id> <level>");
            response(Reply.ARGUMENTS);
            return;
        }
        int level = 0;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            response("&a/uadd %1$s %2$s &c%3$s", args);
            response(Reply.WHOLE_NUMBER);
            return;
        }
        if (args[1].equalsIgnoreCase("all") && hasPermission("uber.enchant.add.all")) {
            if (level > 255) {
                localized("&c", "actions.enchant.add.max_level");
                level = 255;
            }
            Map<Enchantment, Integer> map = new HashMap<>();
            final Integer lev = level;
            UberConfiguration.getRecords().forEach(r -> map.put(r.getEnchant(), lev));
            //for (Enchantment enchantment : Enchantment.values()) {
            if (item.getType().equals(Material.ENCHANTED_BOOK))
                UberUtils.addStoredEnchantments(map, item);
            else
                UberUtils.addEnchantments(map, item);
            //}
            player.getInventory().setItemInMainHand(item);
            localized("&a", "actions.enchant.add.success_all");
            return;
        }
        Enchantment enchant = EnchantmentUtils.getEnchantment(args[1]);
        if (enchant == null) {
            localized("&c", "actions.enchant.invalid");
            response("&a/ulist enchants");
            return;
        }
        UberConfiguration.UberRecord e = UberConfiguration.getByEnchant(enchant);
        if (!hasPermission("uber.add.enchant.all") && !hasPermission("uber.add.enchant.%1$s", e.getName().toLowerCase())) {
            response(Reply.PERMISSIONS);
            return;
        }
        if (hasPermission("uber.enchant.%1$s.free", e.getName().toLowerCase())) {
            if (level >= e.getMinLevel() && level <= e.getMaxLevel() || hasPermission("uber.enchant.bypass.level")) {
                if (level > 255) {
                    localized("&c", "actions.enchant.add.max_level");
                    level = 255;
                }
                if (item.getType().equals(Material.ENCHANTED_BOOK))
                    EnchantmentUtils.setStoredEnchantment(enchant, item, level);
                else
                    EnchantmentUtils.setEnchantment(enchant, item, level);
                player.getInventory().setItemInMainHand(item);
                localized("&a", "actions.enchant.add.success", e.getDisplayName(), level);
            } else {
                localized("&c", "actions.enchant.add.range", e.getMinLevel(), e.getMaxLevel());
            }
            return;
        }
        if (EconomyUtils.hasEconomy()) {
            if (!e.getEnchant().canEnchantItem(item)) {
                if (!e.getCanUseOnAnything() || !hasPermission("uber.enchant.bypass.any")) {
                    localized("&c", "actions.enchant.add.incompatible");
                    return;
                }
            }
            if (level >= e.getMinLevel() && level <= e.getMaxLevel()) {
                double cost = e.getCostForLevel().containsKey(level) ? e.getCostForLevel().get(level) : e.getCost() + (e.getCostMultiplier() * e.getCost() * (level - 1));
                if (EconomyUtils.has(player, cost)) {
                    if (level > 255) {
                        localized("&c", "actions.enchant.add.max_level");
                        level = 255;
                    }
                    if (item.getType().equals(Material.ENCHANTED_BOOK))
                        EnchantmentUtils.setStoredEnchantment(enchant, item, level);
                    else
                        EnchantmentUtils.setEnchantment(enchant, item, level);
                    EconomyResponse n = EconomyUtils.withdraw(player, cost);
                    player.getInventory().setItemInMainHand(item);
                    localized("&a", "actions.enchant.add.pay_success", e.getDisplayName(), level, n.amount);
                } else {
                    localized("&c", "actions.enchant.add.pay_more", cost - EconomyUtils.getBalance(player));
                }
            } else {
                localized("&c", "actions.enchant.add.range", e.getMinLevel(), e.getMaxLevel());
            }
        } else {
            response(Reply.NO_ECONOMY);
        }
    }

    private void effect() {
        if (args.length < 4) {
            response("&a/uadd effect &c<effect | id> <duration> <level>");
            response(Reply.ARGUMENTS);
            return;
        }
        int level = 1;
        int duration = 60;
        try {
            duration = Integer.parseInt(args[2]);
            duration *= 20;
        } catch (NumberFormatException e) {
            response("&a/uadd %1$s %2$s &c%3$s &a%4$s", args);
            response(Reply.WHOLE_NUMBER);
            return;
        }
        try {
            level = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            response("&a/uadd %1$s %2$s %3$s &c%4$s", args);
            response(Reply.WHOLE_NUMBER);
            return;
        }
        PotionEffectType type = EffectUtils.getEffect(args[1]);
        if (type == null) {
            localized("&c", "actions.effect.invalid");
            response("&a/ulist effects");
            return;
        }
        if (player.hasPotionEffect(type)) {
            EffectUtils.removeEffect(player, type);
        }
        EffectUtils.setEffect(player, type, duration, level);
        localized("&a", "actions.effect.add.success");
    }

    private void lore(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 2) {
            response("&a/uadd lore &c<text...>");
            response(Reply.ARGUMENTS);
            return;
        }
        int index = UberUtils.offset(item);
        StringBuilder message = new StringBuilder(args[1]);
        if (args.length > 2) {
            for (int arg = 2; arg < args.length; arg++) {
                message.append(" ").append(args[arg]);
            }
        }
        String name = ChatUtils.color(message.toString().trim());
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<String>();
        if (meta.hasLore() || (meta.hasLore() && meta.getLore().size() - index > 0)) {
            lore = meta.getLore();
        }
        lore.add(name.replace("%null", ""));
        meta.setLore(lore);
        item.setItemMeta(meta);
        localized("&a", "actions.lore.add.success");
    }

    private void name(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 2) {
            response("&a/uadd name &c<text...>");
            response(Reply.ARGUMENTS);
            return;
        }
        StringBuilder message = new StringBuilder(args[1]);
        for (int arg = 2; arg < args.length; arg++) {
            message.append(" ").append(args[arg]);
        }
        String name = ChatUtils.color(message.toString().trim());
        ItemMeta meta = item.getItemMeta();
        String prev = meta.getDisplayName();
        if (!meta.hasDisplayName()) {
            localized("&c", "actions.name.no_name");
            return;
        }
        if (EconomyUtils.hasEconomy()) {
            double cost = EconomyUtils.getCost("cost.name.add");
            if (EconomyUtils.has(player, cost)) {
                EconomyUtils.withdraw(player, cost);
                meta.setDisplayName(prev + name);
                item.setItemMeta(meta);
                localized("&a", "actions.name.add.pay_success", cost);
            } else {
                localized("&c", "actions.name.add.pay_fail", cost - EconomyUtils.getBalance(player));
            }
        } else {
            meta.setDisplayName(prev + name);
            item.setItemMeta(meta);
            localized("&a", "actions.name.add.success");
        }
    }
}
