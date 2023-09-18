package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.commands.abstraction.UberCommand;
import me.sciguymjm.uberenchant.utils.Reply;
import org.bukkit.command.CommandExecutor;

/**
 * For internal use.
 */
public class ReloadCommand extends UberCommand implements CommandExecutor {

    @Override
    public boolean onCmd() {
        if (hasPermission("uber.reload"))
            UberConfiguration.reloadAll();
        else
            response(Reply.PERMISSIONS);
        return true;
    }
}
