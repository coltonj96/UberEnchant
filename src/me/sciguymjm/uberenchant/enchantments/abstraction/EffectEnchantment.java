package me.sciguymjm.uberenchant.enchantments.abstraction;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.utils.UberEffects;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.stream.Stream;

/**
 * Abstract class for internal use.
 */
public abstract class EffectEnchantment extends UberEnchantment {

    /**
     * For internal use.
     *
     * @param key String
     * @hidden
     */
    public EffectEnchantment(String key) {
        super(key);
    }

    /**
     * Utility method for internal use.
     *
     * @return UberEffects
     * @hidden
     */
    public abstract UberEffects getEffect();

    @Override
    public final String getDisplayName() {
        return getEffect().getDisplayName();
    }

    @Override
    public final String getPermission() {
        return "";
    }

    /**
     * Utility method for internal use.
     *
     * @param item ItemStack
     * @return int
     * @hidden
     */
    public int getDuration(ItemStack item) {
        return item.getEnchantments().get(this) * 20;
    }

    /**
     * Utility method for internal use.
     *
     * @param item   ItemStack
     * @param entity LivingEntity
     * @hidden
     */
    public void apply(ItemStack item, LivingEntity entity) {
        getEffect().getEffect().createEffect(getDuration(item), item.getEnchantments().get(this)).apply(entity);
    }

    /**
     * Utility method for internal use.
     *
     * @hidden
     */
    public static void init() {
        List.of(UberEffects.values()).forEach(a -> {
            new EffectEnchantmentWrapper(a).register();
        });
    }

    /**
     * Utility method for internal use.
     *
     * @param event EntityDamageByEntityEvent
     * @hidden
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void OnHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && event.getEntity() instanceof LivingEntity entity) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item != null && containsEnchantment(item)) {
                apply(item, entity);
            }
        }
        if (event.getDamager() instanceof LivingEntity entity && event.getEntity() instanceof Player player) {
            Stream.of(player.getInventory().getArmorContents()).forEach(item -> {
                if (item != null && containsEnchantment(item)) {
                    UberEffects e = getEffect();
                    if (e.getValue() >= 0) {
                        if (e.getEffect() == PotionEffectType.HEAL) {
                            switch (entity.getType()) {
                                case DROWNED, HUSK, PHANTOM, SKELETON, SKELETON_HORSE, STRAY, WITHER, WITHER_SKELETON, ZOGLIN, ZOMBIE, ZOMBIE_HORSE, ZOMBIE_VILLAGER, ZOMBIFIED_PIGLIN ->
                                        apply(item, entity);
                                default -> apply(item, player);
                            }
                        }
                        apply(item, player);
                    } else {
                        apply(item, entity);
                    }
                }
            });
        }
    }

    /**
     * Utility method for internal use.
     *
     * @param event EntityDamageEvent
     * @hidden
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void OnHit(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            Stream.of(player.getInventory().getArmorContents()).forEach(item -> {
                if (item != null && containsEnchantment(item)) {
                    if (getEffect().getValue() >= 0)
                        apply(item, player);
                }
            });
        }
    }

    /**
     * Utility method for internal use.
     *
     * @param event PlayerItemConsumeEvent
     * @hidden
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void OnHit(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item != null && containsEnchantment(item)) {
            apply(item, player);
        }
    }
}
