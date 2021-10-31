package gg.eclipsemc.eclipsecore.module.chat.listener;

import gg.eclipsemc.eclipsecore.module.chat.ChatModule;
import gg.eclipsemc.eclipsecore.module.chat.ChatRenderer;
import gg.eclipsemc.eclipsecore.object.EclipsePlayer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatListener implements Listener {

    private final ChatModule module;

    public ChatListener(ChatModule module) {
        this.module = module;
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        if(module.getSlowModeCooldown().contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Component.text("You are currently on chat slowmode").color(NamedTextColor.RED));
            return;
        }

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

        module.addToCache(player.getUniqueId());
    }

}
