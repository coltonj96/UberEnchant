package me.sciguymjm.uberenchant.commands.abstraction;

import me.sciguymjm.uberenchant.UberEnchant;
import me.sciguymjm.uberenchant.commands.*;
import me.sciguymjm.uberenchant.utils.ChatUtils;
import me.sciguymjm.uberenchant.utils.Reply;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

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

    public UberCommand(String name) {
        UberEnchant.instance().getCommand(name).setExecutor(this);
    }

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

    protected <T> void action(String node, AbstractAction<T> action, T t) {
        if (hasPermission(command.getPermission() + "." + node))
            action.run(t);
        else
            response(Reply.PERMISSIONS);
    }

    protected void action(String node, Action action) {
        if (hasPermission(command.getPermission() + "." + node))
            action.run();
        else
            response(Reply.PERMISSIONS);
    }

    protected void action(Action action) {
        if (hasPermission())
            action.run();
        else
            response(Reply.PERMISSIONS);
    }

    protected String argue(String string) {
        String[] msg = string.split(" ");
        if (msg.length - 1 == args.length)
            return String.join(" ", msg);
        int i;
        for (i = 0; i < args.length; i++)
            if (i + 1 < msg.length)
                msg[i + 1] = args[i].toLowerCase();
        if (i + 1 < msg.length)
            msg[i + 1] = "&c" + msg[i];
        return String.join(" ", msg);
    }

    protected void add(List<String> list, String node, String value, boolean condition) {
        if (condition && hasPermission(node))
            list.add(value);
    }

    protected void add(List<String> list, String node, String value) {
        add(list, node, value, true);
    }

    protected void add(List<String> list, List<String> nodes, String value) {
        nodes.forEach(node -> add(list, node, value));
    }

    protected void add(List<String> list, String node, String... values) {
        if (hasPermission(node))
            list.addAll(List.of(values));
    }

    protected void addAll(List<String> list, Map<String, String> nodes) {
        nodes.forEach((k, v) -> {
            if (hasPermission(k))
                list.add(v);
        });
    }

    protected void addAll(List<String> list, String... pairs) {
        if (pairs.length % 2 == 0)
            for (int i = 0; i < pairs.length; i += 2)
                if (hasPermission(pairs[i]))
                    list.add(pairs[i + 1]);
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

    public static void init() {
        new AddCommand();
        new ClearCommand();
        new CostCommand();
        new DelCommand();
        new ExtractCommand();
        new HelpCommand();
        new InsertCommand();
        new ItemCommand();
        new ListCommand();
        new ReloadCommand();
        new SetCommand();
    }
}
