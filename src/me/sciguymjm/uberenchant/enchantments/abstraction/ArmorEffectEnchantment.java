package me.sciguymjm.uberenchant.enchantments.abstraction;

import me.sciguymjm.uberenchant.api.events.UberArmorEquippedEvent;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.BoolTag;
import me.sciguymjm.uberenchant.enchantments.tasks.ArmorEffectChanceTask;
import me.sciguymjm.uberenchant.enchantments.tasks.ArmorEffectTask;
import me.sciguymjm.uberenchant.enchantments.tasks.Conditional;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public abstract class ArmorEffectEnchantment extends EffectEnchantment {

    public ArmorEffectEnchantment(String key) {
        super(key);
        //set(BOOL, BoolTag.ON_EQUIP, true);
        setTag(BoolTag.HAS_CHANCE, false);
        setTag(BoolTag.ON_HIT, false);
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ARMOR;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        return EnchantmentTarget.ARMOR.includes(itemStack);
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment enchantment) {
        return false;
    }

    @Override
    public String getTranslationKey() {
        return "";
    }

    @EventHandler
    public void onEquip(UberArmorEquippedEvent event) {
        if (event.isCancelled())
            return;
        ItemStack item = event.getItem();
        if (!containsEnchantment(item))
            return;
        Conditional condition = (p, i, e) -> {
            int a = getLevel(item, this);
            int b = getLevel(i, this);
            return !e.containsEnchantment(i) || a != b;
        };
        //if (BoolTag.ON_EQUIP.test(item, this)) {
            ArmorEffectTask task = new ArmorEffectTask(event.getPlayer(), this, event.getType(), condition);
            if (BoolTag.HAS_CHANCE.test(item, this))
                task = new ArmorEffectChanceTask(event.getPlayer(), this, event.getType(), condition);
            addTask(task);
        //}
    }
}
