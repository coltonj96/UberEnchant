package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.UberEnchant;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.utils.Reply;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * For internal use.
 */
public class HelpCommand extends UberTabCommand {

    @Override
    public boolean onCmd() {
        if (hasPermission("uber.help")) {
            if (args.length == 0)
                EnchantmentUtils.help(player, "all");
            if (args.length == 1)
                EnchantmentUtils.help(player, args[0]);
        } else {
            response(Reply.PERMISSIONS);
        }
        return true;
    }

    @Override
    public List<String> onTab() {
        List<String> list = new ArrayList<>();
        if (args.length == 1)
            list.addAll(UberEnchant.instance().getDescription().getCommands().keySet());
        return list;
    }
}
