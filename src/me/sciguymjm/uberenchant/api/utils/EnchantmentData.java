package me.sciguymjm.uberenchant.api.utils;

import org.bukkit.plugin.Plugin;

public interface EnchantmentData {

    Plugin getPlugin();
    double getWeight();
    int getMinBase();
    int getMinPer();
    int getMaxBase();
    int getMaxPer();
    boolean isTreasure();
}
