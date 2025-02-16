package me.sciguymjm.uberenchant.enchantments.abstraction;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.api.utils.random.WeightedChance;
import me.sciguymjm.uberenchant.api.utils.random.WeightedEntry;
import me.sciguymjm.uberenchant.enchantments.effects.*;
import me.sciguymjm.uberenchant.enchantments.effects.armor.*;
import me.sciguymjm.uberenchant.enchantments.effects.armor.boots.JumpEnchantment;
import me.sciguymjm.uberenchant.enchantments.effects.armor.boots.SlowFallingEnchantment;
import me.sciguymjm.uberenchant.enchantments.effects.armor.boots.SpeedEnchantment;
import me.sciguymjm.uberenchant.enchantments.effects.armor.helmet.HeroOfTheVillageEnchantment;
import me.sciguymjm.uberenchant.enchantments.effects.armor.helmet.NightVisionEnchantment;
import me.sciguymjm.uberenchant.enchantments.effects.armor.helmet.WaterBreathingEnchantment;
import me.sciguymjm.uberenchant.utils.UberEffects;
import me.sciguymjm.uberenchant.utils.VersionUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class for internal use.
 */
public abstract class EffectEnchantment extends UberEnchantment {

    private final UberEffects effect;

    private static Set<EffectEnchantment> effects = new HashSet<>();

    /**
     * For internal use.
     *
     * @param key String
     * @hidden
     */
    public EffectEnchantment(String key) {
        super(key);
        effect = UberEffects.get(key);
        effects.add(this);
    }

    @Override
    public final String getPermission() {
        return String.format("uber.effect.%1$s", getKey().getKey().toLowerCase());
    }

    @Override
    public final int getMaxLevel() {
        return 10;
    }

    @Override
    public final int getStartLevel() {
        return 1;
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

    /**
     * Utility method for internal use.
     *
     * @return UberEffects
     * @hidden
     */
    public UberEffects getEffect() {
        return effect;
    }

    @Override
    public final String getDisplayName() {
        return getEffect().getDisplayName();
    }

    /**
     * Utility method for internal use.
     *
     * @param item ItemStack
     * @return int
     * @hidden
     */
    public int getDuration(ItemStack item) {
        return UberUtils.getMap(item).get(this) * 20;
    }

    /**
     * Utility method for internal use.
     *
     * @param item   ItemStack
     * @param entity LivingEntity
     * @hidden
     */
    public void apply(ItemStack item, LivingEntity entity) {
        new PotionEffect(getEffect().getEffect(), getDuration(item), UberUtils.getMap(item).get(this), false, false).apply(entity);
    }

    /**
     * Utility method for internal use.
     *
     * @hidden
     */
    public static void init() {
        new SpeedEnchantment();
        new SlowEnchantment();
        new FastDiggingEnchantment();
        new SlowDiggingEnchantment();
        new IncreaseDamageEnchantment();
        new HealEnchantment();
        new HarmEnchantment();
        new JumpEnchantment();
        new ConfusionEnchantment();
        new RegenerationEnchantment();
        new DamageResistanceEnchantment();
        new FireResistanceEnchantment();
        new WaterBreathingEnchantment();
        new InvisibilityEnchantment();
        new BlindnessEnchantment();
        new NightVisionEnchantment();
        new HungerEnchantment();
        new WeaknessEnchantment();
        new PoisonEnchantment();
        new WitherEnchantment();
        new HealthBoostEnchantment();
        new AbsorptionEnchantment();
        new SaturationEnchantment();
        new GlowingEnchantment();
        new LevitationEnchantment();
        new LuckEnchantment();
        new UnLuckEnchantment();
        new SlowFallingEnchantment();
        new ConduitPowerEnchantment();
        new DolphinsGraceEnchantment();
        new BadOmenEnchantment();
        new HeroOfTheVillageEnchantment();
        if (VersionUtils.isAtLeast("1.19"))
            new DarknessEnchantment();
        if (VersionUtils.isAtLeast("1.20.5")) {
            new TrialOmenEnchantment();
            new RaidOmenEnchantment();
            new WindChargedEnchantment();
            new WeavingEnchantment();
            new OozingEnchantment();
            new InfestedEnchantment();
        }
        effects.forEach(e -> e.register());
    }

    /*
     * Utility method for internal use.
     *
     * @param event EntityDamageByEntityEvent
     * @hidden
     *
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void OnHit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && event.getEntity() instanceof LivingEntity entity) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() != Material.AIR && containsEnchantment(item))
                apply(item, entity);
        }
        if (event.getDamager() instanceof LivingEntity entity && event.getEntity() instanceof Player player) {
            Stream.of(player.getInventory().getArmorContents()).forEach(item -> {
                if (item != null && item.getType() != Material.AIR && containsEnchantment(item)) {
                    UberEffects e = getEffect();
                    if (e.getValue() >= 0) {
                        if (e.getEffect() == PotionEffectType.INSTANT_HEALTH) {
                            entity.
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
    }*/

    public void apply(Entity damager, Entity damaged) {
        if (damager instanceof Player player && damaged instanceof LivingEntity entity) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() != Material.AIR && containsEnchantment(item)) {
                double chance = (double) getLevel(item) / getMaxLevel();
                boolean outcome = WeightedChance.fromArray(
                        new WeightedEntry<>(true, chance),
                        new WeightedEntry<>(false, 1 - chance)
                ).select();
                if (outcome)
                    getEffect().getEffect().createEffect(getDuration(item), UberUtils.getMap(item).get(this)).apply(entity);
            }
        }
    }

    public void apply(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.AIR && containsEnchantment(item)) {
            double chance = (double) getLevel(item) / getMaxLevel();
            boolean outcome = WeightedChance.fromArray(
                    new WeightedEntry<>(true, chance),
                    new WeightedEntry<>(false, 1 - chance)
            ).select();
            if (outcome)
                getEffect().getEffect().createEffect(getDuration(item), UberUtils.getMap(item).get(this)).apply(player);
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
        if (item.getType() != Material.AIR && containsEnchantment(item)) {
            apply(item, player);
        }
    }

    public boolean apply(Player player, EquipmentSlot slot, int duration, int n) {
        if (!player.isValid())
            return false;
        ItemStack i = player.getInventory().getItem(slot);
        if (i == null || !containsEnchantment(i))
            return false;
        if (n % duration == 0) {
            int level = getLevel(i);
            if (player.hasPotionEffect(effect.getEffect())) {
                if (player.getPotionEffect(effect.getEffect()).getAmplifier() < level)
                    player.addPotionEffect(new PotionEffect(effect.getEffect(), duration, level, false, true));
            } else {
                player.addPotionEffect(new PotionEffect(effect.getEffect(), duration, level, false, true));
            }
        }
        return true;
    }
}
