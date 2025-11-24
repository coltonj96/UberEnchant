package me.sciguymjm.uberenchant.commands.abstraction;

import me.sciguymjm.uberenchant.utils.ChatUtils;
import me.sciguymjm.uberenchant.utils.Reply;
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

    protected CommandSender sender;

    /**
     * For internal use.
     */
    protected Command command;

    protected String[] args;

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.command = command;
        this.sender = sender;
        this.args = args;
        if (sender instanceof Player plyr)
            this.player = plyr;
        if (!hasPermission()) {
            response(Reply.PERMISSIONS);
            return true;
        }
        return onCmd();
    }

    /**
     * Checks if the player running the command has the specified permission
     *
     * @param node The permission node
     * @return True if the player has permission or not
     * @hidden
     */
    public final boolean hasPermission(String node) {
        return sender.hasPermission(node);
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
        return sender.hasPermission(node.formatted(args));
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
        return sender.hasPermission(node.formatted((Object[]) args));
    }

    /**
     * Checks if the player running the command has the permission of the
     * command
     *
     * @return True if the player has permission or not
     * @hidden
     */
    public final boolean hasPermission() {
        return sender.hasPermission(command.getPermission());
    }

    /**
     * Sends the player running the command a formatted message
     *
     * @param message The message
     * @hidden
     */
    public final void response(String message) {
        ChatUtils.response(sender, message);
    }

    public final void response(Reply reply) {
        ChatUtils.response(sender, reply.get());
    }

    public final void localized(String color, String key) {
        ChatUtils.localized(sender, color, key);
    }

    public final void localized(String color, String key, Object... args) {
        ChatUtils.localized(sender, color, key, args);
    }

    /**
     * Sends the player running the command a formatted message
     *
     * @param message The message
     * @param args    The formatting arguments
     * @hidden
     */
     public final void response(String message, Object... args) {
         ChatUtils.response(sender, message, args);
     }

    /**
     * Sends the player running the command a formatted message
     *
     * @param message The message
     * @param args    The formatting arguments
     * @hidden
     */
    public final void response(String message, String[] args) {
        ChatUtils.response(sender, message, args);
    }

    /**
     * Sends the player running the command an array of messages
     *
     * @param messages The messages
     * @hidden
     */
    public final void response(String[] messages) {
        ChatUtils.response(sender, messages);
    }
}
