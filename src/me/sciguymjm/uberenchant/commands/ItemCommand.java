package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.utils.ItemFileUtils;
import me.sciguymjm.uberenchant.utils.Reply;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemCommand extends UberTabCommand {

    @Override
    public boolean onCmd() {
        if (args.length != 0) {
            switch (args[0].toLowerCase()) {
                case "save" -> {
                    if (hasPermission("uber.item.save"))
                        save();
                    else
                        response(Reply.PERMISSIONS);
                }
                case "load" -> {
                    if (hasPermission("uber.item.load"))
                        load();
                    else
                        response(Reply.PERMISSIONS);
                }
                case "delete" -> {
                    if (hasPermission("uber.item.delete"))
                        delete();
                    else
                        response(Reply.PERMISSIONS);
                }
                default -> EnchantmentUtils.help(player, "uitem");
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
            if (hasPermission("uber.item.save"))
                list.add("save");
            if (hasPermission("uber.item.load"))
                list.add("load");
            if (hasPermission("uber.item.delete"))
                list.add("delete");
        }
        if (args.length == 2)
            if (args[0].equalsIgnoreCase("load") || args[0].equalsIgnoreCase("delete"))
                list.addAll(ItemFileUtils.getNames());
        if (args.length == 3 && args[0].equalsIgnoreCase("load") && hasPermission("uber.item.load.others"))
            list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());

        return list;
    }

    private void save() {
        if (player == null) {
            localized("&c", "actions.item.console", args[0]);
            return;
        }
        if (args.length < 2) {
            response(Reply.ARGUMENTS);
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length == 2) {
            ItemFileUtils.save(item, args[1]);
            localized("&a", "actions.item.save.success", args[1]);
        }
    }

    private void load() {
        boolean other = false;
        if (args.length < 2) {
            response(Reply.ARGUMENTS);
            return;
        }
        if (args.length == 3) {
            if (!hasPermission("uber.item.load.others")) {
                response(Reply.PERMISSIONS);
                return;
            }
            player = Bukkit.getPlayerExact(args[2]);
            if (player == null) {
                localized("&c", "actions.item.load.player.fail");
                return;
            }
            other = true;
        }
        if (player == null) {
            localized("&c", "actions.item.console", args[0]);
            return;
        }
        ItemStack item = null;
        if (args.length >= 2)
            item = ItemFileUtils.load(args[1]);
        if (item != null) {
            HashMap<Integer, ItemStack> map = player.getInventory().addItem(item);
            if (!map.isEmpty()) {
                player.getWorld().dropItemNaturally(player.getLocation(), map.get(0));
                if (other)
                    localized("&c", "actions.item.load.player.inventory", player.getDisplayName());
                else
                    localized("&c", "actions.item.load.inventory");
            } else {
                if (other)
                    localized("&a", "actions.item.load.player.success", player.getDisplayName(), args[1]);
                else
                    localized("&a", "actions.item.load.success", args[1]);
            }
        } else {
            localized("&c", "actions.item.load.fail", args[1]);
        }
    }

    private void delete() {
        if (args.length < 2) {
            response(Reply.ARGUMENTS);
            return;
        }
        if (ItemFileUtils.delete(args[1]))
            localized("&a", "actions.item.delete.success", args[1]);
        else
            localized("&c", "actions.item.delete.fail", args[1]);
    }
}
