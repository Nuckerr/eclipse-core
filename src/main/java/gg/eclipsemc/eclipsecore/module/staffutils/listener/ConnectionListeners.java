// Decompiled with: FernFlower
// Class Version: 16
package gg.eclipsemc.eclipsecore.module.staffutils.listener;

import gg.eclipsemc.eclipsecore.module.staffutils.StaffUtilsModule;
import gg.eclipsemc.eclipsecore.module.staffutils.packet.StaffJoinPacket;
import gg.eclipsemc.eclipsecore.module.staffutils.packet.StaffQuitPacket;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListeners implements Listener {
    private final StaffUtilsModule module;

    public ConnectionListeners(StaffUtilsModule module) {
        this.module = module;
    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void handleLogin(PlayerLoginEvent event) {
        EclipsePlayer player = EclipsePlayer.getPlayerFromBukkit(event.getPlayer());
        if (player.getPlayerData().getString("lastServer") != null) {
            player.getPlayerData().set("isJoinAlert", true);
        }

    }

    @EventHandler(
            priority = EventPriority.MONITOR
    )
    public void handleStaffJoinMessages(PlayerJoinEvent event) {
        EclipsePlayer player = EclipsePlayer.getPlayerFromBukkit(event.getPlayer());
        if (player.getBukkitPlayer().hasPermission("eclipsecore.staffutils.staff-messages")) {
            System.out.println(player.getPlayerData().getBool("isJoinAlert"));
            this.module.sendPacket(new StaffJoinPacket(player, this.module));
        }

        player.getPlayerData().set("isJoinAlert", false);
        player.getPlayerData().set("lastServer", this.module.getServerName());
    }

    @EventHandler
    public void handlePlayerQuit(PlayerQuitEvent event) {
        EclipsePlayer player = EclipsePlayer.getPlayerFromBukkit(event.getPlayer());
        if (player.getBukkitPlayer().hasPermission("eclipsecore.staffutils.staff-messages")) {
            this.module.sendPacket(new StaffQuitPacket(player, this.module));
        }

        player.getPlayerData().set("lastServer", "null");
    }
}
