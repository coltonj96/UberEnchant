package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.commands.abstraction.UberCommand;
import me.sciguymjm.uberenchant.utils.ChatUtils;
import me.sciguymjm.uberenchant.utils.Reply;
import me.sciguymjm.uberenchant.utils.UberLocale;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * For internal use.
 */
public class InsertCommand extends UberCommand {

    @Override
    public boolean onCmd() {
        if (args.length != 0) {
            if (args[0].equalsIgnoreCase("lore")) {
                if (hasPermission("uber.insert.lore")) {
                    lore(player.getInventory().getItemInMainHand());
                    return true;
                } else {
                    response(Reply.PERMISSIONS);
                }
            }
        } else {
            response("&6%1$s", command.getUsage());
        }
        return true;
    }

    private void lore(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 3) {
            response("&a/uinsert lore &c<line#> <text...>");
            response(Reply.ARGUMENTS);
            return;
        }
        int index = UberUtils.offset(item);
        ItemMeta meta = (ItemMeta) item.getItemMeta();
        if (!meta.hasLore() || (meta.hasLore() && meta.getLore().size() - index == 0)) {
            response("&c" + UberLocale.get("actions.lore.insert.no_lore"));
            return;
        }
        List<String> lore = meta.getLore();
        int size = lore.size() - index;
        int line = -1;
        try {
            line = Integer.parseInt(args[1]);
        } catch (NumberFormatException err) {
            StringBuilder message = new StringBuilder("&a/uinsert lore &c%1$s &a%2$s");
            if (args.length > 3) {
                for (int arg = 3; arg < args.length; arg++) {
                    message.append(" ").append(args[arg]);
                }
            }
            response(message.toString().trim(), args[1], args[2]);
            response("&c" + UberLocale.get("actions.lore.insert.line_number"));
            return;
        }
        if (line > (size - 1) || line < 0) {
            StringBuilder message = new StringBuilder("&a/uinsert lore &c%1$s &a%2$s");
            if (args.length > 3) {
                for (int arg = 3; arg < args.length; arg++) {
                    message.append(" ").append(args[arg]);
                }
            }
            response(message.toString().trim(), args[1], args[2]);
            response("&c" + UberLocale.get("actions.lore.insert.no_line"));
            return;
        }
        if (Integer.toString(line).contains(".")) {
            StringBuilder message = new StringBuilder("&a/uinsert lore &c%1$s &a%2$s");
            if (args.length > 3) {
                for (int arg = 3; arg < args.length; arg++) {
                    message.append(" ").append(args[arg]);
                }
            }
            response(message.toString().trim(), args[1], args[2]);
            response(Reply.WHOLE_NUMBER);
            return;
        }
        StringBuilder message = new StringBuilder(args[2]);
        if (args.length > 3) {
            for (int arg = 3; arg < args.length; arg++) {
                message.append(" ").append(args[arg]);
            }
        }
        String name = ChatUtils.color(message.toString().trim());
        lore.add(index + line, name.replace("%null", ""));
        meta.setLore(lore);
        item.setItemMeta(meta);
        response("a" + UberLocale.get("actions.lore.insert.success"));
    }
}
