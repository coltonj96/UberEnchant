package me.sciguymjm.uberenchant.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A chat-related utility class
 */
public class ChatUtils {

    /**
     * Sends the specified player a specific reply.
     *
     * @param sender CommandSender
     * @param reply  Reply
     */
    public static void response(CommandSender sender, Reply reply) {
        response(sender, reply.get());
    }

    /**
     * Sends the specified player a message
     *
     * @param message The message
     * @hidden
     */
    public static void response(CommandSender sender, String message) {
        response(sender, message, "");
    }

    public static void localized(CommandSender sender, String color, String key) {
        response(sender, UberLocale.getC(color, key));
    }

    public static void localized(CommandSender sender, String color, String key, Object... args) {
        response(sender, UberLocale.getCF(color, key, args));
    }

    /**
     * Sends the specified player a formated message.<br>
     * Usage:
     *
     * <pre>{@code
     * Playe p = new Player("john_doe"); // Psuedo code
     * ChatUtils.response(p, "This is a message for %1$s", p.getName());
     * // outputs "[UberEnchant] This is a message for john_doe"
     * }</pre>
     *
     * @param sender  - The sender
     * @param message - The message
     * @param args    - The arguments for the message
     */
    public static void response(CommandSender sender, String message, Object... args) {
        sendMessage(sender, color("&8&l[&5UberEnchant&8&l] %1$s".formatted(message).formatted(args)));
    }

    /**
     * Sends the specified player a formatted message
     *
     * @param message The message
     * @param args    The formatting arguments
     * @hidden
     */
    public static void response(CommandSender sender, String message, String[] args) {
        response(sender, message, (Object[]) args);
    }

    /**
     * Sends the specified player an array of messages.
     *
     * @param sender   - The player
     * @param messages - The array of messages
     */
    public static void response(CommandSender sender, String[] messages) {
        messages[0] = color("&8&l[&5UberEnchant&8&l] %1$s".formatted(messages[0]));
        for (int i = 1; i < messages.length; i++)
            messages[i] = color(messages[i]);
        if (sender instanceof Conversable person && person.isConversing())
            Arrays.stream(messages).forEach(person::sendRawMessage);
        else
            sender.sendMessage(messages);
    }

    /**
     * Adds color to a string for messages.<br>
     * (If using the spigot api or a for of it, hex codes can be used using a
     * #rrggbb ie #ff0000 = red)<br>
     * Example formatting:
     *
     * <pre>{@code
     * // For bukkit style color codes
     * ChatUtils.color("&6&lThis bold gold color");
     *
     * // For any hex color using rgb
     * ChatUtils.color("#ff7700&lThis is bold orange");
     * }</pre>
     *
     * @param string - The string to color format
     * @return A color-formatted string
     */
    public static String color(String string) {
        try {
            Class<?> chatColor;
            Class.forName("net.md_5.bungee.api.ChatColor");
            chatColor = Class.forName("net.md_5.bungee.api.ChatColor");
            Method of = chatColor.getMethod("of", String.class);
            Pattern p = Pattern.compile("(#[0-9a-fA-F]{6})");
            Matcher m = p.matcher(string);
            while (m.find()) {
                String a = m.group();
                string = string.replace(a, of.invoke(null, a).toString());
            }
            return ChatColor.translateAlternateColorCodes('&', string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static void sendMessage(CommandSender sender, String message) {
        if (sender instanceof Conversable person && person.isConversing())
            person.sendRawMessage(message);
        else
            sender.sendMessage(message);
    }
}
