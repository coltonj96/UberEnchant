package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.UberEnchant;
import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.UberConfiguration;
import me.sciguymjm.uberenchant.api.utils.UberRecord;
import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.api.utils.persistence.UberMeta;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.MetaTag;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.utils.EffectUtils;
import me.sciguymjm.uberenchant.utils.Reply;
import me.sciguymjm.uberenchant.utils.VersionUtils;
import me.sciguymjm.uberenchant.utils.Versions;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentUtils;
import me.sciguymjm.uberenchant.utils.plugins.VaultUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * For internal use.
 */
public class DelCommand extends UberTabCommand {

    public DelCommand() {
        super("udel");
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
                /*case "owner" -> {
                    if (hasPermission("uber.del.owner"))
                        owner(item);
                    else
                        response(Reply.PERMISSIONS);
                }*/
                default -> EnchantmentUtils.help(player, "udel");
            }
        } else
            response("&6%1$s", command.getUsage());
        return true;
    }

    @Override
    public List<String> onTab() {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            add(list, "uber.del.attribute", "attribute");
            add(list, "uber.del.enchant", "enchant");
            add(list, "uber.del.effect", "effect");
            add(list, "uber.del.lore", "lore");
            add(list, "uber.del.meta", "meta", Versions.isV1_20_4());
            add(list, "uber.del.name", "name");
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (args.length == 2)
            switch (args[0].toLowerCase()) {
                case "attribute" -> {
                    if (!item.getType().equals(Material.AIR) && item.hasItemMeta() && item.getItemMeta().hasAttributeModifiers())
                        list = item.getItemMeta().getAttributeModifiers().values().stream().map(AttributeModifier::getName).toList();
                }
                case "enchant" -> {
                    if (!item.getType().equals(Material.AIR) && !UberUtils.getAllMap(item).isEmpty())
                        list = EnchantmentUtils.find(player, item, args[1]);
                }
                case "effect" -> list = player.getActivePotionEffects().stream().map(effect -> effect.getType().getName().toLowerCase()).toList();
                case "meta" -> {
                    if (!Versions.isV1_20_4())
                        return list;
                    Map<UberEnchantment, Integer> map = UberUtils.getMap(item);
                    if (!item.getType().equals(Material.AIR) && !map.isEmpty())
                        list = map.keySet().stream().map(key -> key.getKey().getKey().toLowerCase()).toList();
                }
            }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("meta"))
                list = UberUtils.getTags(item, args[1]).stream()
                        .map(MetaTag::getName)
                        .filter(tag -> !tag.equalsIgnoreCase("level") && !tag.equalsIgnoreCase("duration")).toList();
        }
        return list;
    }

    private void enchant(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 2) {
            response("&a/udel enchant &c<enchantment>");
            response(Reply.ARGUMENTS);
            return;
        }
        Set<Enchantment> set = EnchantmentUtils.getMatches(args[1]);
        set.retainAll(UberUtils.getAllMap(item).keySet());
        if (EnchantmentUtils.multi(player, set))
            return;
        Enchantment enchantment = set.iterator().next();
        if (enchantment != null) {
            UberRecord enchant = UberConfiguration.getByEnchant(enchantment);
            if (!hasPermission("uber.del.enchant.%1$s", enchant.getName().toLowerCase())) {
                response(Reply.PERMISSIONS);
                return;
            }
            if (hasPermission("uber.del.enchant.free") || !VaultUtils.useEconomy()) {
                if (EnchantmentUtils.removeEnchantment(enchantment, item))
                    localized("&a", "actions.enchant.remove.success", enchant.getDisplayName());
                else
                    localized("&c", "actions.enchant.remove.no_enchant", enchant.getDisplayName());
                return;
            }
            if (VaultUtils.hasEconomy()) {
                if (VaultUtils.has(player, enchant.getRemovalCost())) {
                    if (EnchantmentUtils.removeEnchantment(enchantment, item)) {
                        VaultUtils.withdraw(player, enchant.getRemovalCost());
                        localized("&a", "actions.enchant.remove.pay_success", enchant.getDisplayName(), enchant.getRemovalCost());
                    } else
                        localized("&c", "actions.enchant.remove.no_enchant", enchant.getDisplayName());
                } else
                    localized("&c", "actions.enchant.remove.pay_more", enchant.getRemovalCost() - VaultUtils.getBalance(player));
            } else
                response(Reply.NO_ECONOMY);
            return;
        }
        localized("&c", "actions.enchant.not_exist");
    }

    /*
    /udel atrribute <atrribute> <name>
     */
    private void attribute(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 2) {
            response("&a/udel attribute &c<key>");
            response(Reply.ARGUMENTS);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;
        if (!meta.hasAttributeModifiers()) {
            localized("&c", "actions.attribute.remove.empty");
            return;
        }
        Attribute attribute;
        if (Versions.isV1_20_4())
            attribute = Registry.ATTRIBUTE.get(NamespacedKey.minecraft(args[1].split("/")[0].toLowerCase()));
        else
            attribute = Attribute.valueOf(args[1].split("/")[0].toLowerCase());
        if (attribute == null) {
            localized("&c", "actions.attribute.not_exist");
            return;
        }
        if (!meta.hasAttributeModifiers() || meta.getAttributeModifiers(attribute) == null) {
            localized("&c", "actions.attribute.no_attribute");
            return;
        }
        //List<AttributeModifier> attributes = new ArrayList<>(meta.getAttributeModifiers(attribute));
        Map<String, AttributeModifier> map = meta.getAttributeModifiers(attribute).stream().collect(Collectors.toMap(VersionUtils::key, value -> value));
        NamespacedKey key = new NamespacedKey(UberEnchant.instance(), args[1].toLowerCase());
        if (!map.containsKey(key.getKey())) {
            localized("&c", "actions.attribute.no_attribute");
            return;
        }
        AttributeModifier modifier = map.get(key.getKey());
        if (meta.removeAttributeModifier(attribute, modifier) && item.setItemMeta(meta))
            localized("&a", "actions.attribute.remove.success");
        else
            localized("&c", "actions.attribute.remove.fail");
        /*Multimap<Attribute, AttributeModifier> attributes = meta.getAttributeModifiers();
        attributes.keySet().forEach(k -> {
            response(VersionUtils.key(k) + ":");
            meta.getAttributeModifiers(k).forEach(value -> {
                response("    " + value.toString());
            });
        });*/
    }

    private void meta(ItemStack item) {
        if (!Versions.isV1_20_4())
            return;
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 3) {
            response(argue("&a/udel meta <enchantment> <tag>"));
            response(Reply.ARGUMENTS);
            return;
        }
        Set<UberEnchantment> set = EnchantmentUtils.getMatches(item, args[1]);
        if (EnchantmentUtils.multi(player, set))
            return;

        if (set == null)
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

        if (UberUtils.removeMeta(item, enchant, meta)) {
            localized("&a", "actions.meta.remove.success");
            return;
        }
        localized("&c", "actions.meta.remove.fail");
    }

    private void owner(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }

        if (!UberUtils.hasOwner(item)) {
            localized("&c", "actions.owner.del.no_owner");
            return;
        }

        UberUtils.removeOwner(item);

        localized("&a", "actions.owner.del.success");
    }

    private void effect() {
        if (args.length < 2) {
            response("&a/udel effect &c<effect>");
            response(Reply.ARGUMENTS);
            return;
        }
        PotionEffectType effect = EffectUtils.getEffect(args[1]);
        if (effect != null && player.hasPotionEffect(effect)) {
            EffectUtils.removeEffect(player, effect);
            localized("&a", "actions.effect.remove.success");
            return;
        }
        localized("&c", "actions.effect.remove.not_exist");
    }

    private void lore(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 2) {
            response("&a/udel lore &c<line#>");
            response(Reply.ARGUMENTS);
            return;
        }
        int index = UberUtils.offset(item);
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore() || (meta.hasLore() && meta.getLore().size() - index == 0)) {
            localized("&c", "actions.lore.remove.no_lore");
            return;
        }
        List<String> lore = meta.getLore();
        int size = lore.size() - index;
        if (size == 1) {
            lore.remove(index);
            meta.setLore(null);
            item.setItemMeta(meta);
            localized("&a", "actions.lore.remove.success");
            return;
        }
        int line;
        try {
            line = Integer.parseInt(args[1]);
        } catch (NumberFormatException err) {
            response("&a/udel %1$s &c%2$s", args);
            localized("&c", "actions.lore.remove.line_number");
            return;
        }
        if (line > (size - 1) || line < 0) {
            response("&a/udel %1$s &c%2$s", args);
            localized("&c", "actions.lore.remove.no_line");
            return;
        }
        if (Integer.toString(line).contains(".")) {
            response("&a/udel %1$s &c%2$s", args);
            response(Reply.WHOLE_NUMBER);
            return;
        }
        lore.remove(index + line);
        meta.setLore(lore);
        item.setItemMeta(meta);
        localized("&a", "actions.lore.remove.success");
    }

    private void name(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (!item.hasItemMeta()) {
            localized("&c", "actions.name.no_name");
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) {
            localized("&c", "actions.name.no_name");
            return;
        }
        if (VaultUtils.useEconomy() && VaultUtils.hasEconomy()) {
            double cost = VaultUtils.getCost("cost.name.remove");
            if (VaultUtils.has(player, cost)) {
                VaultUtils.withdraw(player, cost);
                meta.setDisplayName(null);
                item.setItemMeta(meta);
                localized("&a", "actions.name.remove.pay_success", cost);
            } else {
                localized("&c", "actions.name.remove.pay_fail", (cost - VaultUtils.getBalance(player)));
            }
        } else {
            meta.setDisplayName(null);
            item.setItemMeta(meta);
            localized("&a", "actions.name.remove.success");
        }
    }
}
