package me.sciguymjm.uberenchant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UberUtils {

	public static Enchantment getEnchant(String name) {
		for (Enchants value : Enchants.values()) {
			Pattern pattern = Pattern.compile(name);
			Matcher matcher = pattern.matcher(value.getName());
			if (matcher.lookingAt()) {
				return value.getEnchant();
			}
		}
		return null;
	}
	
	public static int getId(Enchantment enchant) {
		for (Enchants value : Enchants.values()) {
			if (value.getEnchant().equals(enchant)) {
				return value.getId();
			}
		}
		return 0;
	}
	
	public static Enchantment getEnchant(Integer id) {
		for (Enchants value : Enchants.values()) {
			if (value.getId() == id) {
				return value.getEnchant();
			}
		}
		return null;
	}
	
	public static void removeEnchantment(Enchantment enchant, ItemStack item, Player player) {
		if (item.containsEnchantment(enchant)) {
			item.removeEnchantment(enchant);
			player.sendMessage(ChatColor.GREEN + "Successfully Removed Specified Enchantment!");
		} else {
			player.sendMessage(ChatColor.RED + "That Item Does Not Contain That Enchantment!");
		}
	}
	
	public static int getOption(String[] args) {
		if (args[0].equalsIgnoreCase("del"))
			return -1;
		if (args[0].equalsIgnoreCase("list"))
			return 0;
		if (args[0].equalsIgnoreCase("add"))
			return 1;
		if (args[0].equalsIgnoreCase("clear"))
			return 2;
		if (args[0].equalsIgnoreCase("set"))
			return 3;
		if (args[0].equalsIgnoreCase("insert"))
			return 4;
		return -2;
	}
	
	public static int getType(String[] args) {
		if (args[1].equalsIgnoreCase("enchant"))
			return 1;
		if (args[1].equalsIgnoreCase("name"))
			return 2;
		if (args[1].equalsIgnoreCase("lore"))
			return 3;
		return 0;
	}
}
