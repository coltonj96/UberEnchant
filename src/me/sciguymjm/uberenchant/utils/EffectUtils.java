package me.sciguymjm.uberenchant.utils;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Utility class for handling PotionEffect related functions.<br>
 * (Meant for internal use)
 */
public class EffectUtils {

    /**
     * For internal use.
     *
     * @return String[]
     * @hidden
     */
    public static String[] listEffects() {
        List<String> list = new ArrayList<>();
        list.add("&6Effect (ID)");
        for (UberEffects effect : UberEffects.values()) {
            if (effect.getEffect() != null) {
                list.add(String.format("        &6&l%1$s (&c%2$s&6&l)", effect.getName(), effect.getId()));
            }
        }
        return list.toArray(String[]::new);
    }

    /**
     * For internal use.
     *
     * @param name String
     * @return PotionEffectType
     * @hidden
     */
    public static PotionEffectType getEffect(String name) {
        if (name.isEmpty())
            return null;
        Pattern pattern = Pattern.compile(name.toLowerCase());
        List<UberEffects> list = Arrays.stream(UberEffects.values())
                .filter(effect -> pattern.matcher(effect.getName().toLowerCase()).lookingAt() ||
                        pattern.matcher(String.valueOf(effect.getId())).matches())
                .toList();
        if (!list.isEmpty())
            return list.get(0).getEffect();
        return null;
    }


    /**
     * For internal use.
     *
     * @param name String
     * @return {@code List<String>}
     * @hidden
     */
    public static List<String> matchEffects(String name) {
        List<String> list = new ArrayList<>();
        if (name.isEmpty()) {
            for (UberEffects value : UberEffects.values()) {
                if (!list.contains(value.getName().toLowerCase()))
                    list.add(value.getName().toLowerCase());
            }
            return list;
        }
        for (UberEffects value : UberEffects.values()) {
            if (String.valueOf(value.getId()).startsWith(name) || value.getName().toLowerCase().startsWith(name.toLowerCase())) {
                list.add(value.getName().toLowerCase());
            }
        }
        return list;
    }


    /**
     * For internal use.
     *
     * @param effect PotionEffectType
     * @return int
     * @hidden
     */
    public static int getId(PotionEffectType effect) {
        return Stream.of(UberEffects.values()).filter(e ->
                e.getEffect().equals(effect)).map(UberEffects::getId).findFirst().orElse(0);
	/*for (UberEffects value : UberEffects.values()) {
	    if (value.getEffect().equals(effect)) {
		return value.getId();
	    }
	}
	return 0;*/
    }

    /**
     * For internal use.
     *
     * @param player Player
     * @param effect PotionEffectType
     * @hidden
     */
    public static void removeEffect(Player player, PotionEffectType effect) {
        if (player.hasPotionEffect(effect)) {
            player.removePotionEffect(effect);
            ChatUtils.response(player, "&a" + UberLocale.get("utils.effects.remove_success"));
        } else {
            ChatUtils.response(player, "&c" + UberLocale.get("utils.effects.remove_fail"));
        }
    }

    /**
     * For internal use.
     *
     * @param player   player
     * @param effect   PotionEffectType
     * @param duration int
     * @param level    int
     * @hidden
     */
    public static void setEffect(Player player, PotionEffectType effect, int duration, int level) {
        effect.createEffect(duration, level).apply(player);
    }
}
