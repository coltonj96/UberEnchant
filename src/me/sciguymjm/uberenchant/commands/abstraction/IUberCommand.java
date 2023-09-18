package me.sciguymjm.uberenchant.commands.abstraction;

import org.bukkit.command.CommandExecutor;

/**
 * For internal use.
 */
public interface IUberCommand extends CommandExecutor {

    /**
     * Simplified version of {@link #onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, String, String[])}
     *
     * @return True if command was successful
     * @hidden
     */
    public boolean onCmd();

}
