package me.sciguymjm.uberenchant.utils.enchanting;

import me.sciguymjm.uberenchant.utils.VersionUtils;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UberAnvil {

    private PrepareAnvilEvent event;

    public UberAnvil(PrepareAnvilEvent event) {
        this.event = event;
    }

    private <C, T> T call(C c, Class<T> type, String method, Class<?>[] params, Object... values) {
        if (params.length != values.length)
            return null;
        try {
            T value = null;
            Method m = c.getClass().getMethod(method, params);
            boolean def = m.canAccess(c);
            if (!def)
                m.setAccessible(true);
            if (type != null)
                value = type.cast(m.invoke(c, values));
            else
                m.invoke(c, values);
            m.setAccessible(def);
            return value;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
        return null;
    }

    public void setMaximumRepairCost(int value) {
        if (VersionUtils.isAtLeast("1.21"))
            event.getView().setMaximumRepairCost(value);
        else
            call(event.getInventory(), null, "setMaximumRepairCost", new Class<?>[]{int.class}, value);
    }

    public ItemStack getItem(int slot) {
        if (VersionUtils.isAtLeast("1.21"))
            return event.getView().getTopInventory().getItem(slot);
        else
            return event.getInventory().getItem(slot);
    }

    public void setRepairCost(int cost) {
        if (VersionUtils.isAtLeast("1.21"))
            event.getView().setRepairCost(cost);
        else
            call(event.getInventory(), null, "setRepairCost", new Class<?>[]{int.class}, cost);
    }

    public String getRenameText() {
        if (VersionUtils.isAtLeast("1.21"))
            return event.getView().getRenameText();
        else
            return call(event.getInventory(), String.class, "getRenameText", new Class[0]);
    }
}
