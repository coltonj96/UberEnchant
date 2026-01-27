package me.sciguymjm.uberenchant.commands.abstraction;

import me.sciguymjm.uberenchant.UberEnchant;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Abstract class for internal use.
 */
public abstract class UberTabCommand extends UberCommand implements IUberTabComplete {

    public UberTabCommand(String name) {
        super(name);
        UberEnchant.instance().getCommand(name).setTabCompleter(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        this.sender = sender;
        this.args = args;
        if (sender instanceof Player player) {
            this.player = player;
            return onTab().stream().filter(l ->
                    args[args.length-1].isEmpty() || l.startsWith(args[args.length-1]) || l.contains(args[args.length-1])
            ).toList();
        }
        return null;
    }
}
