package me.sciguymjm.uberenchant.commands.abstraction;

import me.sciguymjm.uberenchant.utils.Reply;
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
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&l[&5UberEnchant&8&l] %1$s".formatted(message)));
    }

    /**
     * Sends the player running the command a formatted message
     *
     * @param message The message
     * @param args    The formatting arguments
     * @hidden
     */
     public final void response(String message, Object... args) {
         player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&l[&5UberEnchant&8&l] %1$s".formatted(message.formatted(args))));
     }

    /**
     * Sends the player running the command a formatted message
     *
     * @param message The message
     * @param args    The formatting arguments
     * @hidden
     */
    public final void response(String message, String[] args) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&l[&5UberEnchant&8&l] %1$s".formatted(message.formatted((Object[]) args))));
    }

    /**
     * Sends the player running the command an array of messages
     *
     * @param messages The messages
     * @hidden
     */
    public final void response(String[] messages) {
        messages[0] = ChatColor.translateAlternateColorCodes('&', "&8&l[&5UberEnchant&8&l] %1$s".formatted(messages[0]));
        for (int i = 1; i < messages.length; i++) {
            messages[i] = ChatColor.translateAlternateColorCodes('&', messages[i]);
        }
        player.sendMessage(messages);
    }
}
