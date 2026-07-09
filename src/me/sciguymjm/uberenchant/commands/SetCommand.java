package me.sciguymjm.uberenchant.commands;

import me.sciguymjm.uberenchant.UberEnchant;
import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.UberRunnable;
import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.BoolTag;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.DoubleTag;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.IntTag;
import me.sciguymjm.uberenchant.api.utils.persistence.UberMeta;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.MetaTag;
import me.sciguymjm.uberenchant.commands.abstraction.UberTabCommand;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import me.sciguymjm.uberenchant.enchantments.tasks.HeldEffectTask;
import me.sciguymjm.uberenchant.utils.*;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentUtils;
import me.sciguymjm.uberenchant.utils.plugins.VaultUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * For internal use.
 */
public class SetCommand extends UberTabCommand {

    public SetCommand() {
        super("uset");
    }

    @Override
    public boolean onCmd() {
        if (args.length != 0) {
            ItemStack item = player.getInventory().getItemInMainHand();
            switch (args[0].toLowerCase()) {
                case "attribute" -> action("attribute", this::attribute, item);
                case "effect" -> action("effect", this::effect);
                case "lore" -> action("lore", this::lore, item);
                case "meta" -> action("meta", this::meta, item);
                case "name" -> action("name", this::name, item);
                case "hidden" -> action("hidden", this::hidden, item);
                case "unbreakable" -> action("unbreakable", this::unbreakable, item);
                /*case "owner" -> {
                    if (hasPermission("uber.set.owner"))
                        owner(item);
                    else
                        response(Reply.PERMISSIONS);
                }*/
                default -> EnchantmentUtils.help(player, "uset");
            }
        } else
            response("&6%1$s", command.getUsage());
        return true;
    }

    @Override
    public List<String> onTab() {
        List<String> list = new ArrayList<>();
        ItemStack item = player.getInventory().getItemInMainHand();
        switch (args.length) {
            case 1 -> {
                add(list, "uber.set.attribute", "attribute");
                add(list, "uber.set.effect", "effect");
                add(list, "uber.set.lore", "lore");
                add(list, "uber.set.meta", "meta", Versions.isV1_20_4());
                add(list, "uber.set.name", "name");
                add(list, "uber.set.hidden", "hidden");
                add(list, "uber.set.unbreakable", "unbreakable");
                /*if (hasPermission("uber.set.owner"))
                    list.add("owner");*/
            }
            case 2 -> {
                switch (args[0].toLowerCase()) {
                    case "attribute" -> {
                        if (!item.getType().equals(Material.AIR) && item.hasItemMeta() && item.getItemMeta().hasAttributeModifiers())
                            list = item.getItemMeta().getAttributeModifiers().values().stream().map(AttributeModifier::getName).toList();
                    }
                    case "hidden" -> add(list, "uber.set.hidden", "true", "false");
                    case "unbreakable" -> add(list, "uber.set.unbreakable", "true", "false");
                    case "effect" -> list = EffectUtils.matchEffects(args[1]);
                    case "meta" -> {
                        if (!Versions.isV1_20_4())
                            return list;
                        Map<UberEnchantment, Integer> map = UberUtils.getMap(item);
                        if (!item.getType().equals(Material.AIR) && !map.isEmpty())
                            list = map.keySet().stream().map(key -> key.getKey().getKey().toLowerCase()).toList();
                    }
                }
            }
        }
        if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("attribute")) {
                if (args.length == 3) {
                    list = new ArrayList<>(list);
                    list.add("value");
                    list.add("operation");
                    list.add("group");
                }
                if (args.length == 4)
                    switch (args[2].toLowerCase()) {
                        case "operation" -> list = Arrays.stream(AttributeModifier.Operation.values()).map(op -> op.name().toLowerCase()).filter(name -> args[3].isBlank() || name.contains(args[3])).distinct().toList();
                        case "group" -> list = Arrays.stream(EquipmentSlot.values()).map(slot -> slot.getGroup().toString()).filter(name -> args[4].isBlank() || name.contains(args[4])).distinct().toList();
                    }
            }
            if (args[0].equalsIgnoreCase("meta")) {
                if (args.length == 3)
                    list = UberUtils.getTags(item, args[1]).stream().map(MetaTag::getName).filter(name ->
                            hasPermission(String.format("uber.set.meta.%1$s", name))
                    ).toList();
                if (args.length == 4 && BoolTag.matches(args[2])) {
                    list = new ArrayList<>(list);
                    list.add("true");
                    list.add("false");
                }
            }
        }
        return list;
    }

    private void attribute(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 4) {
            response(argue("&a/uset attribute <key> <value|operation|group> <value>"));
            //response("&a/uset attribute &c<key> <value|operation|group> <value>");
            response(Reply.ARGUMENTS);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return;
        if (!meta.hasAttributeModifiers()) {
            localized("&c", "actions.attribute.set.empty");
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

        Modifier mod = new Modifier(modifier);

        if (!meta.removeAttributeModifier(attribute, modifier)) {
            localized("&c", "actions.atrribute.remove.fail");
            return;
        }

        switch (args[2].toLowerCase()) {
            case "value" -> {
                try {
                    mod.amount = Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    response(Reply.DECIMAL_NUMBER);
                    return;
                }
            }
            case "operation" -> {
                try {
                    mod.operation = AttributeModifier.Operation.valueOf(args[3].toUpperCase());
                }  catch (IllegalArgumentException e) {
                    localized("&c", "actions.attribute.operation.not_exist");
                    return;
                }
            }
            case "group" -> {
                mod.group = EquipmentSlotGroup.getByName(args[3]);
                if (mod.group == null) {
                    localized("&c", "actions.attribute.group.not_exist");
                    return;
                }
            }
        }

        String format = String.format(
                Locale.ROOT,
                "%1$s/%2$s/%3$s/%4$s",
                VersionUtils.key(attribute),
                mod.operation.name().toLowerCase(),
                mod.amount,
                mod.group
        );

        modifier = new AttributeModifier(new NamespacedKey(UberEnchant.instance(), format), mod.amount, mod.operation, mod.group);

        if (meta.addAttributeModifier(attribute, modifier) && item.setItemMeta(meta))
            localized("&a", "actions.attribute.set.success");
        else
            localized("&c", "actions.attribute.set.fail");
        /*Multimap<Attribute, AttributeModifier> attributes = meta.getAttributeModifiers();
        attributes.keySet().forEach(k -> {
            response(VersionUtils.key(k) + ":");
            meta.getAttributeModifiers(k).forEach(value -> {
                response("    " + value.toString());
            });
        });*/
    }

    private static class Modifier {

        private AttributeModifier.Operation operation;
        private double amount;
        private EquipmentSlotGroup group;

        private Modifier(AttributeModifier modifier) {
            this.operation = modifier.getOperation();
            this.amount = modifier.getAmount();
            this.group = modifier.getSlotGroup();
        }

        private void setOperation(AttributeModifier.Operation operation) {
            this.operation = operation;
        }

        private void setAmount(double amount) {
            this.amount = amount;
        }

        private void setGroup(EquipmentSlotGroup group) {
            this.group = group;
        }
    }

    private void meta(ItemStack item) {
        /*UberEnchantmentsAddedEvent event = new UberEnchantmentsAddedEvent(player, item, null);
        if (event.isCancelled())
            return;*/
        if (!Versions.isV1_20_4())
            return;
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 4) {
            response(argue("&a/uset meta <enchantment> <tag> <value>"));
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

        if (!UberUtils.containsMeta(item, enchant, meta)) {
            localized("&c", "actions.meta.set.add_tag");
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
                    response("&a/uset %1$s %2$s %3$s &c%4$s", args);
                    return;
                }
            }
            if (enchant instanceof EffectEnchantment effect && tag == BoolTag.ON_HELD && !BoolTag.ON_HELD.test(item, effect) && value)
                UberRunnable.addTask(player.getUniqueId().toString() + "_HELD", new HeldEffectTask(player, effect, (p, i, e) ->
                        i.getType().equals(Material.AIR) ||
                                !e.containsEnchantment(i) ||
                                !BoolTag.ON_HELD.test(i, e)));
            UberUtils.setMetaTag(item, enchant, tag.asMeta(), value);
            success = true;
        }
        if (!success && meta.tag() instanceof IntTag tag)  {
            int value;
            try {
                value = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                response("&a/uset %1$s %2$s %3$s &c%4$s", args);
                response(Reply.WHOLE_NUMBER);
                return;
            }
            UberUtils.setMetaTag(item, enchant, tag.asMeta(), value);
            success = true;
        }
        if (!success && meta.tag() instanceof DoubleTag tag)  {
            double value;
            try {
                value = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                response("&a/uset %1$s %2$s %3$s &c%4$s", args);
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
        if (success)
            localized("&a", "actions.meta.set.success");
        else
            localized("&c", "actions.meta.set.fail");
    }

    private void owner(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }

        UberUtils.setOwner(item, player.getUniqueId());

        localized("&a", "actions.owner.set.success");
    }

    private void effect() {
        if (args.length < 4) {
            response(argue("&a/uset effect <effect> <duration> <level>"));
            response(Reply.ARGUMENTS);
            return;
        }
        int level;
        int duration;
        try {
            duration = Integer.parseInt(args[2]);
            duration *= 20;
        } catch (NumberFormatException e) {
            response("&a/uset %1$s %2$s %3$s &c%4$s", args);
            response(Reply.WHOLE_NUMBER);
            return;
        }
        try {
            level = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            response("&a/uset %1$s %2$s &c%3$s &a%4$s", args);
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
        localized("&a", "actions.effect.set.success");
    }

    private void lore(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 3) {
            response(argue("&a/uset lore <line#> <text...>"));
            response(Reply.ARGUMENTS);
            return;
        }
        int index = UberUtils.offset(item);
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore() || (meta.hasLore() && meta.getLore().size() - index == 0)) {
            localized("&c", "actions.lore.set.no_lore");
            return;
        }
        List<String> lore = meta.getLore();
        int size = lore.size() - index;
        int line;
        try {
            line = Integer.parseInt(args[1]);
        } catch (NumberFormatException err) {
            StringBuilder message = new StringBuilder("&a/uset lore &c%1$s &a%2$s");
            if (args.length > 3)
                for (int arg = 3; arg < args.length; arg++)
                    message.append(" ").append(args[arg]);
            response(message.toString().trim(), args[1], args[2]);
            response(Reply.WHOLE_NUMBER);
            return;
        }
        if (line > (size - 1) || line < 0) {
            StringBuilder message = new StringBuilder("&a/uset lore &c%1$s &a%2$s");
            if (args.length > 3)
                for (int arg = 3; arg < args.length; arg++)
                    message.append(" ").append(args[arg]);
            response(message.toString().trim(), args[1], args[2]);
            localized("&c", "actions.lore.set.no_line");
            return;
        }
        StringBuilder message = new StringBuilder(args[2]);
        if (args.length > 3)
            for (int arg = 3; arg < args.length; arg++)
                message.append(" ").append(args[arg]);
        String name = ChatUtils.color(message.toString().trim());
        lore.set(index + line, name.replace("%null", ""));
        meta.setLore(lore);
        item.setItemMeta(meta);
        localized("&a", "actions.lore.set.success");
    }

    private void name(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 2) {
            response("&a/uset name &c<text...>");
            response(Reply.ARGUMENTS);
            return;
        }
        StringBuilder message = new StringBuilder(args[1]);
        for (int arg = 2; arg < args.length; arg++)
            message.append(" ").append(args[arg]);
        String name = ChatUtils.color(message.toString().trim());
        ItemMeta meta = item.getItemMeta();
        if (hasPermission("uber.set.name.free") || !VaultUtils.useEconomy()) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
            localized("&a", "actions.name.set.success");
            return;
        }
        if (VaultUtils.hasEconomy()) {
            double cost = VaultUtils.getCost("cost.name.set");
            if (VaultUtils.has(player, cost)) {
                VaultUtils.withdraw(player, cost);
                meta.setDisplayName(name);
                item.setItemMeta(meta);
                localized("&a", "actions.name.set.pay_success", cost);
            } else
                localized("&c", "actions.name.set.pay_fail", cost - VaultUtils.getBalance(player));
        } else {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
            localized("&a", "actions.name.set.success");
        }
    }

    private void hidden(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 2) {
            response("&a/uset hidden &c<true | false>");
            response(Reply.ARGUMENTS);
            return;
        }
        switch (args[1].toLowerCase()) {
            case "false", "f", "0" -> response(EnchantmentUtils.setHideEnchants(item, false));
            case "true", "t", "1" -> response(EnchantmentUtils.setHideEnchants(item, true));
            default -> {
                response("&a/uset hidden &c%1$s", args[1]);
                response(Reply.INVALID);
            }
        }
    }

    private void unbreakable(ItemStack item) {
        if (item.getType().equals(Material.AIR)) {
            response(Reply.HOLD_ITEM);
            return;
        }
        if (args.length < 2) {
            response("&a/uset unbreakable &c<true | false>");
            response(Reply.ARGUMENTS);
            return;
        }
        switch (args[1].toLowerCase()) {
            case "false", "f", "0" -> response(setUnbreakable(item, false));
            case "true", "t", "1" -> response(setUnbreakable(item, true));
            default -> {
                response("&a/uset unbreakable &c%1$s", args[1]);
                response(Reply.INVALID);
            }
        }
    }

    private static String setUnbreakable(ItemStack item, boolean value) {
        ItemMeta meta = item.getItemMeta();
        boolean old = meta.isUnbreakable();
        if (old == value)
            return UberLocale.getCF("&c", "utils.unbreakable.set.no_change", old);
        meta.setUnbreakable(value);
        item.setItemMeta(meta);
        return UberLocale.getCF("&a", "utils.unbreakable.set.success", value);
    }
}
