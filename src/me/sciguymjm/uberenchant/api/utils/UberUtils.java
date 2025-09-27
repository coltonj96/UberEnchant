package me.sciguymjm.uberenchant.api.utils;

import me.sciguymjm.uberenchant.api.UberEnchantment;
import me.sciguymjm.uberenchant.api.utils.persistence.PDCItemUtils;
import me.sciguymjm.uberenchant.api.utils.persistence.UberMeta;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.BoolTag;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.IntTag;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.MetaTag;
import me.sciguymjm.uberenchant.api.utils.persistence.tags.UUIDTag;
import me.sciguymjm.uberenchant.enchantments.abstraction.EffectEnchantment;
import me.sciguymjm.uberenchant.utils.ChatUtils;
import me.sciguymjm.uberenchant.utils.VersionUtils;
import me.sciguymjm.uberenchant.utils.enchanting.EnchantmentUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Custom enchantment related utility class
 *
 * Data structure:
 *
 * ItemStack:(PersistentDataContainer)
 * -> UberEnchantment:(PersistentDataContainer)
 * --> Tag1(Type)
 * --> Tag2(Type)
 * --> Tag3(Type)
 * --> Tag...(Type)
 *
 */
public class UberUtils extends PDCItemUtils {

    public static final NamespacedKey uberEnchantment = generateKey("uberenchantment");
    public static final NamespacedKey storedUberEnchantment = generateKey("storeduberenchantment");

    public static PersistentDataContainer getData(ItemStack item) {
        return getPDC(item, uberEnchantment);
    }

    public static boolean hasData(ItemStack item) {
        return has(item, uberEnchantment);
    }

    public static boolean hasMeta(ItemStack item, UberEnchantment enchantment) {
        return hasData(item) && has(getData(item), enchantment.getKey(), TAG);
    }

    public static boolean hasStoredData(ItemStack item) {
        return has(item, storedUberEnchantment);
    }

    public static boolean containsData(ItemStack item, UberEnchantment enchantment) {
        return hasData(item) && has(getData(item), enchantment.getKey());
    }

    public static boolean containsMeta(ItemStack item, UberEnchantment enchantment, MetaTag<?> tag) {
        return containsMeta(item, enchantment, tag.asMeta());
    }

    public static boolean containsMeta(ItemStack item, UberEnchantment enchantment, UberMeta<?> meta) {
        return hasMeta(item, enchantment) && has(getPDC(getData(item), enchantment.getKey()), meta.getKey(), meta.getType());
    }

    public static boolean containsStoredData(ItemStack item, UberEnchantment enchantment) {
        return hasStoredData(item) && has(getData(item), enchantment.getKey());
    }

    public static <T> T getTag(ItemStack item, UberEnchantment enchantment, NamespacedKey namespace, UberMeta<T> meta) {
        PersistentDataContainer data = getPDC(item, namespace);
        if (containsMeta(item, enchantment, meta))
            return get(getPDC(data, enchantment.getKey()), meta.getKey(), meta.getType());
        if (meta == UberMeta.LEVEL)
            return get(data, enchantment.getKey(), meta.getType());
        return null;
    }

    public static <T> T getTag(ItemStack item, UberEnchantment enchantment, NamespacedKey namespace, MetaTag<T> tag) {
        return getTag(item, enchantment, namespace, tag.asMeta());
    }

    public static <T> T getMetaTag(ItemStack item, UberEnchantment enchantment, UberMeta<T> meta) {
        return getTag(item, enchantment, uberEnchantment, meta);
    }

    public static <T> T getMetaTag(ItemStack item, UberEnchantment enchantment, MetaTag<T> tag) {
        return getTag(item, enchantment, uberEnchantment, tag);
    }

    public static <T> T getStoredMetaTag(ItemStack item, UberEnchantment enchantment, MetaTag<T> tag) {
        return getTag(item, enchantment, storedUberEnchantment, tag);
    }

    public static <T> T getStoredMetaTag(ItemStack item, UberEnchantment enchantment, UberMeta<T> meta) {
        return getTag(item, enchantment, storedUberEnchantment, meta);
    }

    public static PersistentDataContainer getStoredData(ItemStack item) {
        return getPDC(item, storedUberEnchantment);
    }

    private static Map<UberEnchantment, Integer> getCustomMap(ItemStack item, NamespacedKey namespace) {
        Map<UberEnchantment, Integer> map = new HashMap<>();
        if (has(item, namespace)) {
            PersistentDataContainer data = getPDC(item, namespace);
            if (notNull(data)) {
                data.getKeys().forEach(key -> {
                    if (UberEnchantment.containsKey(key)) {
                        if (has(data, key, PersistentDataType.INTEGER)) {
                            map.put(UberEnchantment.getByKey(key), get(data, key, PersistentDataType.INTEGER));
                        } else {
                            PersistentDataContainer meta = getPDC(data, key);
                            UberMeta<Integer> level = UberMeta.LEVEL;
                            if (has(meta, level.getKey()))
                                map.put(UberEnchantment.getByKey(key), get(meta, level.getKey(), level.getType()));
                        }
                    }
                });
            }
        }
        return map;
    }

    private static List<MetaTag<?>> getTags(ItemStack item, NamespacedKey enchantment, NamespacedKey namespace) {
        List<MetaTag<?>> list =  new ArrayList<>();
        if (notNull(item, enchantment, namespace) && has(item, namespace)) {
            PersistentDataContainer data = getPDC(item, namespace);
            if (notNull(data) && has(data, enchantment))
                getPDC(data, enchantment).getKeys().forEach(key -> list.add(MetaTag.byKey(key)));
        }
        return list;
    }

    private static List<MetaTag<?>> getTags(ItemStack item, UberEnchantment enchantment, NamespacedKey namespace) {
        if (notNull(enchantment))
            return getTags(item, enchantment.getKey(), namespace);
        return new ArrayList<>();
    }

    private static List<MetaTag<?>> getTags(ItemStack item, String name, NamespacedKey namespace) {
        return getTags(item, UberEnchantment.getByName(name), namespace);
    }

    public static List<MetaTag<?>> getTags(ItemStack item, UberEnchantment enchantment) {
        return getTags(item, enchantment, uberEnchantment);
    }

    public static List<MetaTag<?>> getTags(ItemStack item, String name) {
        return getTags(item, name, uberEnchantment);
    }

    public static Map<UberEnchantment, Integer> getMap(ItemStack item) {
        return getCustomMap(item, uberEnchantment);
    }

    public static Set<UberEnchantment> getEnchants(ItemStack item) {
        return getCustomMap(item, uberEnchantment).keySet();
    }

    public static Map<UberEnchantment, Integer> getStoredMap(ItemStack item) {
        return getCustomMap(item, storedUberEnchantment);
    }

    public static Map<Enchantment, Integer> getAllMap(ItemStack item) {
        Map<Enchantment, Integer> map = new HashMap<>(item.getEnchantments());
        if (VersionUtils.isAtLeast("1.20.4"))
            map.putAll(getMap(item));
        return map;
    }

    public static Map<Enchantment, Integer> getAllStoredMap(ItemStack item) {
        Map<Enchantment, Integer> map = new HashMap<>();
        if (item.getItemMeta() instanceof EnchantmentStorageMeta meta) {
            if (meta.hasStoredEnchants())
                map.putAll(meta.getStoredEnchants());
            if (VersionUtils.isAtLeast("1.20.4"))
                map.putAll(getStoredMap(item));
        }
        return map;
    }

    private static <T> void addMeta(ItemStack item, UberEnchantment enchantment, NamespacedKey namespace, UberMeta<T> tag, T value) {
        removeEnchantmentLore(item);
        if (!has(item, namespace))
            createPDC(item, namespace);
        if (!notNull(getPDC(item, namespace))) {
            addEnchantmentLore(item);
            return;
        }
        if (!has(getPDC(item, namespace), enchantment.getKey()))
            set(item, namespace, TAG, set(getPDC(item, namespace), enchantment.getKey(), TAG, createPDC(item)));
        setTag(item, enchantment, namespace, tag, value);
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasEnchants())
            setEnchantmentGlintOverride(meta, true);
        item.setItemMeta(meta);
        addEnchantmentLore(item);
    }

    private static void addCustom(ItemStack item, UberEnchantment enchantment, int level, NamespacedKey namespace) {
        removeEnchantmentLore(item);
        if (!has(item, namespace))
            createPDC(item, namespace);
        if (!notNull(getPDC(item, namespace))) {
            addEnchantmentLore(item);
            return;
        }
        if (!has(getPDC(item, namespace), enchantment.getKey()))
            set(item, namespace, TAG, set(getPDC(item, namespace), enchantment.getKey(), TAG, createPDC(item)));
        setTag(item, enchantment, namespace, UberMeta.LEVEL, level);
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasEnchants())
            setEnchantmentGlintOverride(meta, true);
        item.setItemMeta(meta);
        addEnchantmentLore(item);
    }

    private static <T> void setTag(ItemStack item, UberEnchantment enchantment, NamespacedKey namespace, UberMeta<T> tag, T value) {
        PersistentDataContainer data = getPDC(item, namespace);
        PersistentDataContainer meta = getPDC(data, enchantment.getKey());
        set(meta, tag.getKey(), tag.getType(), value);
        set(data, enchantment.getKey(), TAG, meta);
        set(item, namespace, TAG, data);
    }

    public static UUID getOwner(ItemStack item, UUID id) {
        PersistentDataContainer data = getPDC(item, uberEnchantment);
        return get(data, generateKey("owner"), UUIDTag.PLAYER.getType());
    }

    public static void setOwner(ItemStack item, UUID id) {
        PersistentDataContainer data = getPDC(item, uberEnchantment);
        set(data, generateKey("owner"), UUIDTag.PLAYER.getType(), id);
        set(item, uberEnchantment, TAG, data);
    }

    public static void removeOwner(ItemStack item) {
        PersistentDataContainer data = getPDC(item, uberEnchantment);
        remove(data, generateKey("owner"));
    }

    public static boolean hasOwner(ItemStack item) {
        PersistentDataContainer data = getPDC(item, uberEnchantment);
        return has(data, generateKey("owner"));
    }

    private static <T> void setMeta(ItemStack item, UberEnchantment enchantment, NamespacedKey namespace, UberMeta<T> tag, T value) {
        removeEnchantmentLore(item);
        PersistentDataContainer data = getPDC(item, namespace);
        if (!has(item, namespace) || !notNull(data) || !has(data, enchantment.getKey())) {
            addEnchantmentLore(item);
            return;
        }
        setTag(item, enchantment, namespace, tag, value);
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasEnchants())
            setEnchantmentGlintOverride(meta, true);
        item.setItemMeta(meta);
        addEnchantmentLore(item);
    }

    public static <T> void setMetaTag(ItemStack item, UberEnchantment enchantment, UberMeta<T> tag, T value) {
        setMeta(item, enchantment, uberEnchantment, tag, value);
    }

    public static <T> void setStoredMetaTag(ItemStack item, UberEnchantment enchantment, UberMeta<T> tag, T value) {
        setMeta(item, enchantment, storedUberEnchantment, tag, value);
    }

    /*private static void addCustom(ItemStack item, UberEnchantment enchantment, int level, NamespacedKey namespace) {
        removeEnchantmentLore(item);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data;
        PersistentDataContainer mdata;
        if (!has(item, namespace)) {
            data = meta.getPersistentDataContainer();
            mdata = data.getAdapterContext().newPersistentDataContainer();
            mdata.set(enchantment.getKey(), TAG, data.getAdapterContext().newPersistentDataContainer());
            data.set(namespace, TAG, mdata);
            item.setItemMeta(meta);
            meta = item.getItemMeta();
        }
        data = meta.getPersistentDataContainer().get(namespace, TAG);
        if (data == null) {
            addEnchantmentLore(item);
            return;
        }
        if (!has(data, enchantment.getKey())) {
            data.set(enchantment.getKey(), TAG, data.getAdapterContext().newPersistentDataContainer());
            item.setItemMeta(meta);
            meta = item.getItemMeta();
        }
        mdata = data.get(enchantment.getKey(), TAG);
        mdata.set(generateKey("level"), PersistentDataType.INTEGER, level);
        data.set(enchantment.getKey(), TAG, mdata);
        meta.getPersistentDataContainer().set(namespace, TAG, data);
        if (!meta.hasEnchants())
            meta.setEnchantmentGlintOverride(true);
        item.setItemMeta(meta);
        addEnchantmentLore(item);
    }

    public static void addData(ItemStack item, UberEnchantment enchantment, int level) {
        //createContext(item, enchantment, uberEnchantment);
        addCustom(item, enchantment, level, uberEnchantment);
    }

    public static void addStoredData(ItemStack item, UberEnchantment enchantment, int level) {
        addCustom(item, enchantment, level, storedUberEnchantment);
    }*/

    public static <T> void addMetaData(ItemStack item, UberEnchantment enchantment, UberMeta<T> tag, T value) {
        addMeta(item, enchantment, uberEnchantment, tag, value);
    }

    public static void addData(ItemStack item, UberEnchantment enchantment, int level) {
        addCustom(item, enchantment, level, uberEnchantment);
        if (enchantment instanceof EffectEnchantment effect) {
            addMeta(item, enchantment, uberEnchantment, UberMeta.DURATION, level);
            effect.getTagDefaults(PersistentDataType.BOOLEAN).forEach((k, v) -> addMeta(item, enchantment, uberEnchantment, k.asMeta(), v));
            effect.getTagDefaults(PersistentDataType.INTEGER).forEach((k, v) -> addMeta(item, enchantment, uberEnchantment, k.asMeta(), v));
        }
    }

    public static void addStoredData(ItemStack item, UberEnchantment enchantment, int level) {
        addCustom(item, enchantment, level, storedUberEnchantment);
    }

    private static int removeCustom(ItemStack item, UberEnchantment enchantment, NamespacedKey namespace) {
        int level = 0;
        if (has(item, namespace)) {
            removeEnchantmentLore(item);
            PersistentDataContainer data = getPDC(item, namespace);
            if (data == null) {
                addEnchantmentLore(item);
                return 0;
            }
            if (has(data, enchantment.getKey())) {
                level = enchantment.getLevel(item);
                remove(data, enchantment.getKey());
                set(item, namespace, TAG, data);
            }
            if (data.isEmpty())
                remove(item, namespace);

            ItemMeta meta = item.getItemMeta();
            if (!has(item, namespace)) {
                setEnchantmentGlintOverride(meta, null);
                item.setItemMeta(meta);
            }
            addEnchantmentLore(item);
        }
        return level;
    }

    private static void setEnchantmentGlintOverride(ItemMeta meta, Boolean value) {
        try {
            Method m = meta.getClass().getMethod("setEnchantmentGlintOverride", Boolean.class);
            m.setAccessible(true);
            m.invoke(meta, value);
            m.setAccessible(false);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
    }

    private static <T> void delTag(ItemStack item, UberEnchantment enchantment, NamespacedKey namespace, UberMeta<T> tag) {
        PersistentDataContainer data = getPDC(item, namespace);
        PersistentDataContainer meta = getPDC(data, enchantment.getKey());
        remove(meta, tag.getKey());
        set(data, enchantment.getKey(), TAG, meta);
        set(item, namespace, TAG, data);
    }

    private static <T> boolean removeTag(ItemStack item, UberEnchantment enchantment, NamespacedKey namespace, UberMeta<T> tag) {
        if (tag == UberMeta.LEVEL || tag == UberMeta.DURATION)
            return false;
        if (has(item, namespace)) {
            removeEnchantmentLore(item);
            PersistentDataContainer data = getPDC(item, namespace);
            if (data == null || !has(data, enchantment.getKey())) {
                addEnchantmentLore(item);
                return false;
            }
            PersistentDataContainer meta = getPDC(data, enchantment.getKey());
            if (!has(meta, tag.getKey()))
                return false;
            remove(meta, tag.getKey());
            set(data, enchantment.getKey(), TAG, meta);
            set(item, namespace, TAG, data);

            ItemMeta itemMeta = item.getItemMeta();
            if (notNull(itemMeta) && !has(item, namespace)) {
                setEnchantmentGlintOverride(itemMeta, null);
                item.setItemMeta(itemMeta);
            }
            addEnchantmentLore(item);
        }
        return true;
    }

    public static int removeData(ItemStack item, UberEnchantment enchantment) {
        return removeCustom(item, enchantment, uberEnchantment);
    }

    public static <T> boolean removeMeta(ItemStack item, UberEnchantment enchantment, UberMeta<T> tag) {
        return removeTag(item, enchantment, uberEnchantment, tag);
    }

    public static int removeStoredData(ItemStack item, UberEnchantment enchantment) {
        return removeCustom(item, enchantment, storedUberEnchantment);
    }

    /**
     * Adds the specified UberEnchantment to the item with specified level. Also
     * adds the lore for displaying the enchantment on the item.
     *
     * @param enchant - The enchantment to add
     * @param item    - The item
     * @param level   - The level
     */
    public static void addEnchantment(UberEnchantment enchant, ItemStack item, int level) {
        /* Minecraft 1.20.2
        UberUtils.removeEnchantmentLore(item);
        item.addUnsafeEnchantment(enchant, level);
        UberUtils.addEnchantmentLore(item);
        */
        addData(item, enchant, level);
    }

    /**
     * Adds the map of Enchantments to the item with specified level. Also
     * adds the lore for displaying the enchantment on the item.
     *
     * @param enchants - The enchantments to add
     * @param item    - The item
     */
    public static void addEnchantments(Map<? extends Enchantment, Integer> enchants, ItemStack item) {
        /* Minecraft 1.20.2
        removeEnchantmentLore(item);
        EnchantmentUtils.setEnchantments(enchants, item);
        addEnchantmentLore(item);
        */
        EnchantmentUtils.setEnchantments(enchants, item);
    }

    /**
     * Adds the specified UberEnchantment to the Enchanted Book with specified level. Also
     * adds the lore for displaying the enchantment on the book.
     *
     * @param enchant - The enchantment to add
     * @param book    - The Enchanted Book
     * @param level   - The level
     */
    public static void addStoredEnchantment(UberEnchantment enchant, ItemStack book, int level) {
        if (!book.getType().equals(Material.ENCHANTED_BOOK))
            return;
        addStoredData(book, enchant, level);
        /* Minecraft 1.20.2
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        UberUtils.removeEnchantmentLore(book);
        meta.addStoredEnchant(enchant, level, true);
        book.setItemMeta(meta);
        UberUtils.addEnchantmentLore(book);
        */
    }

    /**
     * Adds the map of Enchantments to the book. Also
     * adds the lore for displaying the enchantment on the book.
     *
     * @param enchants - The enchantments to add
     * @param item    - The item
     */
    public static void addStoredEnchantments(Map<? extends Enchantment, Integer> enchants, ItemStack item) {
        if (!item.getType().equals(Material.ENCHANTED_BOOK))
            return;
        //EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();

        //UberUtils.removeEnchantmentLore(item);
        EnchantmentUtils.setStoredEnchantments(enchants, item);
        //UberUtils.addEnchantmentLore(item);
    }

    /**
     * Removes the specified UberEnchantment from the item, returning the level
     * of the enchantment or 0.
     *
     * @param enchantment - The enchantment to remove
     * @param item        - The item
     * @return The enchantment level or 0
     */
    public static int removeEnchantment(UberEnchantment enchantment, ItemStack item) {
        /* Minecraft 1.20.2
        if (item.hasItemMeta() && enchantment.containsEnchantment(item)) {
            UberUtils.removeEnchantmentLore(item);
            int level = item.removeEnchantment(enchantment);
            UberUtils.addEnchantmentLore(item);
            return level;
        }
        */
        return removeData(item, enchantment);
    }

    /**
     * Removes the specified UberEnchantment from the Enchanted Book
     *
     * @param enchantment - The enchantment to remove
     * @param book        - The book
     */
    public static int removeStoredEnchantment(UberEnchantment enchantment, ItemStack book) {
        if (!book.getType().equals(Material.ENCHANTED_BOOK))
            return 0;
        return removeStoredData(book, enchantment);
    }

    /**
     * Extracts the specified UberEnchantment from the item and puts it as a
     * stored enchantment on an enchanted book.
     *
     * @param enchantment - The enchantment to extract
     * @param item        - The item
     * @return An enchanted book itemstack containing the extracted enchantment
     */
    public static ItemStack extractEnchantment(UberEnchantment enchantment, ItemStack item) {
        if (item.hasItemMeta() && enchantment.containsEnchantment(item)) {
            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
            /* Minecraft 1.20.2
            UberUtils.removeEnchantmentLore(item);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
            meta.addStoredEnchant(enchantment, UberEnchantment.getLevel(item, enchantment), true);
            book.setItemMeta(meta);
            UberUtils.addEnchantmentLore(item);
            UberUtils.addEnchantmentLore(book);
            */
            addStoredEnchantment(enchantment, book, enchantment.getLevel(item));
            return book;
        } else {
            return null;
        }
    }

    /**
     * Adds custom enchantment lore to the specified item if it contains any
     * custom enchantments.<br>
     * (An example would be "Sharpness V", if sharpness was custom)
     *
     * @param item - The item
     */
    public static void addEnchantmentLore(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (meta.hasLore())
            lore = meta.getLore();
        Map<UberEnchantment, Integer> enchantments = item.getType() == Material.ENCHANTED_BOOK ? getStoredMap(item) : getMap(item);
        /* Minecraft 1.20.2
        if (item.getItemMeta() instanceof EnchantmentStorageMeta)
            enchantments = UberEnchantment.getStoredEnchantments(item);
        */
        List<String> effects = enchantments.entrySet().stream()
                .filter(data ->
                        !(BoolTag.HIDDEN.test(item, data.getKey())))
                .map(data -> {
                    UberEnchantment enchantment = data.getKey();
                    Integer level = data.getValue();
                    Integer duration = data.getValue();
                    if (IntTag.DURATION.has(item, enchantment))
                        duration = UberMeta.DURATION.get(item, enchantment);
                    if (duration == null)
                        duration = 0;
                    return displayName(data.getKey(), level, duration);
                }).toList();
        //effects.stream().sorted().toList()
        lore.addAll(0, effects);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    /**
     * Removes any custom enchantment lore on the specified item
     *
     * @param item - The item
     */
    public static void removeEnchantmentLore(ItemStack item) {
        update(item, uberEnchantment);
        update(item, storedUberEnchantment);
        ItemMeta meta = item.getItemMeta();
        int n = offset(item);
        if (meta.hasLore() && n > 0) {
            List<String> lore = meta.getLore();
            if (n <= lore.size())
                lore.subList(0, n).clear();
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    /**
     * Constructs the custom enchantment lore.<br>
     * (Formatted as {@code "<name> <level>"} ie "Sharpness V")
     *
     * @param enchantment - The enchantment
     * @param level       - ENchantment level
     * @return A string with the formatted display name
     */
    public static String displayName(UberEnchantment enchantment, int level) {
        return displayName(enchantment, level, level);
    }

    public static String displayName(UberEnchantment enchantment, int level, int duration) {
        if (enchantment instanceof EffectEnchantment e)
            return ChatUtils.color(format(enchantment.getDisplayName(), level, duration * 20));
        return ChatUtils.color(enchantment.getDisplayName() + " " + toRomanNumeral(level));
    }

    /**
     * Gets the offset (if any) required for custom enchantment lore.
     *
     * @param item - The item
     * @return Amount of offset or 0 in case of no enchantments
     */
    public static int offset(ItemStack item) {
        /* Minecraft 1.20.2
        if (item.getItemMeta() instanceof EnchantmentStorageMeta)
            return UberEnchantment.getStoredEnchantments(item).size();
        */
        return item.getType() == Material.ENCHANTED_BOOK ? getStoredMap(item).size() : (int) getMap(item).keySet().stream()
                .filter(enchantment -> !BoolTag.HIDDEN.test(item, enchantment)).count();
    }

    /**
     * Simple Integer to Roman numeral function.
     *
     * @param number - The number to convert
     * @return the converted number as a String or an empty String if number is less than 1
     */
    public static String toRomanNumeral(int number) {
        if (number < 1) {
            return "";
        }
        TreeMap<Integer, String> map = new TreeMap<>();
        map.put(1000000, "(M)");
        map.put(900000, "(C)(M)");
        map.put(500000, "(D)");
        map.put(400000, "(C)(D)");
        map.put(100000, "(C)");
        map.put(90000, "(X)(C)");
        map.put(50000, "(L)");
        map.put(40000, "(X)(L)");
        map.put(10000, "(X)");
        map.put(9000, "M(X)");
        map.put(5000, "(V)");
        map.put(4000, "M(V)");
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");
        int l = map.floorKey(number);
        if (number == l) {
            return map.get(number);
        }
        return map.get(l) + toRomanNumeral(number - l);
    }

    public static String format(String name, int amplifier, int ticks) {
        double seconds = ticks / 20.0;
        int minutes = (int) (seconds / 60);
        int hours = minutes / 60;
        int days = hours / 24;
        String s = Double.toString(seconds % 60);
        String a = (seconds % 60.0 < 10 ? "0" : "") + s.substring(0, Math.min(s.length(), 5));
        String roman = toRomanNumeral(amplifier);
        if (days > 0)
            return String.format("%1$s %2$s (%3$sd %4$sh %5$sm %6$ss)", name, roman, days, hours % 24, minutes % 60, a);
        if (hours > 0)
            return String.format("%1$s %2$s (%3$sh %4$sm %5$ss)", name, roman, hours % 24, minutes % 60, a);
        if (minutes > 0)
            return String.format("%1$s %2$s (%3$sm %4$ss)", name, roman, minutes % 60, a);
        return String.format("%1$s %2$s (%3$ss)", name, roman, a);
    }

    private static void update(ItemStack item, NamespacedKey namespaced) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = getPDC(item, namespaced);
        if (data == null)
            return;
        getCustomMap(item, namespaced).forEach((k, v) -> {
            if (data.has(k.getKey(), PersistentDataType.INTEGER))  {
                data.remove(k.getKey());
                PersistentDataContainer mdata = data.getAdapterContext().newPersistentDataContainer();
                mdata.set(generateKey("level"), PersistentDataType.INTEGER, v);
                data.set(k.getKey(), TAG, mdata);
            }
        });
        meta.getPersistentDataContainer().set(namespaced, TAG,  data);
        item.setItemMeta(meta);
    }
}
