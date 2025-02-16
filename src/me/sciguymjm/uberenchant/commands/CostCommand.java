package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.utils.EconomyUtils;
import me.sciguymjm.uberenchant.utils.Reply;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentUtils;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * For internal use.
 */
public class CostCommand extends UberTabCommand {

    @Override
    public boolean onCmd() {
        if (args.length != 0) {
            switch (args[0].toLowerCase()) {
                case "add" -> {
                    if (args[1].equalsIgnoreCase("enchant")) {
                        if (hasPermission("uber.cost.add.enchant"))
                            addEnchant();
                        else
                            response(Reply.PERMISSIONS);
                    } else {
                        response("&a/ucost add &cenchant &a<enchantment> <level>");
                        response(Reply.INVALID);
                    }
                }
                case "del" -> {
                    if (args[1].equalsIgnoreCase("enchant")) {
                        if (hasPermission("uber.cost.del.enchant"))
                            removeEnchant();
                        else
                            response(Reply.PERMISSIONS);
                    } else {
                        response("&a/ucost extract &cenchant &a<enchantment>");
                        response(Reply.INVALID);
                    }
                }
                case "extract" -> {
                    if (args[1].equalsIgnoreCase("enchant")) {
                        if (hasPermission("uber.cost.extract.enchant"))
                            extractEnchant();
                        else
                            response(Reply.PERMISSIONS);
                    } else {
                        response("&a/ucost extract &cenchant &a<enchantment> <level>");
                        response(Reply.INVALID);
                    }
                }
                default -> EnchantmentUtils.help(player, "ucost");
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
                if (hasPermission("uber.add.enchant"))
                    list.add("add");
                if (hasPermission("uber.del.enchant"))
                    list.add("del");
                if (hasPermission("uber.extract.enchant"))
                    list.add("extract");
            }
            case 2 -> {
                if (hasPermission("uber.add.enchant") || hasPermission("uber.del.enchant") || hasPermission("uber.extract.enchant"))
                    list.add("enchant");
            }
            case 3 -> {
                switch (args[1].toLowerCase()) {
                    case "enchant":
                        list = EnchantmentUtils.find(player, args[2]);
                        break;
                    case "effect":
                        break;
                }
            }
        }
        return list;
    }

    private void addEnchant() {
        if (args.length < 4) {
            response("&a/ucost add enchant &c<enchantment | id> <level>");
            response(Reply.ARGUMENTS);
            return;
        }
        int level = 0;
        try {
            level = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            response("&a/ucost %1$s %2$s %3$s &c%4$s", args);
            response(Reply.WHOLE_NUMBER);
            return;
        }
        Set<Enchantment> set = EnchantmentUtils.getMatches(args[2]);
        if (EnchantmentUtils.multi(player, set))
            return;
        Enchantment enchantment = set.iterator().next();
        if (enchantment == null) {
            localized("&c", "actions.enchant.invalid");
            response("&a/ulist enchants");
            return;
        }
        UberConfiguration.UberRecord e = UberConfiguration.getByEnchant(enchantment);
        if (EconomyUtils.hasEconomy()) {
            if (level >= e.getMinLevel() && level <= e.getMaxLevel()) {
                double cost = e.getCostForLevel().containsKey(level) ? e.getCostForLevel().get(level) : e.getCost() + (e.getCostMultiplier() * e.getCost() * (level - 1));
                localized("&a", "actions.cost.add.display", e.getName(), level, cost);
            } else {
                localized("&c", "actions.enchant.range", e.getMinLevel(), e.getMaxLevel());
            }
            return;
        }
        response(Reply.NO_ECONOMY);
    }

    private void extractEnchant() {
        if (args.length < 4) {
            response("&a/ucost extract enchant &c<enchantment | id> <level>");
            response(Reply.ARGUMENTS);
            return;
        }
        if (EconomyUtils.hasEconomy()) {
            int level = 0;
            try {
                level = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                response("&a/ucost %1$s %2$s %3$s &c%4$s", args);
                response(Reply.WHOLE_NUMBER);
                return;
            }
            Set<Enchantment> set = EnchantmentUtils.getMatches(args[2]);
            if (EnchantmentUtils.multi(player, set))
                return;
            Enchantment enchantment = set.iterator().next();
            if (enchantment == null) {
                localized("&c", "actions.enchant.invalid");
                response("&a/ulist enchants");
                return;
            }
            UberConfiguration.UberRecord e = UberConfiguration.getByEnchant(enchantment);
            localized("&a", "actions.cost.extract.display", e.getName(), level, e.getExtractionCost() + (e.getCostMultiplier() * e.getExtractionCost() * (level - 1)));
            return;
        }
        response(Reply.NO_ECONOMY);
    }

    private void removeEnchant() {
        if (args.length != 3) {
            response("&a/ucost del enchant &c<enchantment | id>");
            response(Reply.ARGUMENTS);
            return;
        }
        if (EconomyUtils.hasEconomy()) {
            Set<Enchantment> set = EnchantmentUtils.getMatches(args[2]);
            if (EnchantmentUtils.multi(player, set))
                return;
            Enchantment enchantment = set.iterator().next();
            if (enchantment != null) {
                UberConfiguration.UberRecord enchant = UberConfiguration.getByEnchant(enchantment);

                localized("&a", "actions.cost.remove.display", enchant.getName(), enchant.getRemovalCost());
                return;
            }
            localized("&c", "actions.enchant.not_exist");
        }
        response(Reply.NO_ECONOMY);
    }
}
