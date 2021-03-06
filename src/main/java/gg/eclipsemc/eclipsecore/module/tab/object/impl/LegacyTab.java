package gg.eclipsemc.eclipsecore.module.tab.object.impl;

import de.leonhard.storage.Yaml;
import gg.eclipsemc.eclipsecore.module.tab.TabModule;
import gg.eclipsemc.eclipsecore.module.tab.object.Tab;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Nucker
 */
public class LegacyTab implements Tab {

    private String playerListHeader;
    private String playerListFooter;
    private String tabName;

    private final TabModule module;

    public LegacyTab(TabModule module) {
        this.module = module;
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
        String configHeader = getConfig().getString("legacy.header");
        String configFooter = getConfig().getString("legacy.footer");
        getLogger().log(Level.INFO, "Got tab header " + configHeader);
        getLogger().log(Level.INFO, "Got tab footer " + configFooter);
        if (configHeader == null || configFooter == null) {
            playerListHeader = "";
            playerListFooter = "";
            return;
        }
        playerListHeader = configHeader;
        playerListFooter = configFooter;
        tabName = getConfig().getString("legacy.playername");
    }

    @Override
    public void refreshTabList(final Player player) {
        player.sendPlayerListHeaderAndFooter(getHeader(player), getFooter(player));
        player.playerListName(Component.text(PlaceholderAPI.setPlaceholders(player, getName())));
    }

    private Yaml getConfig() {
        return module.getConfig();
    }

    private Logger getLogger() {
        return module.getLogger();
    }

}
