package me.sciguymjm.uberenchant.commands.abstraction;

import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

/**
 * For internal use.
 */
public interface IUberCommand extends CommandExecutor {

    /**
     * Simplified version of {@link #onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, String, String[])}
     *
     * @param player The player
     * @param args   The arguments
     * @return True if command was successful
     * @hidden
     */
    public boolean onCmd();

}
