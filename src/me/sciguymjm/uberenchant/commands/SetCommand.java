package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.utils.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

/**
 * For internal use.
 */
public class SetCommand extends UberTabCommand {

    @Override
    public boolean onCmd() {
        if (args.length != 0) {
            ItemStack item = player.getInventory().getItemInMainHand();
            switch (args[0].toLowerCase()) {
                case "effect" -> {
                    if (hasPermission("uber.set.effect"))
                        effect();
                    else
                        response(Reply.PERMISSIONS);
                }
                case "lore" -> {
                    if (hasPermission("uber.set.lore"))
                        lore(item);
                    else
                        response(Reply.PERMISSIONS);
                }
                case "name" -> {
                    if (hasPermission("uber.set.name"))
                        name(item);
                    else
                        response(Reply.PERMISSIONS);
                }
                case "hidden" -> {
                    if (hasPermission("uber.set.hidden"))
                        hidden(item);
                    else
                        response(Reply.PERMISSIONS);
                }
                default -> EnchantmentUtils.help(player, "uset");
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
            if (hasPermission("uber.set.effect"))
                list.add("effect");
            if (hasPermission("uber.set.lore"))
                list.add("lore");
            if (hasPermission("uber.set.name"))
                list.add("name");
            if (hasPermission("uber.set.hidden"))
                list.add("hidden");
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("hidden")) {
                if (hasPermission("uber.set.hidden")) {
                    list.add("true");
                    list.add("false");
                }
            }
        }
        return list;
    }

    private void effect() {
        if (args.length < 4) {
            response("&a/uset effect &c<effect | id> <duration> <level>");
            response(Reply.ARGUMENTS);
            return;
        }
        int level = 1;
        int duration = 60;
        try {
            duration = Integer.parseInt(args[2]);
            duration *= 20;
        } catch (NumberFormatException e) {
            response("&a/uset %1$s %2$s %3$s &c%4$s", args);
            response(Reply.WHOLE_NUMBER);
            return;
        }
        try {
            level = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            response("&a/uset %1$s %2$s &c%3$s &a%4$s", args);
            response(Reply.WHOLE_NUMBER);
            return;
        }
        PotionEffectType type = EffectUtils.getEffect(args[1]);
        if (type == null) {
            response("&c" + UberLocale.get("actions.effect.invalid"));
            response("&a/ulist effects");
            return;
        }
        if (player.hasPotionEffect(type))
            EffectUtils.removeEffect(player, type);
        EffectUtils.setEffect(player, type, duration, level);
        response("&a" + UberLocale.get("actions.effect.set.success"));
    }

    private void lore(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 3) {
            response("&a/uset lore &c<line#> <text...>");
            response(Reply.ARGUMENTS);
            return;
        }
        int index = UberUtils.offset(item);
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore() || (meta.hasLore() && meta.getLore().size() - index == 0)) {
            response("&c" + UberLocale.get("actions.lore.set.no_lore"));
            return;
        }
        List<String> lore = meta.getLore();
        int size = lore.size() - index;
        int line = -1;
        try {
            line = Integer.parseInt(args[1]);
        } catch (NumberFormatException err) {
            StringBuilder message = new StringBuilder("&a/uset lore &c%1$s &a%2$s");
            if (args.length > 3) {
                for (int arg = 3; arg < args.length; arg++) {
                    message.append(" ").append(args[arg]);
                }
            }
            response(message.toString().trim(), args[1], args[2]);
            response(Reply.WHOLE_NUMBER);
            return;
        }
        if (line > (size - 1) || line < 0) {
            StringBuilder message = new StringBuilder("&a/uset lore &c%1$s &a%2$s");
            if (args.length > 3) {
                for (int arg = 3; arg < args.length; arg++) {
                    message.append(" ").append(args[arg]);
                }
            }
            response(message.toString().trim(), args[1], args[2]);
            response("&c" + UberLocale.get("actions.lore.set.no_line"));
            return;
        }
        StringBuilder message = new StringBuilder(args[2]);
        if (args.length > 3) {
            for (int arg = 3; arg < args.length; arg++) {
                message.append(" ").append(args[arg]);
            }
        }
        String name = ChatUtils.color(message.toString().trim());
        lore.set(index + line, name.replace("%null", ""));
        meta.setLore(lore);
        item.setItemMeta(meta);
        response("&a" + UberLocale.get("actions.lore.set.success"));
    }

    private void name(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 2) {
            response("&a/uset name &c<text...>");
            response(Reply.ARGUMENTS);
            return;
        }
        StringBuilder message = new StringBuilder(args[1]);
        for (int arg = 2; arg < args.length; arg++) {
            message.append(" ").append(args[arg]);
        }
        String name = ChatUtils.color(message.toString().trim());
        ItemMeta meta = item.getItemMeta();
        if (hasPermission("uber.set.name.free")) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
            response("&a" + UberLocale.get("actions.name.set.success"));
            return;
        }
        if (EconomyUtils.hasEconomy()) {
            double cost = EconomyUtils.getCost("cost.name.set");
            if (EconomyUtils.has(player, cost)) {
                EconomyUtils.withdraw(player, cost);
                meta.setDisplayName(name);
                item.setItemMeta(meta);
                response("&a" + UberLocale.get("actions.name.set.pay_success", cost));
            } else {
                response("&c" + UberLocale.get("actions.name.set.pay_fail", cost - EconomyUtils.getBalance(player)));
            }
        } else {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
            response("&a" + UberLocale.get("actions.name.set.success"));
        }
    }

    private void hidden(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 2) {
            response("&a/uset hidden &c<true | false>");
            response(Reply.ARGUMENTS);
            return;
        }
        switch (args[1].toLowerCase()) {
            case "false" -> response(EnchantmentUtils.setHideEnchants(item, false));
            case "true" -> response(EnchantmentUtils.setHideEnchants(item, true));
            default -> {
                response("&a/uset hidden &c%1$s", args[1]);
                response(Reply.INVALID);
            }
        }
    }
}
