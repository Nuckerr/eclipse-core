package gg.eclipsemc.eclipsecore.listener;

import gg.eclipsemc.eclipsecore.EclipseCore;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListListener implements Listener {

    EclipseCore eclipseCore;

    public PlayerListListener(EclipseCore eclipseCore) {
        this.eclipseCore = eclipseCore;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(eclipseCore, () -> eclipseCore.refreshPlayerList());
    }

}
