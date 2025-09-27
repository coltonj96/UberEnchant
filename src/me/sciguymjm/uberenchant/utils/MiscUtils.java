package me.sciguymjm.uberenchant.utils;

import me.sciguymjm.uberenchant.api.utils.UberUtils;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.BoolTag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MiscUtils {

    public static List<String> parse(Player player, String[] args) {
        List<String> list = new ArrayList<>();
        //BoolTagMap map = new BoolTagMap();
        //Map<String, String> map = new HashMap<>();
        String[] entries = args[3].toLowerCase().split(",");
        /*for (String entry : entries) {
            String[] pair = entry.split("=");
            if (pair.length > 0 && !pair[0].isEmpty()) {
                if (pair.length == 1)
                    map.put(pair[0], "");
                else
                    map.put(pair[0], pair[1]);
            }
        }
        map.forEach((k,v) -> Debugging.debug("[" + k + "=" + v + "]"));*/
        StringBuilder builder = new StringBuilder();

        for (String entry : entries) {
            test(player, args, entry, builder, list);
        }

        if (args[3].endsWith(","))
            test(player, args, "", builder, list);

        return list;
    }

    private static boolean isValid(String token) {
        return BoolTag.matches(token);
    }

    public static boolean isBool(String string) {
        return string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false");
    }

    private static void test(Player player, String[] args, String entry, StringBuilder builder, List<String> list) {
        ItemStack item = player.getInventory().getItemInMainHand();
        String[] pair = entry.split("=");
        String TAG = pair[0];
        String VALUE = pair.length == 2 ? pair[1] : "";
        List<BoolTag> tags = UberUtils.getTags(item, args[1]).stream().filter(value ->
                player.hasPermission(String.format("uber.set.meta.%1$s", value.getName()))
                        && value instanceof BoolTag tag
                        && !(args[3].toLowerCase().contains(tag.getName() + "=true") || args[3].toLowerCase().contains(tag.getName() + "=false"))
                        && (TAG.isEmpty() || tag.getName().startsWith(TAG) || tag.getName().contains(TAG))
        ).map(tag -> (BoolTag) tag).toList();

        if (pair.length < 2) {
            list.addAll(tags.stream().map(tag -> builder + tag.getName() + "=").toList());
        }

        tags.forEach(tag -> {
            if (isValid(TAG))
                get(VALUE).forEach(bool -> list.add(builder + TAG + "=" + bool));
            else
                bools().forEach(bool -> list.add(builder + tag.getName() + "=" + bool));
        });

        if (isValid(TAG) && pair.length == 2 && isBool(VALUE))
            builder.append(entry).append(",");
    }

    private static List<String> bools() {
        return List.of("true", "false");
    }

    private static boolean has(String arg) {
        return get(arg).isEmpty();
    }

    private static List<String> get(String arg) {
        return bools().stream().filter(bool -> bool.isEmpty() || bool.startsWith(arg) || arg.contains(bool)).toList();
    }
}
