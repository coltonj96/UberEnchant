package me.sciguymjm.uberenchant;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class UberEventListener implements Listener {

	public UberEnchant plugin;
	public int priceMin, priceMax, levelMax;
	

	UberEventListener(UberEnchant plugin) {
		this.plugin = plugin;
		this.priceMin = plugin.getConfig().getInt("settings.price.min", 10);
		this.priceMax = plugin.getConfig().getInt("settings.price.max", 10000);
		this.levelMax = plugin.getConfig().getInt("settings.level.max", 10);
	}

	@EventHandler
	public void signChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		String[] lines = event.getLines();
		String[] error = {"§4You Do Not Have Enough Permissions!",
				"§4No Supported Economy Plugin Found!",
				"§4There Cannot Be Any Blank Lines!",
				"§4Price Is Not In Proper Number Format!",
				"§4Price Must Be More Than " + this.priceMin + "!",
				"§4Price Must Be Less Than " + this.priceMax + "!",
				"§4Price Must Not Be Negative!",
				"§4Invalid Enchantment!",
				"§4Level Is Not In Proper Number Format!",
				"§4Level Must Be More Than 0!",
				"§4Level Must Be Less Than " + this.levelMax
		};
		if (lines[0].equalsIgnoreCase("[Uber]")) {
			if (!player.hasPermission("uber.sign.create")) {
				event.setLine(0, "[§5Uber§0]");
				event.setLine(1, "§4Error");
				event.setLine(2, "");
				event.setLine(3, "");
				player.sendMessage(error[0]);
			} else {
				if (UberEnchant.economy == null) {
					event.setLine(0, "[§5Uber§0]");
					event.setLine(1, "§4Error");
					event.setLine(2, "");
					event.setLine(3, "");
					player.sendMessage(error[1]);
				} else {
					if (lines[1].isEmpty() || lines[2].isEmpty() || lines[3].isEmpty()) {
						event.setLine(0, "[§5Uber§0]");
						event.setLine(1, "§4Error");
						event.setLine(2, "");
						event.setLine(3, "");
						player.sendMessage(error[2]);
					} else {
						int price = 0;
						try {
							price = Integer.parseInt(lines[1]);
						} catch (Exception err1) {
							event.setLine(0, "[§5Uber§0]");
							event.setLine(1, "§4Error");
							event.setLine(2, "");
							event.setLine(3, "");
							player.sendMessage(error[3]);
							return;
						}
						if (price < this.priceMin) {
							event.setLine(0, "[§5Uber§0]");
							event.setLine(1, "§4Error");
							event.setLine(2, "");
							event.setLine(3, "");
							player.sendMessage(error[4]);
							return;
						}
						if (price > this.priceMax) {
							event.setLine(0, "[§5Uber§0]");
							event.setLine(1, "§4Error");
							event.setLine(2, "");
							event.setLine(3, "");
							player.sendMessage(error[5]);
							return;
						}
						if (price < 0) {
							event.setLine(0, "[§5Uber§0]");
							event.setLine(1, "§4Error");
							event.setLine(2, "");
							event.setLine(3, "");
							player.sendMessage(error[6]);
							return;
						}
						Enchantment enchant = UberUtils.getEnchant(lines[2]);
						if (enchant == null) {
							event.setLine(0, "[§5Uber§0]");
							event.setLine(1, "§4Error");
							event.setLine(2, "");
							event.setLine(3, "");
							player.sendMessage(error[7]);
						} else {
							int level = -1;
							try {
								level = Integer.parseInt(lines[3]);
							} catch (Exception err2) {
								event.setLine(0, "[§5Uber§0]");
								event.setLine(1, "§4Error");
								event.setLine(2, "");
								event.setLine(3, "");
								player.sendMessage(error[8]);
								return;
							}
							if (level < 1) {
								event.setLine(0, "[§5Uber§0]");
								event.setLine(1, "§4Error");
								event.setLine(2, "");
								event.setLine(3, "");
								player.sendMessage(error[9]);
								return;
							}
							if (level > this.levelMax) {
								event.setLine(0, "[§5Uber§0]");
								event.setLine(1, "§4Error");
								event.setLine(2, "");
								event.setLine(3, "");
								player.sendMessage(error[10]);
								return;
							}
							event.setLine(0, "[§5Uber§0]");
							event.setLine(1, "§2$§1" + price);
							event.setLine(2, "§4" + UberUtils.getId(enchant) + ":" + level);
							event.setLine(3, player.getName());
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
			Sign sign = (Sign) block.getState();
			String[] lines = sign.getLines();
			if (lines[0].equals("[§5Uber§0]")) {
				if (!player.getName().startsWith(lines[3]) && !player.hasPermission("uber.sign.destroy")) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		int level = 0;
		int price = 0;
		int id = 0;
		Action action = event.getAction();
		if (action == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
				Sign sign = (Sign) block.getState();
				String[] lines = sign.getLines();
				if (lines[0].equalsIgnoreCase("[§5Uber§0]")) {
					String[] split = lines[2].replace("§4", "").split(":");
					try {
						level = Integer.parseInt(split[1]);
						id = Integer.parseInt(split[0]);
						price = Integer.parseInt(lines[1].replace("§2$§1", ""));
					} catch (Exception error) {
						return;
					}
					player.sendMessage("§5Enchantment: §1" + UberUtils.getEnchant(id).getName());
					player.sendMessage("§5Level: §1" + level);
					player.sendMessage("§5Price: §1" + price);
				}
			}
		} else if (action == Action.LEFT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
			if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
				Sign sign = (Sign) block.getState();
				String[] lines = sign.getLines();
				if (lines[0].equalsIgnoreCase("[§5Uber§0]")) {
					String[] split = lines[2].replace("§4", "").split(":");
					try {
						level = Integer.parseInt(split[1]);
						id = Integer.parseInt(split[0]);
						price = Integer.parseInt(lines[1].replace("§2$§1", ""));
					} catch (Exception error) {
						return;
					}
					if (player.getItemInHand().getType() == Material.AIR) {
						player.sendMessage("§4Must Be Holding An Item!");
					} else {
						Economy eco = (Economy) UberEnchant.economy;
						if (eco.has(player.getName(), price)) {
							eco.withdrawPlayer(player.getName(), price);
							player.getItemInHand().addUnsafeEnchantment(UberUtils.getEnchant(id), level);
							eco.depositPlayer(lines[3], price);
							player.sendMessage("§5Transaction Complete!");
						} else {
							player.sendMessage("§4You Cannot Afford This!");
						}
					}
				}
			}
		}
	}
}
