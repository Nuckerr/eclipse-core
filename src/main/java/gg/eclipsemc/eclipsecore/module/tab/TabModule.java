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
    public void enable() {
        rgbTab = new RGBTab(eclipseCore);
        legacyTab = new RGBTab(eclipseCore);
        scheduleRepeatingAsync(() -> Bukkit.getOnlinePlayers().forEach(this::refreshPlayerList), 0L, 200L);
        super.enable();
    }

    @Override
    public void reload() {
        rgbTab.reloadPlayerList();
        legacyTab.reloadPlayerList();
        super.reload();
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

}
