package gg.eclipsemc.eclipsecore.module.tab.object;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * @author Nucker
 */
public interface Tab {

    Component getHeader(Player player);

    Component getFooter(Player player);

    String getName();

    void reloadPlayerList();

    void refreshTabList(Player player);

}
