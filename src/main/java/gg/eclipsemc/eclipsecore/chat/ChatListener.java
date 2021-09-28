package gg.eclipsemc.eclipsecore.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        /*
        PlayerData playerData = AquaCore.INSTANCE.getPlayerManagement().getPlayerData(e.getPlayer().getUniqueId());
        if(!playerData.getMessageSystem().isGlobalChat()) {
            e.getPlayer().sendMessage(MiniMessage.get().parse("<click:run_command:/settings><red>You currently have Global Chat disabled. Click here to open the settings menu.</click>"));
            e.setCancelled(true);
            return;
        }
         */
        /*
        for (final Audience viewer : e.viewers()) {
            if(viewer instanceof Player player) {
                PlayerData data = AquaCore.INSTANCE.getPlayerManagement().getPlayerData(player.getUniqueId());

                if(!data.getMessageSystem().isGlobalChat())
                    e.viewers().remove(viewer);
                if(data.getMessageSystem().isIgnoring(e.getPlayer().getName()))
                    e.viewers().remove(viewer);
            }
        }
         */
        e.renderer(new EclipseChatRenderer());
    }

}
