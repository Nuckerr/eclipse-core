package gg.eclipsemc.eclipsechat.listener;

import gg.eclipsemc.eclipsechat.EclipseChat;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListListener implements Listener {

    EclipseChat eclipseChat;

    public PlayerListListener(EclipseChat eclipseChat) {
        this.eclipseChat = eclipseChat;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(eclipseChat, () -> eclipseChat.refreshPlayerList());
    }

}
