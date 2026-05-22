package me.sciguymjm.uberenchant.utils;

public enum Versions {
    v1_19,
    v1_20_4,
    v1_20_5,
    v1_21,
    v1_21_3,
    v1_21_4,
    v1_21_11,
    v26_1;

    private final boolean check;

    Versions() {
        this.check = isAtLeast(name().substring(1).replace("_", "."));
    }

    public boolean atLeast() {
        return check;
    }

    public static boolean isAtLeast(String version) {
        return VersionUtils.isAtLeast(version);
    }

    public static boolean isV1_20_4() {
        return Versions.v1_20_4.atLeast();
    }
}
