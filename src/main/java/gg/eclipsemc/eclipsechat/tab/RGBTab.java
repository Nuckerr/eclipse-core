package gg.eclipsemc.eclipsechat.tab;

import gg.eclipsemc.eclipsechat.EclipseChat;
import gg.eclipsemc.eclipsechat.objects.Tab;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Nucker
 */
public class RGBTab implements Tab {

    private String playerListHeader;
    private String playerListFooter;
    private String tabName;

    private EclipseChat eclipseChat;

    public RGBTab(EclipseChat eclipseChat) {
        this.eclipseChat = eclipseChat;
        reloadPlayerList();
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
    }

    @Override
    public void refreshTabList(final Player player) {
        player.sendPlayerListHeaderAndFooter(getHeader(player), getFooter(player));
        player.playerListName(Component.text(PlaceholderAPI.setPlaceholders(player, getName())));
    }

    private FileConfiguration getConfig() {
        return eclipseChat.getConfig();
    }

    private Logger getLogger() {
        return eclipseChat.getLogger();
    }
}
