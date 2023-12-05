package me.sciguymjm.uberenchant.commands.abstraction;

import me.sciguymjm.uberenchant.utils.ChatUtils;
import me.sciguymjm.uberenchant.utils.Reply;
import me.sciguymjm.uberenchant.utils.UberLocale;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Abstract class for internal use.
 */
public abstract class UberCommand implements IUberCommand {

    /**
     * For internal use.
     */
    protected Player player;

    /**
     * For internal use.
     */
    protected Command command;

    protected String[] args;

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.command = command;
        if (sender instanceof Player player) {
            this.player = player;
            if (!hasPermission()) {
                response(Reply.PERMISSIONS);
                return true;
            }
            this.args = args;
            return onCmd();
        }
        return false;
    }

    /**
     * Checks if the player running the command has the specified permission
     *
     * @param node The permission node
     * @return True if the player has permission or not
     * @hidden
     */
    public final boolean hasPermission(String node) {
        return player.hasPermission(node);
    }

    /**
     * Checks if the player running the command has the specified permission (Formatted)
     *
     * @param node The permission node
     * @param args Arguments
     * @return True if the player has permission or not
     * @hidden
     */
    public final boolean hasPermission(String node, Object... args) {
        return player.hasPermission(node.formatted(args));
    }

    /**
     * Checks if the player running the command has the specified permission (Formatted)
     *
     * @param node The permission node
     * @param args Arguments
     * @return True if the player has permission or not
     * @hidden
     */
    public final boolean hasPermission(String node, String[] args) {
        return player.hasPermission(node.formatted((Object[]) args));
    }

    /**
     * Checks if the player running the command has the permission of the
     * command
     *
     * @return True if the player has permission or not
     * @hidden
     */
    public final boolean hasPermission() {
        return player.hasPermission(command.getPermission());
    }

    /**
     * Sends the player running the command a formatted message
     *
     * @param message The message
     * @hidden
     */
    public final void response(String message) {
        ChatUtils.response(player, message);
    }

    public final void response(Reply reply) {
        ChatUtils.response(player, reply.get());
    }

    public final void localized(String color, String key) {
        ChatUtils.localized(player, color, key);
    }

    public final void localized(String color, String key, Object... args) {
        ChatUtils.localized(player, color, key, args);
    }

    /**
     * Sends the player running the command a formatted message
     *
     * @param message The message
     * @param args    The formatting arguments
     * @hidden
     */
     public final void response(String message, Object... args) {
         ChatUtils.response(player, message, args);
     }

    /**
     * Sends the player running the command a formatted message
     *
     * @param message The message
     * @param args    The formatting arguments
     * @hidden
     */
    public final void response(String message, String[] args) {
        ChatUtils.response(player, message, args);
    }

    /**
     * Sends the player running the command an array of messages
     *
     * @param messages The messages
     * @hidden
     */
    public final void response(String[] messages) {
        ChatUtils.response(player, messages);
    }
}
