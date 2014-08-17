package me.sciguymjm.uberenchant;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class UberEnchant extends JavaPlugin {
	public static UberEnchant plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public File dataDir;
	public boolean updates;
	public String name;
	public long size;
	public static Economy economy = null;

	public void onDisable() {
		this.logger.info("Uber Enchant is now disabled.");
	}
	
	public void onEnable() {
		
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		    // Failed to submit the stats :-(
		}
		
		createConfig();
		setupEconomy();
		PluginManager manage = getServer().getPluginManager();
		manage.registerEvents(new UberEventListener(this), this);
		this.getCommand("uber").setExecutor(new Enchant());
		this.logger.info("Uber Enchant is now enabled.");
	}
	
	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = rsp.getProvider();
		return economy != null;
	}
	
	public void createConfig() {
		try {
			if (!this.getDataFolder().exists()) {
				this.getDataFolder().mkdir();
				FileConfiguration config = this.getConfig();
				ConfigurationSection settings = config.createSection("settings");
				ConfigurationSection price = settings.createSection("price");
				price.set("min", 100);
				price.set("max", 10000);
				ConfigurationSection level = settings.createSection("level");
				level.set("max", 10);
				this.saveConfig();
			}
		} catch (Exception error) {
			
		}
	}
}
