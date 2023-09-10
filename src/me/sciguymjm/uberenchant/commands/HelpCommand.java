package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.commands.abstraction.UberCommand;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.utils.EnchantmentUtils;
import me.sciguymjm.uberenchant.utils.Reply;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

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
        List<String> list = new ArrayList<String>();
        if (args.length == 1) {
            list.add("ulist");
            list.add("uadd");
            list.add("ucost");
            list.add("udel");
            list.add("uextract");
            list.add("uset");
            list.add("uinsert");
            list.add("uclear");
            list.add("ureload");
        }
        return list;
    }
}
