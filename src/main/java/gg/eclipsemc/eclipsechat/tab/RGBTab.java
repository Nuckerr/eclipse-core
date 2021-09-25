package gg.eclipsemc.eclipsechat.tab;

import gg.eclipsemc.eclipsechat.EclipseChat;
import gg.eclipsemc.eclipsechat.objects.Tab;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Nucker
 */
public class RGBTab implements Tab {

    private String playerListHeader;
    private String playerListFooter;
    private String tabName;

    private final List<Player> players;

    public RGBTab() {
        this.players = new ArrayList<>();

        String configHeader = getConfig().getString("tab.header");
        String configFooter = getConfig().getString("tab.footer");
        getLogger().log(Level.INFO, "Got tab header " + configHeader);
        getLogger().log(Level.INFO, "Got tab footer " + configFooter);
        if (configHeader == null || configFooter == null) {
            playerListHeader = "";
            playerListFooter = "";
            return;
        }
        playerListHeader = configHeader;
        playerListFooter = configFooter;
        tabName = getConfig().getString("tab.playername");

        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onPlayerLeave(PlayerQuitEvent event) {
                for (final Player player : players) {
                    if(player.getUniqueId().equals(event.getPlayer().getUniqueId())) {
                        players.remove(player);
                        return;
                    }
                }
            }

        }, JavaPlugin.getPlugin(EclipseChat.class));
    }

    @Override
    public Component getHeader(final Player player) {
        return MiniMessage.get().parse(PlaceholderAPI.setPlaceholders(player, playerListHeader));
    }

    @Override
    public Component getFooter(final Player player) {
        return MiniMessage.get().parse(PlaceholderAPI.setPlaceholders(player, playerListFooter));
    }

    @Override
    public String getName() {
        return tabName;
    }

    @Override
    public void reloadPlayerList() {
        String configHeader = getConfig().getString("tab.header");
        String configFooter = getConfig().getString("tab.footer");
        getLogger().log(Level.INFO, "Got tab header " + configHeader);
        getLogger().log(Level.INFO, "Got tab footer " + configFooter);
        if (configHeader == null || configFooter == null) {
            playerListHeader = "";
            playerListFooter = "";
            return;
        }
        playerListHeader = configHeader;
        playerListFooter = configFooter;
        tabName = getConfig().getString("tab.playername");
        for (final Player player : players) {
            player.sendPlayerListHeaderAndFooter(getHeader(player), getFooter(player));
            if (tabName == null) return;
            player.playerListName(Component.text(PlaceholderAPI.setPlaceholders(player, tabName)));
        }
    }

    @Override
    public void loadTabList(final Player player) {
        player.sendPlayerListHeaderAndFooter(getHeader(player), getFooter(player));
        player.playerListName(Component.text(PlaceholderAPI.setPlaceholders(player, getName())));
        players.add(player);
    }

    private FileConfiguration getConfig() {
        return EclipseChat.getPlugin(EclipseChat.class).getConfig();
    }

    private Logger getLogger() {
        return EclipseChat.getPlugin(EclipseChat.class).getLogger();
    }
}
