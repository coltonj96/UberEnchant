package me.sciguymjm.uberenchant.api.utils.persistence.tags.utils;

import me.sciguymjm.uberenchant.api.utils.persistence.tags.BoolTag;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.MetaTag;
import me.sciguymjm.uberenchant.utils.MiscUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BoolTagMap {

    private Map<BoolTag, Boolean> map;

    public BoolTagMap() {
        map = new HashMap<>();
    }

    private record data<T>(MetaTag<T> tag, T value){}

    public BoolTagMap(Map<BoolTag, Boolean> map) {
        this.map = map;
    }

    public BoolTagMap(String string) {
        this();
        parse(string);
    }

    public static BoolTagMap fromString(String string) {
        return new BoolTagMap(string);
    }

    public static boolean isValid(String string) {
        for (String token : string.toLowerCase().replace(" ", "").split(",")) {
            String[] pair = token.split("=");
            if (pair.length != 2 || !BoolTag.matches(pair[0]) || !MiscUtils.isBool(pair[1]))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return map.entrySet().stream()
                .map(e -> String.format("%1$s=%2$s", e.getKey().getName(), e.getValue().toString()))
                .collect(Collectors.joining(","));
    }

    protected void parse(String string) {
        for (String token : string.toLowerCase().replace(" ", "").split(",")) {
            String[] pair = token.split("=");
            if (pair.length != 2 || !BoolTag.matches(pair[0]) || !MiscUtils.isBool(pair[1]))
                continue;
            BoolTag tag = BoolTag.fromString(pair[0]);
            Boolean value = Boolean.parseBoolean(pair[1]);
            map.put(tag, value);
        }
    }

    public Map<BoolTag, Boolean> getMap() {
        return map;
    }

    public void addAll(Map<? extends BoolTag, ? extends Boolean> map) {
        this.map.putAll(map);
    }

    public void addAll(String string) {
        parse(string);
    }

    public void setMap(Map<BoolTag, Boolean> map) {
        this.map = map;
    }

    public Boolean get(BoolTag tag) {
        return map.get(tag);
    }

    public void put(BoolTag tag, Boolean value) {
        map.put(tag, value);
    }

    public void remove(BoolTag tag) {
        map.remove(tag);
    }
}
