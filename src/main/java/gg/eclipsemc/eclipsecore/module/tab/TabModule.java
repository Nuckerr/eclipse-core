package gg.eclipsemc.eclipsecore.module.tab;

import com.viaversion.viaversion.api.Via;
import gg.eclipsemc.eclipsecore.EclipseCore;
import gg.eclipsemc.eclipsecore.EclipseModule;
import gg.eclipsemc.eclipsecore.module.tab.object.Tab;
import gg.eclipsemc.eclipsecore.module.tab.object.impl.RGBTab;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Logger;

public class TabModule extends EclipseModule {

    private Tab rgbTab;
    private Tab legacyTab;

    public TabModule(final EclipseCore eclipseCore) {
        super(eclipseCore);
    }

    @Override
    public String getName() {
        return "TabModule";
    }

    @Override
    public String getConfigName() {
        return "tab.yml";
    }

    @Override
    public void onEnable() {
        rgbTab = new RGBTab(this);
        legacyTab = new RGBTab(this);
        scheduleRepeatingAsync(() -> Bukkit.getOnlinePlayers().forEach(this::refreshPlayerList), 0L, 200L);
        super.onEnable();
    }

    @Override
    public void onReload() {
        rgbTab.reloadPlayerList();
        legacyTab.reloadPlayerList();
        super.onReload();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        runAsync(this::refreshPlayerList);
    }

    public void refreshPlayerList() {
        Bukkit.getOnlinePlayers().forEach(this::refreshPlayerList);
    }

    public void refreshPlayerList(Player player) {
        if (Via.getAPI().getPlayerVersion(player.getUniqueId()) >= 735 /* 1.16 */) {
            rgbTab.refreshTabList(player);
        } else {
            legacyTab.refreshTabList(player);
        }
    }

    public Logger getLogger() {
        return this.eclipseCore.getLogger();
    }

}
