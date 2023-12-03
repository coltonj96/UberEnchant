package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.utils.EffectUtils;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentUtils;
import me.sciguymjm.uberenchant.utils.Reply;

import java.util.ArrayList;
import java.util.List;

/**
 * For internal use.
 */
public class ListCommand extends UberTabCommand {

    @Override
    public boolean onCmd() {
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "enchants" -> {
                    if (hasPermission("uber.list.enchants"))
                        response(EnchantmentUtils.listEnchants());
                    else
                        response(Reply.PERMISSIONS);
                }
                case "effects" -> {
                    if (hasPermission("uber.list.effects"))
                        response(EffectUtils.listEffects());
                    else
                        response(Reply.PERMISSIONS);
                }
                default -> EnchantmentUtils.help(player, "ulist");
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
            if (hasPermission("uber.list.enchants"))
                list.add("enchants");
            if (hasPermission("uber.list.effects"))
                list.add("effects");
        }
        return list;
    }
}
