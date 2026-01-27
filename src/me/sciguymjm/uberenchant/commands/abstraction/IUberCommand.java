package me.sciguymjm.uberenchant.commands.abstraction;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

/**
 * For internal use.
 */
public interface IUberCommand extends CommandExecutor {

    /**
     * Simplified version of {@link #onCommand(CommandSender, Command, String, String[])}
     *
     * @return True if command was successful
     * @hidden
     */
    boolean onCmd();

    interface Action {
        void run();
    }

    interface AbstractAction<T> {
        void run(T t);
    }

    interface ItemAction {
        void apply(ItemStack item);
    }

    interface Condition {
        boolean test();
    }
}
