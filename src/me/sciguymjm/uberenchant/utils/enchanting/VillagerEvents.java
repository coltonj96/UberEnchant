package me.sciguymjm.uberenchant.utils.enchanting;

import me.sciguymjm.uberenchant.api.utils.UberRecord;
import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.api.utils.random.WeightedChance;
import me.sciguymjm.uberenchant.utils.FileUtils;
import me.sciguymjm.uberenchant.utils.VersionUtils;
import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;

public class VillagerEvents implements Listener {

    public static List<String> disabled;

    public VillagerEvents() {
        reload();
    }

    public static void reload() {
        disabled = FileUtils.loadConfig("/mechanics/villager.yml").getStringList("disabled_enchantments");
    }

    @EventHandler
    public void OnSpawn(VillagerAcquireTradeEvent event) {
        if (event.getEntity() instanceof Villager villager && villager.getProfession().equals(Villager.Profession.LIBRARIAN)) {
            MerchantRecipe recipe = event.getRecipe();
            if (recipe.getResult().getType().equals(Material.ENCHANTED_BOOK)) {
                ItemStack item = recipe.getResult();
                int max = ((EnchantmentStorageMeta) item.getItemMeta()).getStoredEnchants().values().stream().max(
                        Comparator.comparing(n -> n)
                ).orElse(1);
                WeightedChance<EnchantmentTableUtils.WeightedEnchantment> enchants = new WeightedChance<>();
                Random r = new Random();
                UberRecord.getRecords().stream()
                        .filter(record -> !disabled.contains(VersionUtils.key(record.getEnchant())))
                        .forEach(record -> {
                            EnchantmentTableUtils.WeightedEnchantment entry = new EnchantmentTableUtils.WeightedEnchantment(record.getEnchant(), max <= 1 ? 1 : r.nextInt(1, max));
                            enchants.add(entry.value());
                        });
                /*UberEnchantment.getRegisteredEnchantments().forEach(enchant -> {
                    EnchantmentTableUtils.WeightedEnchantment entry = new EnchantmentTableUtils.WeightedEnchantment(enchant, max <= 1 ? 1 : r.nextInt(1, max));
                    enchants.add(entry.value(), entry.weight() / 100.0);
                });*/
                enchants.add(new EnchantmentTableUtils.WeightedEnchantment(null, 0), 0.2);
                EnchantmentTableUtils.WeightedEnchantment enchant = enchants.select();
                if (enchant.getEnchantment() == null)
                    return;
                boolean exists = villager.getRecipes().stream()
                        .map(MerchantRecipe::getResult)
                        .filter(result -> result.getType().equals(Material.ENCHANTED_BOOK))
                        .anyMatch(result -> UberUtils.getAllStoredMap(result).containsKey(enchant.getEnchantment()));
                if (exists)
                    return;
                ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
                EnchantmentUtils.setStoredEnchantment(enchant.getEnchantment(), book, enchant.getLevel());
                MerchantRecipe newRecipe = new MerchantRecipe(
                        book,
                        recipe.getUses(),
                        recipe.getMaxUses(),
                        recipe.hasExperienceReward(),
                        recipe.getVillagerExperience(),
                        recipe.getPriceMultiplier(),
                        recipe.getDemand(),
                        recipe.getSpecialPrice()
                );
                newRecipe.setIngredients(recipe.getIngredients());
                event.setRecipe(newRecipe);
            }
        }
    }
}
