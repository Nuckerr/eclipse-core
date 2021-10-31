package gg.eclipsemc.eclipsecore.module.chat.listener;

import gg.eclipsemc.eclipsecore.module.chat.ChatRenderer;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        EclipsePlayer player = EclipsePlayer.getPlayerFromBukkit(e.getPlayer());
        if(!player.getPlayerData().getBool("chatToggled")) {
            player.sendMiniMessage("<click:run_command:/settings><red>You currently have Global Chat disabled. Click here to open the settings menu.</click>");
            e.setCancelled(true);
            return;
        }

        List<Audience> audienceToRemove = new ArrayList<>();
        for (final Audience viewer : e.viewers()) {
            if(viewer instanceof Player bukkitAudience) {
                EclipsePlayer audience = EclipsePlayer.getPlayerFromBukkit(bukkitAudience);
                if(!audience.getPlayerData().getBool("chatToggled")) {
                   audienceToRemove.add(viewer);
                }
                if(audience.getPlayerData().getListOf("ignoreList", UUID.class).contains(player.getUniqueId())) {
                    audienceToRemove.add(viewer);
                }
            }
        }
        for (final Audience audience : audienceToRemove) {
            e.viewers().remove(audience);
        }
        e.renderer(new ChatRenderer());
    }

}
