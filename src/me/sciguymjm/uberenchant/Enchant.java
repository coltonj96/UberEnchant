package me.sciguymjm.uberenchant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Enchant implements CommandExecutor {
	
	public String listEnchants() {
		String string = ChatColor.DARK_PURPLE + "Enchantments: " + ChatColor.RED;
		for (Enchants enchant : Enchants.values()) {
			string = string + enchant.getName() + ChatColor.GOLD + "||" + ChatColor.RED;
		}
		return string.trim();
	}
	
	public void setEnchantment(Enchantment enchant, ItemStack item, int level) {
		item.addUnsafeEnchantment(enchant, level);
	}

	public String color(String in) {
		Pattern pattern = Pattern.compile("&[0-9a-f[klmnor]]");
		Matcher match = pattern.matcher(in);
		StringBuffer buffer = new StringBuffer();
		while (match.find()) {
			match.appendReplacement(buffer, "§" + match.group().charAt(1));
		}
		match.appendTail(buffer);
		return buffer.toString();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
		String[] commands = {
				"§cUber Enchant Commands:",
				"§6/uber list",
				"§6/uber add enchant <enchantment> <level>",
				"§6/uber add lore <string...>",
				"§6/uber add name <string...>",
				"§6/uber set name <string...>",
				"§6/uber set lore <line#> <string...>",
				"§6/uber del enchant <enchantment>",
				"§6/uber del name",
				"§6/uber del lore <line#>",
				"§6/uber insert lore <line#> <string...>",
				"§6/uber clear"
				};
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (command.getName().equalsIgnoreCase("uber")) {
				if (player.hasPermission("uber.enchant")) {
					if (args[0].equalsIgnoreCase("help")) {
						player.sendMessage(commands);
						return true;
					}
					if (args.length != 0) {
						int option = UberUtils.getOption(args);
						int type = 0;
						ItemStack item = null;
						if (player.getItemInHand().getType() != Material.AIR) {
							item = player.getItemInHand();
						} else {
							player.sendMessage(ChatColor.RED + "Must Be Holding An Item!");
							return true;
						}
						if (args.length > 1) {
							type = UberUtils.getType(args);
						}
						switch (option) {
						case -2 :
							player.sendMessage(ChatColor.RED + "Invalid Option!");
							break;
						case -1 :
							switch (type) {
							case 0 :
								player.sendMessage(ChatColor.RED + "Invalid Option Type!");
								break;
							case 1 :
								if (args.length < 3) {
									player.sendMessage(ChatColor.RED + "Insufficient Arguments!");
									break;
								}
								Enchantment enchant = UberUtils.getEnchant(args[2]);
								if (enchant == null) {
									player.sendMessage(ChatColor.RED + "Invalid Enchantment!");
									player.sendMessage(listEnchants());
									break;
								}
								UberUtils.removeEnchantment(enchant, item, player);
								break;
							case 2 :
								Map<Enchantment, Integer> enchants = item.getEnchantments();
								List<String> lore1 = item.getItemMeta().getLore();
								item.setItemMeta(null);
								item.getItemMeta().setLore(lore1);
								item.addUnsafeEnchantments(enchants);
								player.sendMessage(ChatColor.GREEN + "Successfully Removed Display Name Of The Item!");
								break;
							case 3 :
								if (args.length < 3) {
									player.sendMessage(ChatColor.RED + "Insufficient Arguments!");
									break;
								}
								ItemMeta meta = (ItemMeta) item.getItemMeta();
								if (meta.hasLore() == false) {
									player.sendMessage(ChatColor.RED + "No Lore To Remove!");
									break;
								}
								List<String> lore2 = meta.getLore();
								int size = lore2.size();
								if (size == 1) {
									meta.setLore(null);
									player.sendMessage(ChatColor.GREEN + "Successfully Removed Some Lore!");
									break;
								}
								Integer line = -1;
								try {
									line = Integer.parseInt(args[2]);
								} catch (NumberFormatException err) {
									player.sendMessage(ChatColor.RED + "Line Must Be A Number!");
									break;
								}
								if (line > size || line < 0) {
									player.sendMessage(ChatColor.RED + "Line Does Not Exist!");
									break;
								}

								lore2.remove(line);
								meta.setLore(lore2);
								item.setItemMeta(meta);
								player.sendMessage(ChatColor.GREEN + "Successfully Removed Lore!");
								break;
							}
							break;
						case 0 :
							player.sendMessage(listEnchants());
							break;
						case 1 :
							switch (type) {
							case 0 :
								player.sendMessage(ChatColor.RED + "Invalid Option Type!");
								break;
							case 1 :
								if (args.length < 4) {
									player.sendMessage(ChatColor.RED + "Insufficient Arguments!!");
									break;
								}
								Enchantment enchant = UberUtils.getEnchant(args[2]);
								int level = 0;
								if (enchant == null) {
									player.sendMessage(ChatColor.RED + "Invalid Enchantment!");
									player.sendMessage(listEnchants());
									break;
								}
								try {
									level = Integer.parseInt(args[3]);
								} catch (NumberFormatException e) {
									player.sendMessage(ChatColor.RED + "Level Must Be A Whole Number!");
									break;
								}
								setEnchantment(enchant, item, level);
								player.sendMessage(ChatColor.GREEN + "Successfully Added Enchantment!");
								break;
							case 2 :
								if (args.length < 3) {
									player.sendMessage(ChatColor.RED + "Insufficient Arguments!");
									break;
								}
								StringBuilder message = new StringBuilder(args[2]);
								for (int arg = 3; arg < args.length; arg++) {
									message.append(" ").append(args[arg]);
								}
								String name = color(message.toString().trim());
								ItemMeta meta = (ItemMeta) item.getItemMeta();
								String prev = meta.getDisplayName();
								meta.setDisplayName(prev + " " + name);
								item.setItemMeta(meta);
								player.sendMessage(ChatColor.GREEN + "Successfully Added To Display Name Of Item!");
								break;
							case 3 :
								if (args.length < 3) {
									player.sendMessage(ChatColor.RED + "Insufficient Arguments!");
									break;
								}
								StringBuilder message2 = new StringBuilder(args[2]);
								for (int arg = 3; arg < args.length; arg++) {
									message2.append(" ").append(args[arg]);
								}
								String name3 = color(message2.toString().trim());
								ItemMeta meta2 = (ItemMeta) item.getItemMeta();
								List<String> lore2 = new ArrayList<String>();
								if (meta2.hasLore()) {
									lore2 = meta2.getLore();
								}
								lore2.add(name3);
								meta2.setLore(lore2);
								item.setItemMeta(meta2);
								player.sendMessage(ChatColor.GREEN + "Successfully Added Lore!");
								break;
							}
							break;
						case 2 :
							item.setItemMeta(null);
							player.sendMessage(ChatColor.GREEN + "Successfully Cleared Item Name, Lore, And Enchantments!");
							break;
						case 3 :
							switch (type) {
							case 1 :
								player.sendMessage(ChatColor.RED + "Invalid Argument!");
								break;
							case 2 :
								if (args.length < 3) {
									player.sendMessage(ChatColor.RED + "Insufficient Arguments!");
									break;
								}
								StringBuilder message = new StringBuilder(args[2]);
								for (int arg = 3; arg < args.length; arg++) {
									message.append(" ").append(args[arg]);
								}
								String name = color(message.toString().trim());
								ItemMeta meta = (ItemMeta) item.getItemMeta();
								meta.setDisplayName(name);
								item.setItemMeta(meta);
								player.sendMessage(ChatColor.GREEN + "Successfully Set Display Name Of Item!");
								break;
							case 3 :
								if (args.length < 4) {
									player.sendMessage(ChatColor.RED + "Insufficient Arguments!");
									break;
								}
								ItemMeta meta2 = (ItemMeta) item.getItemMeta();
								if (meta2.hasLore() == false) {
									player.sendMessage(ChatColor.RED + "No Lore To Set!");
									break;
								}
								List<String> lore2 = meta2.getLore();
								int size = lore2.size();
								Integer line = -1;
								try {
									line = Integer.parseInt(args[2]);
								} catch (NumberFormatException err) {
									player.sendMessage(ChatColor.RED + "Line Must Be A Number!");
									break;
								}
								if (line > size || line < 0) {
									player.sendMessage(ChatColor.RED + "Line Does Not Exist!");
									break;
								}
								if (line.toString().contains(".")) {
									player.sendMessage(ChatColor.RED + "Line Must Be A Whole Number!");
									break;
								}
								StringBuilder message2 = new StringBuilder(args[3]);
								for (int arg = 4; arg < args.length; arg++) {
									message2.append(" ").append(args[arg]);
								}
								String name2 = color(message2.toString().trim());
								lore2.set(line, name2);
								meta2.setLore(lore2);
								item.setItemMeta(meta2);
								player.sendMessage(ChatColor.GREEN + "Successfully Set Lore!");
								break;
							}
							break;
						case 4 :
							switch (type) {
							case 1 :
								player.sendMessage(ChatColor.RED + "Invalid Argument!");
								break;
							case 2 :
								player.sendMessage(ChatColor.RED + "Invalid Argument!");
								break;
							case 3 :
								if (args.length < 4) {
									player.sendMessage(ChatColor.RED + "Insufficient Arguments!");
									break;
								}
								ItemMeta meta2 = (ItemMeta) item.getItemMeta();
								if (meta2.hasLore() == false) {
									player.sendMessage(ChatColor.RED + "No Lore To Set!");
									break;
								}
								List<String> lore2 = meta2.getLore();
								int size = lore2.size();
								Integer line = -1;
								try {
									line = Integer.parseInt(args[2]);
								} catch (NumberFormatException err) {
									player.sendMessage(ChatColor.RED + "Line Must Be A Number!");
									break;
								}
								if (line > size || line < 0) {
									player.sendMessage(ChatColor.RED + "Line Does Not Exist!");
									break;
								}
								if (line.toString().contains(".")) {
									player.sendMessage(ChatColor.RED + "Line Must Be A Whole Number!");
									break;
								}
								StringBuilder message2 = new StringBuilder(args[3]);
								for (int arg = 4; arg < args.length; arg++) {
									message2.append(" ").append(args[arg]);
								}
								String name2 = color(message2.toString().trim());
								lore2.add(line, name2);
								meta2.setLore(lore2);
								item.setItemMeta(meta2);
								player.sendMessage(ChatColor.GREEN + "Successfully Inserted Lore!");
								break;
							}
							break;
						case 5:
							switch (type){
							case 0:
								player.sendMessage("Insufficient Arguments!");
							case 4:
								if (args.length < 3) {
									
									player.sendMessage("Insufficient Arguments!");
									
								} else {
									ItemStack i = player.getItemInHand();
									Player p = Bukkit.getPlayer(args[2]);
									p.getInventory().addItem(i);
									player.sendMessage("Gave item to player "+args[2]);
									
									
								}
							}
						
						}
						return true;
					}
					return true;
				}
				return true;
			}
			return true;
		}
		return false;
	}
}
