package me.sciguymjm.uberenchant.enchantments.abstraction;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.*;
import me.sciguymjm.uberenchant.api.utils.persistence.UberMeta;
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
import me.sciguymjm.uberenchant.enchantments.tasks.HeldEffectTask;
import me.sciguymjm.uberenchant.utils.UberEffects;
import me.sciguymjm.uberenchant.utils.VersionUtils;
import me.sciguymjm.uberenchant.utils.WorldGuardUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstract class for internal use.
 */
public abstract class EffectEnchantment extends UberEnchantment {

    private final UberEffects effect;
    private static Set<EffectEnchantment> effects;

    private final Map<MetaTag<?>, Object> TAGS;

    {
        effects = new HashSet<>();
        TAGS = new HashMap<>();
        setTag(BoolTag.ON_HIT, true);
    }

    public Map<MetaTag<?>, Object> getDefaultTags() {
        return TAGS;
    }

    public <T> Map<MetaTag<T>, T> getTagDefaults(PersistentDataType<?, T> type) {
        Class<T> t = type.getComplexType();
        return TAGS.entrySet().stream()
                .filter(entry -> entry.getKey().getType().equals(type))
                .collect(Collectors.toMap(entry -> {
                    MetaTag<?> tag = entry.getKey();
                    return MetaTag.create(tag.getName(), type, tag.getPlugin());
                }, entry -> t.cast(entry.getValue())));
    }

    public boolean hasTag(MetaTag<?> tag) {
        return TAGS.containsKey(tag);
    }

    public <T> void setTag(MetaTag<T> tag, T value) {
        TAGS.put(tag, value);
    }

    public <T> T getTag(MetaTag<T> tag) {
        return tag.getType().getComplexType().cast(TAGS.get(tag));
    }

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
        if (containsEnchantment(item, this)) {
            Integer duration = UberMeta.DURATION.get(item, this);
            if (duration != null)
                return duration * 20;
        }
        return 0;
    }

    public boolean apply(ItemStack item, LivingEntity entity, int duration) {
        if (WorldGuardUtils.worldGuardLoaded() && WorldGuardUtils.denyPotionSplash(entity, entity.getLocation()))
            return false;
        return new PotionEffect(getEffect().getEffect(), duration, getLevel(item) - 1, false, false).apply(entity);
    }

    /**
     * Utility method for internal use.
     *
     * @param item   ItemStack
     * @param entity LivingEntity
     * @hidden
     */
    public boolean apply(ItemStack item, LivingEntity entity) {
        return apply(item, entity, getDuration(item));
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

    public void apply(Entity damager, Entity damaged) {
        if (damager instanceof Player player && damaged instanceof LivingEntity entity) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() != Material.AIR && containsEnchantment(item)) {
                if (WorldGuardUtils.worldGuardLoaded()) {
                    if (entity instanceof Player && WorldGuardUtils.denyPvp(player, entity.getLocation()))
                        return;
                    if (entity instanceof Animals && WorldGuardUtils.denyDamageAnimals(player, entity.getLocation()))
                        return;
                    if (WorldGuardUtils.denyPotionSplash(player, entity.getLocation()))
                        return;
                }
                double chance = DoubleTag.CHANCE.get(item, this, (double) getLevel(item) / getMaxLevel());
                boolean outcome = WeightedChance.fromArray(
                        new WeightedEntry<>(true, chance),
                        new WeightedEntry<>(false, 1 - chance)
                ).select();
                if (outcome)
                    apply(item, entity);
            }
        }
    }

    public void apply(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.AIR && containsEnchantment(item)) {
            if (WorldGuardUtils.worldGuardLoaded() && WorldGuardUtils.denyPotionSplash(player, player.getLocation()))
                return;
            double chance = DoubleTag.CHANCE.get(item, this, (double) getLevel(item) / getMaxLevel());
            boolean outcome = WeightedChance.fromArray(
                    new WeightedEntry<>(true, chance),
                    new WeightedEntry<>(false, 1 - chance)
            ).select();
            if (outcome)
                apply(item, player);
        }
    }

    public boolean apply(Player player, EquipmentSlot slot, int duration, int n) {
        if (!player.isValid())
            return false;
        ItemStack i = player.getInventory().getItem(slot);
        if (i == null || !containsEnchantment(i) || (WorldGuardUtils.worldGuardLoaded() && WorldGuardUtils.denyPotionSplash(player, player.getLocation())))
            return false;
        if (n % duration == 0)
            return apply(i, player);
        return true;
    }

    /*protected <T> boolean has(Map<? extends MetaTag<T>, T> map, MetaTag<T> tag) {
        return map.containsKey(tag);
    }

    protected <T, E extends MetaTag<T>> void set(Map<E, T> map, E tag, T value) {
        map.put(tag, value);
    }

    protected <T> T get(Map<? extends MetaTag<T>, T> map, MetaTag<T> tag) {
        return map.get(tag);
    }*/

    @EventHandler(priority = EventPriority.MONITOR)
    public void OnConsume(PlayerItemConsumeEvent event) {
        if (event.isCancelled())
            return;
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR || !containsEnchantment(item))
            return;
        if (conditions(item))
            return;
        if (BoolTag.ON_CONSUME.test(item, this))
            apply(item, player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void OnPickup(EntityPickupItemEvent event) {
        if (event.isCancelled())
            return;
        LivingEntity entity = event.getEntity();
        ItemStack item = event.getItem().getItemStack();
        if (item == null || item.getType() == Material.AIR || !containsEnchantment(item))
            return;
        if (conditions(item))
            return;
        if (testBoolTag(item, BoolTag.ON_PICKUP))
            apply(item, entity);
        if (testBoolTag(item, BoolTag.ON_HELD))
            addTask(new HeldEffectTask(entity, this, (p, i, e) ->  i.getType().equals(Material.AIR)|| !e.containsEnchantment(i)));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void OnDrop(EntityDropItemEvent event) {
        if (event.isCancelled())
            return;
        if (event.getEntity() instanceof LivingEntity entity) {
            ItemStack item = event.getItemDrop().getItemStack();
            if (item == null || item.getType() == Material.AIR || !containsEnchantment(item))
                return;
            if (conditions(item))
                return;
            if (testBoolTag(item, BoolTag.ON_DROP))
                apply(item, entity);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void OnDrop(PlayerDropItemEvent event) {
        if (event.isCancelled())
            return;
        ItemStack item = event.getItemDrop().getItemStack();
        if (item == null || item.getType() == Material.AIR || !containsEnchantment(item))
            return;
        if (conditions(item))
            return;
        if (testBoolTag(item, BoolTag.ON_DROP))
            apply(item, event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void OnHeld(PlayerItemHeldEvent event) {
        if (event.isCancelled())
            return;
        ItemStack item = event.getPlayer().getInventory().getItem(event.getNewSlot());
        if (item == null || item.getType() == Material.AIR || !containsEnchantment(item))
            return;
        if (conditions(item))
            return;
        if (BoolTag.ON_HELD.test(item, this))
            addTask(new HeldEffectTask(event.getPlayer(), this, (p, i, e) ->
                    i.getType().equals(Material.AIR) ||
                    !e.containsEnchantment(i) ||
                    !BoolTag.ON_HELD.test(i, e)));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void OnHit(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        if (event.getDamager() instanceof Player player && event.getEntity() instanceof LivingEntity entity) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.AIR || !containsEnchantment(item) || !BoolTag.ON_HIT.test(item, this))
                return;
            if (WorldGuardUtils.worldGuardLoaded()) {
                if (entity instanceof Player && WorldGuardUtils.denyPvp(player, entity.getLocation()))
                    return;
                if (entity instanceof Animals && WorldGuardUtils.denyDamageAnimals(player, entity.getLocation()))
                    return;
                if (WorldGuardUtils.denyPotionSplash(player, entity.getLocation()))
                    return;
            }
            if (conditions(item))
                return;
            if (!BoolTag.HAS_CHANCE.test(item, this)) {
                apply(item, entity);
                return;
            }
            double chance = DoubleTag.CHANCE.get(item, this, (double) getLevel(item) / getMaxLevel());
            boolean outcome = WeightedChance.fromArray(
                    new WeightedEntry<>(true, chance),
                    new WeightedEntry<>(false, 1 - chance)
            ).select();
            if (outcome)
                apply(item, entity);
        }
    }
}
