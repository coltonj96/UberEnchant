package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.UberRunnable;
import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.BoolTag;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.DoubleTag;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.IntTag;
import me.sciguymjm.uberenchant.api.utils.persistence.UberMeta;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.MetaTag;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import me.sciguymjm.uberenchant.enchantments.tasks.HeldEffectTask;
import me.sciguymjm.uberenchant.utils.*;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                case "meta" -> {
                    if (hasPermission("uber.set.meta"))
                        meta(item);
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
                /*case "owner" -> {
                    if (hasPermission("uber.set.owner"))
                        owner(item);
                    else
                        response(Reply.PERMISSIONS);
                }*/
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
        switch (args.length) {
            case 1 -> {
                if (hasPermission("uber.set.effect"))
                    list.add("effect");
                if (hasPermission("uber.set.lore"))
                    list.add("lore");
                if (VersionUtils.isAtLeast("1.20.4") && hasPermission("uber.set.meta"))
                    list.add("meta");
                if (hasPermission("uber.set.name"))
                    list.add("name");
                if (hasPermission("uber.set.hidden"))
                    list.add("hidden");
                /*if (hasPermission("uber.set.owner"))
                    list.add("owner");*/
            }
            case 2 -> {
                switch (args[0].toLowerCase()) {
                    case "hidden" -> {
                        if (hasPermission("uber.set.hidden")) {
                            list.add("true");
                            list.add("false");
                        }
                    }
                    case "effect" -> list = EffectUtils.matchEffects(args[1]);
                    case "meta" -> {
                        if (!VersionUtils.isAtLeast("1.20.4"))
                            return list;
                        ItemStack item = player.getInventory().getItemInMainHand();
                        Map<UberEnchantment, Integer> map = UberUtils.getMap(item);
                        if (!item.getType().equals(Material.AIR) && !map.isEmpty())
                            list = map.keySet().stream().map(key -> key.getKey().getKey().toLowerCase()).toList();
                    }
                }
            }
        }
        if (args.length >= 3 && args[0].equalsIgnoreCase("meta")) {
            if (args.length == 3) {
                ItemStack item = player.getInventory().getItemInMainHand();
                list = UberUtils.getTags(item, args[1]).stream().map(MetaTag::getName).filter(name ->
                        hasPermission(String.format("uber.set.meta.%1$s", name))
                ).toList();
            }
            if (args.length == 4 && BoolTag.matches(args[2])) {
                list.add("true");
                list.add("false");
            }
        }
        return list;
    }

    private void meta(ItemStack item) {
        /*UberEnchantmentsAddedEvent event = new UberEnchantmentsAddedEvent(player, item, null);
        if (event.isCancelled())
            return;*/
        if (!VersionUtils.isAtLeast("1.20.4"))
            return;
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 4) {
            response("&a/uset meta &c<enchantment> <tag> <value>");
            response(Reply.ARGUMENTS);
            return;
        }

        Set<UberEnchantment> set = EnchantmentUtils.getMatches(item, args[1]);
        if (EnchantmentUtils.multi(player, set))
            return;
        UberEnchantment enchant = set.iterator().next();

        if (!enchant.containsEnchantment(item)) {
            localized("&c", "actions.meta.enchantment_not_found");
            return;
        }

        if (!UberMeta.contains(args[2])) {
            localized("&c", "actions.meta.invalid_tag");
            return;
        }

        UberMeta<?> meta = UberMeta.getByName(args[2]);

        if (meta == null) {
            localized("&c", "actions.meta.invalid_tag");
            return;
        }

        if (!UberUtils.containsMeta(item, enchant, meta)) {
            localized("&c", "actions.meta.set.add_tag");
            return;
        }

        boolean success = false;

        if (meta.getTag() instanceof BoolTag tag)  {
            boolean value;
            switch (args[3].toLowerCase()) {
                case "true", "t", "1":
                    value = true;
                    break;
                case "false", "f", "0":
                    value = false;
                    break;
                default:
                    localized("&c", "actions.meta.invalid_bool_value");
                    response("&a/uset %1$s %2$s %3$s &c%4$s", args);
                    return;
            }
            if (enchant instanceof EffectEnchantment effect && tag == BoolTag.ON_HELD && !BoolTag.ON_HELD.test(item, effect) && value)
                UberRunnable.addTask(new HeldEffectTask(player, effect, (p, i, e) ->
                        i.getType().equals(Material.AIR) ||
                                !e.containsEnchantment(i) ||
                                !BoolTag.ON_HELD.test(i, e)));
            UberUtils.setMetaTag(item, enchant, tag.asMeta(), value);
            success = true;
        }
        if (!success && meta.getTag() instanceof IntTag tag)  {
            int value;
            try {
                value = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                response("&a/uset %1$s %2$s %3$s &c%4$s", args);
                response(Reply.WHOLE_NUMBER);
                return;
            }
            UberUtils.setMetaTag(item, enchant, tag.asMeta(), value);
            success = true;
        }
        if (!success && meta.getTag() instanceof DoubleTag tag)  {
            double value;
            try {
                value = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                response("&a/uset %1$s %2$s %3$s &c%4$s", args);
                response(Reply.DECIMAL_NUMBER);
                return;
            }
            if (value <= 0.0 || value >= 1.0) {
                localized("&c", "actions.meta.invalid_double_value");
                return;
            }
            UberUtils.addMetaData(item, enchant, tag.asMeta(), value);
            success = true;
        }
        if (success)
            localized("&a", "actions.meta.set.success");
        else
            localized("&c", "actions.meta.set.fail");
    }

    private void owner(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }

        UberUtils.setOwner(item, player.getUniqueId());

        localized("&a", "actions.owner.set.success");
    }

    private void effect() {
        if (args.length < 4) {
            response("&a/uset effect &c<effect> <duration> <level>");
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
            localized("&c", "actions.effect.invalid");
            response("&a/ulist effects");
            return;
        }
        if (player.hasPotionEffect(type))
            EffectUtils.removeEffect(player, type);
        EffectUtils.setEffect(player, type, duration, level);
        localized("&a", "actions.effect.set.success");
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
            localized("&c", "actions.lore.set.no_lore");
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
            localized("&c", "actions.lore.set.no_line");
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
        localized("&a", "actions.lore.set.success");
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
        if (hasPermission("uber.set.name.free") || !EconomyUtils.useEconomy()) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
            localized("&a", "actions.name.set.success");
            return;
        }
        if (EconomyUtils.hasEconomy()) {
            double cost = EconomyUtils.getCost("cost.name.set");
            if (EconomyUtils.has(player, cost)) {
                EconomyUtils.withdraw(player, cost);
                meta.setDisplayName(name);
                item.setItemMeta(meta);
                localized("&a", "actions.name.set.pay_success", cost);
            } else {
                localized("&c", "actions.name.set.pay_fail", cost - EconomyUtils.getBalance(player));
            }
        } else {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
            localized("&a", "actions.name.set.success");
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
