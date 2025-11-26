package me.sciguymjm.uberenchant.utils.plugins;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

public class ProtocolLibUtils extends PluginUtils {

    private static ProtocolLibUtils instance;
    private static ProtocolManager manager;

    protected ProtocolLibUtils() {
        super("ProtocolLib");
        if (pluginLoaded)
            manager = ProtocolLibrary.getProtocolManager();
        instance = this;
    }

    public static ProtocolLibUtils instance() {
        if (instance == null)
            instance = new ProtocolLibUtils();
        return instance;
    }

    public static boolean isLoaded() {
        return instance.isPluginLoaded();
    }

    public static void setBypass(Player player, boolean bypass) {
        if (!isLoaded())
            return;
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.ABILITIES);
        packet.getFloat()
                .write(0, player.getFlySpeed() / 2)
                .write(1, player.getWalkSpeed() / 2);
        packet.getBooleans()
                .write(0, player.isInvulnerable())
                .write(1, player.isFlying())
                .write(2, player.getAllowFlight())
                .write(3, bypass);
        manager.sendServerPacket(player, packet);
    }
}
