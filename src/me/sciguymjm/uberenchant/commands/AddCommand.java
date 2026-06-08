package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.UberEnchant;
import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.events.UberEnchantmentsAddedEvent;
import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.api.utils.UberRecord;
import me.sciguymjm.uberenchant.api.utils.UberRunnable;
import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.*;
import me.sciguymjm.uberenchant.api.utils.persistence.UberMeta;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import me.sciguymjm.uberenchant.enchantments.tasks.HeldEffectTask;
import me.sciguymjm.uberenchant.utils.*;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentUtils;
import me.sciguymjm.uberenchant.utils.plugins.VaultUtils;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * Class for internal use.
 */
public class AddCommand extends UberTabCommand {

    public AddCommand() {
        super("uadd");
    }

    @Override
    public boolean onCmd() {
        if (args.length != 0) {
            ItemStack item = player.getInventory().getItemInMainHand();
            switch (args[0].toLowerCase()) {
                case "attribute" -> action("attribute", this::attribute, item);
                case "enchant" -> action("enchant", this::enchant, item);
                case "effect" -> action("effect", this::effect);
                case "lore" -> action("lore", this::lore, item);
                case "meta" -> action("meta", this::meta, item);
                case "name" -> action("name", this::name, item);
                default -> EnchantmentUtils.help(player, "uadd");
            }
        } else
            response("&6%1$s", command.getUsage());
        return true;
    }

    /*
    /uadd atrribute <atrribute> <name> <value> <operation> [group]
     */
    @Override
    public List<String> onTab() {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            add(list, "uber.add.attribute", "attribute");
            add(list, "uber.add.enchant", "enchant");
            add(list, "uber.add.effect", "effect");
            add(list, "uber.add.lore", "lore");
            add(list, "uber.add.meta", "meta", Versions.isV1_20_4());
            add(list, "uber.add.name", "name");
        }
        if (args.length == 2)
            switch (args[0].toLowerCase()) {
                case "attribute" -> {
                    List<Attribute> attributes;
                    if (Versions.isV1_20_4())
                        attributes = Registry.ATTRIBUTE.stream().toList();
                    else
                        attributes = List.of(Attribute.values());
                    list = attributes.stream().map(VersionUtils::key).filter(name -> args[1].isBlank() || name.toLowerCase().contains(args[1].toLowerCase())).distinct().toList();
                }
                case "enchant" -> list = EnchantmentUtils.find(player, args[1]);
                case "effect" -> list = EffectUtils.matchEffects(args[1]);
                case "meta" -> {
                    if (!Versions.isV1_20_4())
                        return list;
                    ItemStack item = player.getInventory().getItemInMainHand();
                    Map<UberEnchantment, Integer> map = UberUtils.getMap(item);
                    if (!item.getType().equals(Material.AIR) && !map.isEmpty())
                        list = map.keySet().stream().map(key -> key.getKey().getKey().toLowerCase()).toList();
                }
            }
        if (args.length >= 3 && args[0].equalsIgnoreCase("meta")) {
            if (args.length == 3)
                list = UberMeta.values().stream().map(UberMeta::getName).filter(name ->
                        hasPermission(String.format("uber.add.meta.%1$s", name))
                ).toList();
            if (args.length == 4 && BoolTag.matches(args[2])) {
                list = new ArrayList<>(list);
                list.add("true");
                list.add("false");
            }
            /*if (args.length == 4 && ConditionalTag.matches(args[2])) {
                list = MiscUtils.parse(player, args);
            }
            /*if (args.length == 4 && UUIDTag.matches(args[2])) {
                list.add(player.getName());
                if (hasPermission("uber.add.meta.owner.others"))
                    list.addAll(Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).toList());
            }*/
        }
        if (args.length >= 4 && args[0].equalsIgnoreCase("attribute")) {
            if (args.length == 4)
                list = Arrays.stream(AttributeModifier.Operation.values()).map(op -> op.name().toLowerCase()).filter(name -> args[3].isBlank() || name.contains(args[3])).distinct().toList();
            if (args.length == 5)
                list = Arrays.stream(EquipmentSlot.values()).map(slot -> slot.getGroup().toString()).filter(name -> args[4].isBlank() || name.contains(args[4])).distinct().toList();
        }
        /*if (args.length == 5 && args[0].equalsIgnoreCase("effect")) {
            list = Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).toList();
        }*/

        return list;
    }

    private void enchant(ItemStack item) {
        UberEnchantmentsAddedEvent event = new UberEnchantmentsAddedEvent(player, item, null);
        if (event.isCancelled())
            return;
        if (item.getType().equals(Material.AIR)) {
            if (!hasPermission("uber.add.enchant.book")) {
                response(Reply.HOLD_ITEM);
                return;
            }
            item = new ItemStack(Material.ENCHANTED_BOOK);
        }
        if (args.length < 3) {
            response(argue("&a/uadd enchant <enchantment> <level>"));
            response(Reply.ARGUMENTS);
            return;
        }
        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            response("&a/uadd %1$s %2$s &c%3$s", args);
            response(Reply.WHOLE_NUMBER);
            return;
        }
        if (args[1].equalsIgnoreCase("all") && hasPermission("uber.enchant.add.all")) {
            if (level > 255) {
                localized("&c", "actions.enchant.add.max_level");
                level = 255;
            }
            Map<Enchantment, Integer> map = new HashMap<>();
            final Integer lev = level;
            UberConfiguration.getRecords().forEach(r -> map.put(r.getEnchant(), lev));
            if (item.getType().equals(Material.ENCHANTED_BOOK))
                UberUtils.addStoredEnchantments(map, item);
            else
                UberUtils.addEnchantments(map, item);
            event = new UberEnchantmentsAddedEvent(player, item, map);
            player.getInventory().setItemInMainHand(item);
            localized("&a", "actions.enchant.add.success_all");
            Bukkit.getServer().getPluginManager().callEvent(event);
            return;
        }
        Set<Enchantment> set = EnchantmentUtils.getMatches(args[1]);
        if (EnchantmentUtils.multi(player, set))
            return;
        Enchantment enchant = set.iterator().next();
        UberRecord e = UberConfiguration.getByEnchant(enchant);
        if (!hasPermission("uber.add.enchant.all") && !hasPermission("uber.add.enchant.%1$s", e.getName().toLowerCase())) {
            response(Reply.PERMISSIONS);
            return;
        }
        if (hasPermission("uber.enchant.%1$s.free", e.getName().toLowerCase()) || !VaultUtils.useEconomy()) {
            if (level >= e.getMinLevel() && level <= e.getMaxLevel() || hasPermission("uber.enchant.bypass.level")) {
                if (level > 255) {
                    localized("&c", "actions.enchant.add.max_level");
                    level = 255;
                }
                event = new UberEnchantmentsAddedEvent(player, item, Map.of(enchant, level));
                if (item.getType().equals(Material.ENCHANTED_BOOK))
                    EnchantmentUtils.setStoredEnchantment(enchant, item, level);
                else
                    EnchantmentUtils.setEnchantment(enchant, item, level);
                player.getInventory().setItemInMainHand(item);
                localized("&a", "actions.enchant.add.success", e.getDisplayName(), level);
                Bukkit.getServer().getPluginManager().callEvent(event);
            } else
                localized("&c", "actions.enchant.add.range", e.getMinLevel(), e.getMaxLevel());
            return;
        }
        if (VaultUtils.hasEconomy()) {
            if (!e.getEnchant().canEnchantItem(item))
                if (!e.getCanUseOnAnything() || !hasPermission("uber.enchant.bypass.any")) {
                    localized("&c", "actions.enchant.add.incompatible");
                    return;
                }
            if (level >= e.getMinLevel() && level <= e.getMaxLevel()) {
                double cost = e.getLevelCost().containsKey(level) ? e.getLevelCost().get(level) : e.getCost() + (e.getCostMultiplier() * e.getCost() * (level - 1));
                if (VaultUtils.has(player, cost)) {
                    if (level > 255) {
                        localized("&c", "actions.enchant.add.max_level");
                        level = 255;
                    }
                    event = new UberEnchantmentsAddedEvent(player, item, Map.of(enchant, level));
                    if (item.getType().equals(Material.ENCHANTED_BOOK))
                        EnchantmentUtils.setStoredEnchantment(enchant, item, level);
                    else
                        EnchantmentUtils.setEnchantment(enchant, item, level);
                    EconomyResponse n = VaultUtils.withdraw(player, cost);
                    player.getInventory().setItemInMainHand(item);
                    localized("&a", "actions.enchant.add.pay_success", e.getDisplayName(), level, n.amount);
                    Bukkit.getServer().getPluginManager().callEvent(event);
                } else
                    localized("&c", "actions.enchant.add.pay_more", cost - VaultUtils.getBalance(player));
            } else
                localized("&c", "actions.enchant.add.range", e.getMinLevel(), e.getMaxLevel());
        } else
            response(Reply.NO_ECONOMY);
    }

    private void meta(ItemStack item) {
        //UberEnchantmentsAddedEvent event = new UberEnchantmentsAddedEvent(player, item, null);
        //if (event.isCancelled())
            //return;
        if (!Versions.isV1_20_4())
            return;
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 4) {
            response(argue("&a/uadd meta <enchantment> <tag> <value>"));
            response(Reply.ARGUMENTS);
            return;
        }

        Set<UberEnchantment> set = EnchantmentUtils.getMatches(item, args[1]);
        if (set == null)
            return;
        if (EnchantmentUtils.multi(player, set))
            return;

        UberEnchantment enchant = set.iterator().next();

        if (!enchant.containsEnchantment(item)) {
            localized("&c", "actions.meta.enchantment_not_found");
            return;
        }

        if (!UberMeta.contains(args[2])) {
            localized("&c", "actions.meta.invalid_tag");
            return;
        }

        UberMeta<?> meta = UberMeta.getByName(args[2]);

        if (meta == null) {
            localized("&c", "actions.meta.invalid_tag");
            return;
        }

        if (!hasPermission(String.format("uber.add.meta.%1$s", meta.getName()))) {
            response(Reply.PERMISSIONS);
            return;
        }

        boolean success = false;

        if (meta.tag() instanceof BoolTag tag)  {
            boolean value;
            switch (args[3].toLowerCase()) {
                case "true", "t", "1" -> value = true;
                case "false", "f", "0" -> value = false;
                default -> {
                    localized("&c", "actions.meta.invalid_bool_value");
                    response("&a/uadd %1$s %2$s %3$s &c%4$s", args);
                    return;
                }
            }
            if (enchant instanceof EffectEnchantment effect && tag == BoolTag.ON_HELD && !UberUtils.containsMeta(item, enchant, BoolTag.ON_HELD) && value)
                UberRunnable.addTask(player.getUniqueId() + "_HELD", new HeldEffectTask(player, effect, (p, i, e) ->
                        i.getType().equals(Material.AIR) ||
                                !e.containsEnchantment(i) ||
                                !BoolTag.ON_HELD.test(i, e)));
            UberUtils.addMetaData(item, enchant, tag.asMeta(), value);
            success = true;
        }
        if (!success && meta.tag() instanceof IntTag tag)  {
            int value;
            try {
                value = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                response("&a/uadd %1$s %2$s %3$s &c%4$s", args);
                response(Reply.WHOLE_NUMBER);
                return;
            }
            UberUtils.addMetaData(item, enchant, tag.asMeta(), value);
            success = true;
        }
        if (!success && meta.tag() instanceof DoubleTag tag)  {
            double value;
            try {
                value = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                response("&a/uadd %1$s %2$s %3$s &c%4$s", args);
                response(Reply.DECIMAL_NUMBER);
                return;
            }
            if (value <= 0.0 || value >= 1.0) {
                localized("&c", "actions.meta.invalid_double_value");
                return;
            }
            UberUtils.addMetaData(item, enchant, tag.asMeta(), value);
            success = true;
        }
        /*if (!success && meta.getTag() instanceof ConditionalTag tag)  {
            if (!BoolTagMap.isValid(args[3])) {
                response("&a/uadd %1$s %2$s %3$s &c%4$s", args);
                response(Reply.INVALID);
                return;
            }
            BoolTagMap map = new BoolTagMap();
            if (ConditionalTag.CONDITIONS.has(item, enchant))
                map = ConditionalTag.CONDITIONS.get(item, enchant);
            map.addAll(args[3]);
            UberUtils.addMetaData(item, enchant, tag.asMeta(), map);
            success = true;
        }
        /*if (!success && meta.getTag() instanceof UUIDTag tag)  {
            List<Player> players = Bukkit.matchPlayer(args[3]);
            if (EnchantmentUtils.multi(player, players))
                return;
            if (players.isEmpty()) {
                localized("&c", "utils.players.none", args[3]);
                return;
            }
            UberUtils.addMetaData(item, enchant, tag.create(), players.get(0).getUniqueId());
            success = true;
        }*/
        if (success)
            localized("&a", "actions.meta.add.success");
        else
            localized("&c", "actions.meta.add.fail");
    }

    private void attribute(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 4) {
            response(argue("&a/uadd attribute <attribute> <value> <operation> [group]"));
            //response("&a/uadd attribute &c<attribute> <value> <operation> [group]");
            response(Reply.ARGUMENTS);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;
        Attribute attribute;
        if (Versions.isV1_20_4())
            attribute = Registry.ATTRIBUTE.get(NamespacedKey.minecraft(args[1].toLowerCase()));
        else
            attribute = Attribute.valueOf(args[1].toLowerCase());
        if (attribute == null) {
            localized("&c", "actions.attribute.not_exist");
            return;
        }
        double value;
        try {
            value = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            response("&a/uadd %1$s %2$s &c%3$s %4$s" + (args.length < 5 ? "" : " %5$s"), args);
            response(Reply.DECIMAL_NUMBER);
            return;
        }
        AttributeModifier.Operation operation;
        try {
            operation = AttributeModifier.Operation.valueOf(args[3].toUpperCase());
        }  catch (IllegalArgumentException e) {
            localized("&c", "actions.attribute.operation.not_exist");
            return;
        }

        EquipmentSlotGroup group = EquipmentSlotGroup.ANY;
        if (args.length == 5) {
            group = EquipmentSlotGroup.getByName(args[4]);
            if (group == null) {
                localized("&c", "actions.attribute.group.not_exist");
                return;
            }
        }

        String format = String.format(
                Locale.ROOT,
                "%1$s/%2$s/%3$s/%4$s",
                VersionUtils.key(attribute),
                operation.name().toLowerCase(),
                value,
                group
        );
        NamespacedKey key = new NamespacedKey(UberEnchant.instance(), format);
        if (meta.hasAttributeModifiers() && meta.getAttributeModifiers(attribute) != null && meta.getAttributeModifiers(attribute).stream().anyMatch(mod -> mod.getKey().equals(key))) {
            localized("&c", "actions.attribute.exists");
            return;
        }
        AttributeModifier modifier = new AttributeModifier(key, value, operation, group);
        if (meta.hasAttributeModifiers() && meta.getAttributeModifiers().containsKey(attribute) && meta.getAttributeModifiers(attribute).contains(modifier)) {
            localized("&c", "actions.attribute.modifier.exists");
            return;
        }
        meta.addAttributeModifier(attribute, modifier);
        item.setItemMeta(meta);
        localized("&a", "actions.attribute.add.success");
    }

    private void effect() {
        if (args.length < 4) {
            response(argue("&a/uadd effect <effect> <duration> <level>"));
            response(Reply.ARGUMENTS);
            return;
        }
        int level;
        int duration;
        try {
            duration = Integer.parseInt(args[2]);
            duration *= 20;
        } catch (NumberFormatException e) {
            response("&a/uadd %1$s %2$s &c%3$s &a%4$s", args);
            response(Reply.WHOLE_NUMBER);
            return;
        }
        try {
            level = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            response("&a/uadd %1$s %2$s %3$s &c%4$s", args);
            response(Reply.WHOLE_NUMBER);
            return;
        }
        PotionEffectType type = EffectUtils.getEffect(args[1]);
        if (type == null) {
            localized("&c", "actions.effect.invalid");
            response("&a/ulist effects");
            return;
        }
        if (player.hasPotionEffect(type))
            EffectUtils.removeEffect(player, type);
        EffectUtils.setEffect(player, type, duration, level);
        localized("&a", "actions.effect.add.success");
    }

    private void lore(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 2) {
            response("&a/uadd lore &c<text...>");
            response(Reply.ARGUMENTS);
            return;
        }
        int index = UberUtils.offset(item);
        StringBuilder message = new StringBuilder(args[1]);
        if (args.length > 2)
            for (int arg = 2; arg < args.length; arg++)
                message.append(" ").append(args[arg]);
        String name = ChatUtils.color(message.toString().trim());
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;
        List<String> lore = new ArrayList<>();
        if (meta.hasLore() || (meta.hasLore() && meta.getLore().size() - index > 0))
            lore = meta.getLore();
        lore.add(name.replace("%null", ""));
        meta.setLore(lore);
        item.setItemMeta(meta);
        localized("&a", "actions.lore.add.success");
    }

    private void name(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 2) {
            response("&a/uadd name &c<text...>");
            response(Reply.ARGUMENTS);
            return;
        }
        StringBuilder message = new StringBuilder(args[1]);
        for (int arg = 2; arg < args.length; arg++)
            message.append(" ").append(args[arg]);
        String name = ChatUtils.color(message.toString().trim());
        ItemMeta meta = item.getItemMeta();
        String prev = meta.getDisplayName();
        if (!meta.hasDisplayName()) {
            localized("&c", "actions.name.no_name");
            return;
        }
        if (VaultUtils.hasEconomy()) {
            double cost = VaultUtils.getCost("cost.name.add");
            if (VaultUtils.has(player, cost)) {
                VaultUtils.withdraw(player, cost);
                meta.setDisplayName(prev + name);
                item.setItemMeta(meta);
                localized("&a", "actions.name.add.pay_success", cost);
            } else
                localized("&c", "actions.name.add.pay_fail", cost - VaultUtils.getBalance(player));
        } else {
            meta.setDisplayName(prev + name);
            item.setItemMeta(meta);
            localized("&a", "actions.name.add.success");
        }
    }
}
