package me.sciguymjm.uberenchant.commands.abstraction;

import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * For internal use.
 */
public interface IUberTabComplete extends TabCompleter {

    /**
     * Simplified version of
     * {@link #onTabComplete(org.bukkit.command.CommandSender, org.bukkit.command.Command, String, String[])}
     *
     * @return A list of autocomplete options
     * @hidden
     */
    public List<String> onTab();
}
