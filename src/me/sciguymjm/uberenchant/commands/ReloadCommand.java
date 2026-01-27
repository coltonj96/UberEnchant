package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.commands.abstraction.UberCommand;
import me.sciguymjm.uberenchant.utils.enchanting.AnvilEvents;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentTableEvents;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentTableUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

/**
 * For internal use.
 */
public class ReloadCommand extends UberCommand implements CommandExecutor {

    public ReloadCommand() {
        super("ureload");
    }

    @Override
    public boolean onCmd() {
        action(() -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                InventoryView view = player.getOpenInventory();
                if (view.getType().equals(InventoryType.ANVIL) || view.getType().equals(InventoryType.ENCHANTING))
                    view.close();
            });
            UberConfiguration.reloadAll();
            EnchantmentTableEvents.reload();
            EnchantmentTableUtils.reload();
            AnvilEvents.reload();
        });
        return true;
    }
}
