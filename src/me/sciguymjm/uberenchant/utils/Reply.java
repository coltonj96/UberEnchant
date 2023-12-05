package me.sciguymjm.uberenchant.utils;

/**
 * For internal use.
 */
public enum Reply {

    /**
     * For internal use.
     */
    PERMISSIONS("&c", "reply.permission_denied"),

    /**
     * For internal use.
     */
    ARGUMENTS("&c", "reply.insufficient_argument"),

    /**
     * For internal use.
     */
    INVALID("&c", "reply.invalid_argument"),

    /**
     * For internal use.
     */
    HOLD_ITEM("&c", "reply.hold_item"),

    /**
     * For internal use.
     */
    NO_ECONOMY("&c", "reply.no_economy"),

    /**
     * For internal use.
     */
    WHOLE_NUMBER("&c", "reply.whole_number");

    private String color;
    private String key;

    Reply(String color, String key) {
        this.color = color;
        this.key = key;
    }

    public String get() {
        return color + UberLocale.get(key);
    }
}
